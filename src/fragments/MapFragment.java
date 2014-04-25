package fragments;

import java.util.Calendar;
import java.util.List;

import json.MapGet;
import json.MapPost;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class MapFragment extends LKFragment implements SensorEventListener {

	// for parallax effect
	private SensorManager sm;
	private InfoTextFragment infoTextFragment;
	private float lastX = -1000;
	private float lastY = -1000;
	private ImageView background;	

	// spam controll
	private long lastUpdate;

	// Save current dots
	private Bitmap bmOverlay;
	private ImageView img;
	private int imageWidth;
	private int imageHeight;

	// Handler for post/get position(s)
	private static final int HANDLER_DELAY = 1800000; // 30 min
	// private static final int HANDLER_DELAY = 10000; //10 sec
	private static final int GET_HANDLER_DELAY = 1800000; // 30 min
	// private static final int GET_HANDLER_DELAY = 10000; //10 sec	
	private Handler getHandler;
	private Handler handler;

	// Context
	private Context context;

	// For gps and network
	private LocationManager locMan;
	private static final int TIME_INTERVAL = 1800000; // get gps location every 30 min
	// private static final int TIME_INTERVAL = 10000; // get gps location every 10 sec
	private static final int GPS_DISTANCE = 0; // set the distance value in meter

	// For cluster
	private String token = null;
	private final String SHARED_ID = "SHAREDID";
	private final String key_cluster = "key_cluster_id";
	private int clusterId = -1;

	// Information about the map
	private float maxDotSize = 45;
	private float minDotSize = 15;
	private float diffDotSize = maxDotSize - minDotSize;
	private float startLonMap = (float) 12.445449839578941;
	private float startLatMap = (float) 55.33715099913018;
	private float endLonMap = (float) 14.580917368875816;
	private float endLatMap = (float) 56.52300194685981;
	private float diffLon = endLonMap - startLonMap;
	private float diffLat = endLatMap - startLatMap;
	private int nbrOfPersons;

	// All positions
	private List<Position> positions;

	// Every time you switch to this fragment.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_map, null);

		if(infoTextFragment == null) {
			infoTextFragment = new InfoTextFragment();
			infoTextFragment.setMapFragment(this);
		}


		background = (ImageView) rootView.findViewById(R.id.map_move);
		context = getContext();
		
			if(LKUser.localUserStored(context)) {
				LKUser user = new LKUser(context);
				user.getUserLocaly();
				if(user.token!=null) {
					token = user.token;
					Log.d("MapFragment","Get token: "+user.token);
				} else {
					Log.d("MapFragment","Get token: "+user.token);
					token = "";
				}
			
			}


		if(imageWidth == 0) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			imageWidth = metrics.widthPixels;
			imageHeight = metrics.heightPixels;
		}

		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if(sm == null) {
			// try do get sensor
		} else {
			sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		}

		// add info button
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		View root = actionBar.getCustomView();
		RelativeLayout infoMark = (RelativeLayout) root.findViewById(R.id.info_pic);	
		infoMark.setOnClickListener(infoMarkListener);

		se.lundakarnevalen.widget.LKTextViewBold text = (se.lundakarnevalen.widget.LKTextViewBold) rootView.findViewById(R.id.nbr_of);
		text.setText(""+nbrOfPersons);

		img = (ImageView) rootView.findViewById(R.id.map_id);
		if (bmOverlay != null) {
			((ImageView) rootView.findViewById(R.id.map_id)).setImageBitmap(bmOverlay);
		}
		if (clusterId == -1) {
			SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, Context.MODE_PRIVATE);
			clusterId = prefs.getInt(key_cluster, -1);
		}

		if (handler == null) {
			handler = new Handler();

			final Calendar end = Calendar.getInstance();
			end.set(2014, Calendar.APRIL, 13, 06, 00, 00);

			handler.postDelayed(new Runnable() {
				public void run() {
					Calendar c = Calendar.getInstance();
					if (c.compareTo(end) == 1) {
						return;
					}
					sendPosition();
					handler.postDelayed(this, HANDLER_DELAY);
				}
			}, 0); 
		}

		if (getHandler == null) {
			getHandler = new Handler();
			final Calendar end = Calendar.getInstance();
			end.set(2014, Calendar.APRIL, 13, 06, 00, 00);

			getHandler.postDelayed(new Runnable() {
				public void run() {
					Calendar c = Calendar.getInstance();
					if (c.compareTo(end) == 1) {
						return;
					}
					if (isVisible()) {
						getPositions();
					}
					getHandler.postDelayed(this, GET_HANDLER_DELAY);
				}
			}, GET_HANDLER_DELAY); 
		}
		if (positions == null || positions.size() == 0) {
			getPositions();
		} else if (System.currentTimeMillis() - lastUpdate > 10000) { 
			getPositions();
		} 
		lastUpdate = System.currentTimeMillis();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.karta));
	}

	@Override
	public void onPause() {
		if(sm != null) 
			sm.unregisterListener(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		if(sm != null) 	
			sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * Send current lat/lng, cluster_id and token to the database.
	 * 
	 */
	private boolean sendPosition() {
		if(locMan==null) {
			locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		// Only turn off get position with GPS. Ok with network...
		if(locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
			Log.d("Updateing GPS!", "Update");
		} else {
			Log.d("GPS off", "Avst�ngd GPS");
			if (locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
				Log.d("Updateing Position with network!", "Update");
			}
		}
		float lng;
		float lat;
		Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			lng = (float) location.getLongitude();
			lat = (float) location.getLatitude();
			Log.d("Find GPS_position", lng + " " + lat);
		} else {
			location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				lng = (float) location.getLongitude();
				lat = (float) location.getLatitude();
				Log.d("Find Network_position", lng + " " + lat);
			} else {
				Log.d("No GPS or Network position", "FAIL1");
				return false;
			}
		}

		postPosition(lat, lng);
		return true;
	}

	private void updatePositions(List<Position> positions) {
		if (positions != null && (positions.size() != 0)) {
			float biggestDot = 0;
			float smallestDot = Float.MAX_VALUE;

			// create bitmap of the map
			//BitmapFactory.Options options 
			//Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_skane, BitmapFactory.Options);
			Bitmap mapBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.map_skane, imageWidth , imageHeight);


			//mImageView.setImageBitmap(
			//	    decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));

			//BitmapFactory.de
			// Create an overlay bitmap
			bmOverlay = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
			Canvas canvas = new Canvas();
			canvas.setBitmap(bmOverlay);
			canvas.drawBitmap(mapBitmap, new Matrix(), null);

			Paint paintGray = new Paint();
			Paint paintRed = new Paint();
			paintRed.setColor(getResources().getColor(R.color.red));
			paintGray.setColor(Color.GRAY); 
			nbrOfPersons = 0;
			for (Position p : positions) {
				biggestDot = Math.max(biggestDot, p.quantity);
				smallestDot = Math.min(smallestDot, p.quantity);
				nbrOfPersons += p.quantity;
			}
			float difference = biggestDot - smallestDot;
			if(difference == 0) {
				difference = biggestDot;
				smallestDot = 0;
			}
			for (Position p : positions) {
				float lat = (p.lat - startLatMap) / diffLat;
				float lon = (p.lng - startLonMap) / diffLon;
				float cur = minDotSize + ((p.quantity - smallestDot) / difference) * diffDotSize;

				float x = lon * mapBitmap.getWidth();
				float y = mapBitmap.getHeight() - lat * mapBitmap.getHeight();
				canvas.drawCircle(x, y + 6, cur, paintGray);
				canvas.drawCircle(x, y, cur, paintRed);
			}
			se.lundakarnevalen.widget.LKTextViewBold text = (se.lundakarnevalen.widget.LKTextViewBold) getView().findViewById(R.id.nbr_of);
			text.setText("" + nbrOfPersons);
			img.setImageBitmap(bmOverlay);
		}
	}

	private void getPositions() {
		// TODO
		// TOKEN ???
		LKRemote remote = new LKRemote(context, new GetListener());
		if(remote != null)
			remote.requestServerForText("api/clusters?token=" + token, "", LKRemote.RequestType.GET, false);
	}

	private void postPosition(float lat, float lng) {
		Gson g = new Gson();

		MapPost mp = new MapPost(lat, lng, token);
		String js = g.toJson(mp);
		Log.d("Send json: ", "" + js);

		// TODO
		// TOKEN ???
		Log.d("Cluster id before post: ", "" + clusterId);
		LKRemote remote = new LKRemote(context, new PostListener());
		if(remote != null) {
			if(clusterId == -1 ) {
				remote.requestServerForText("api/clusters", js, LKRemote.RequestType.POST, false);			
			}else {	
				// Error in mail, put instead of post....
				remote.requestServerForText("api/clusters"+"/"+clusterId, js, LKRemote.RequestType.PUT, false);			
			}
		}
	}

	private View.OnClickListener infoMarkListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loadFragment(infoTextFragment, true);
		}
	};

	private class GetListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			if (result == null) {
				Log.d("GetListener get result: ", "null");
				return;
			}
			Log.d("GetListener get result: ", result);
			Gson gson = new Gson();
			MapGet checker = gson.fromJson(result, MapGet.class);
			if(checker.success) {	
				Clusters clusters = gson.fromJson("{\"clusters\":" + checker.clusters+"}", Clusters.class);
				Log.d("GetListener sucess:","true, size:"+clusters.clusters.size());
				positions = clusters.clusters;
				updatePositions(clusters.clusters);
			} else {
				Log.d("GetListener sucess:", "false");
			}
		}
	}

	private class PostListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			if (result == null) {
				Log.d("PostListener get result: ", "null");
				return;
			}
			Log.d("PostListener get result: ", result);

			Gson gson = new Gson();
			ResultPost res = gson.fromJson(result, ResultPost.class);

			if (res.success) {
				Log.d("PostListener sucess:", "true, clusterId:" + clusterId);
				if (clusterId != res.cluster_id) {
					clusterId = res.cluster_id;
					SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, Context.MODE_PRIVATE);
					prefs.edit().putInt(key_cluster, clusterId).commit();
					Log.d("Update clusterId to:", "" + clusterId);
				}
			} else {
				Log.d("PostListener sucess:","false");	
			}
		}
	}

	private class Position {
		private float lat;
		private float lng;
		private int quantity;
	}

	private class Clusters {
		private List<Position> clusters;
	}

	private class ResultPost {
		private boolean success;
		private int cluster_id;
	}

	private LocationListener PositionListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// update location
			locMan.removeUpdates(PositionListener); // remove this listener
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				float x = (float)event.values[0];
				float y = (float)event.values[1];
				float z = (float)event.values[2];
				Matrix matrix = new Matrix();
				background.setScaleType(ImageView.ScaleType.MATRIX);
				matrix.set(background.getImageMatrix());
				float multFactor = (float) 2;
				if(x > 5) {
					x = 5;
				} else if(x < -5) {
					x = -5;
				}
				if(y < 0) {
					y = 0;
				} else if(y > 10) {
					y = 10;
				}
				if(lastX == -1000) {
					// 7 middle
					matrix.postTranslate(x*multFactor, -(y-7)*multFactor);
					lastX = x;
					lastY = y;
				} else {
					float resX = 0;
					float resY = 0;
					if (lastX < x) {
						resX = Math.abs(x - lastX);
						if(resX < 0) {
							resX = 0;
						} else {
							lastX = x;
						}	
					} else {
						resX = Math.abs(lastX - x);
						if(resX <0) {
							resX = 0;
						} else {
							resX = -resX;
							lastX = x;
						}
					}
					if (lastY < y) {
						resY = Math.abs(y - lastY);
						if(resY <0 || z < 0) {
							resY = 0;
						} else {
							resY = -resY;
							lastY = y;
						}			
					} else {
						resY = Math.abs(lastY - y);
						if(resY <0 || z < 0) {
							resY = 0;
						} else {
							lastY = y;
						}
					}
					matrix.postTranslate(resX*multFactor, resY*multFactor);
				}
				background.setImageMatrix(matrix); // display the transformation on screen
				break;
			}
		}
	}
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
}
