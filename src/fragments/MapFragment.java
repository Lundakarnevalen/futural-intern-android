package fragments;

import java.util.Calendar;
import java.util.List;

import json.MapGet;
import json.MapPost;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class MapFragment extends LKFragment implements SensorEventListener {
	SensorManager sm;
	private Sensor mAccelerometer;
	float lastX = -1000;
	float lastY = -1000;

	float totX;
	float totY;

	private PointF start = new PointF();
	private PointF mid = new PointF();

	ImageView background;

	private long lastUpdate;

	// Pre pic.
	private Bitmap bmOverlay;

	private static final int HANDLER_DELAY = 1800000; // 30min
	// private static final int HANDLER_DELAY = 10000; //10sec
	private static final int GET_HANDLER_DELAY = 1800000; // 30min
	// private static final int GET_HANDLER_DELAY = 10000; //30min

	private Context context;

	private Handler getHandler;

	private Handler handler;

	private LocationManager locMan;

	private static final int TIME_INTERVAL = 1800000; // get gps location every
														// 30 min
	// private static final int TIME_INTERVAL = 10000; // get gps location every
	// 30 min

	private static final int GPS_DISTANCE = 0; // set the distance value in
												// meter

	private final String token = "P6VmxzvTypzP3qb3TEW7";

	private final String SHARED_ID = "SHAREDID";

	private float maxDotSize = 45;
	private float minDotSize = 15;
	private float diffDotSize = maxDotSize - minDotSize;
	private float startLonMap = (float) 12.445449839578941;
	private float startLatMap = (float) 55.33715099913018;
	private float endLonMap = (float) 14.580917368875816;
	private float endLatMap = (float) 56.52300194685981;
	private float diffLon = endLonMap - startLonMap;
	private float diffLat = endLatMap - startLatMap;

	private int clusterId = -1;
	private int nbrOfPersons;

	private final String key_cluster = "key_cluster_id";

	private List<Position> positions;

	private ImageView img;
	private boolean gpsOn = true;

	// Every time you switch to this fragment.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_map, null);
		totX = 0;
		totY = 0;

		background = (ImageView) rootView.findViewById(R.id.map_test_move);

		context = getContext();

		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (sm == null) {
			Log.d("sm NUll", "ff");
		} else {
			Log.d("Okey!", " ");
			mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		}

		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		View root = actionBar.getCustomView();
		RelativeLayout gpsCheckbox = (RelativeLayout) root.findViewById(R.id.info_pic);
		gpsCheckbox.setOnClickListener(gpsCheckboxListener);

		se.lundakarnevalen.widget.LKTextViewBold text = (se.lundakarnevalen.widget.LKTextViewBold) rootView.findViewById(R.id.nbr_of);
		// TODO change to english!
		text.setText("" + nbrOfPersons);

		img = (ImageView) rootView.findViewById(R.id.map_id);
		if (bmOverlay != null) {
			((ImageView) rootView.findViewById(R.id.map_id)).setImageBitmap(bmOverlay);
		}
		if (clusterId == -1) {
			SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, Context.MODE_PRIVATE);
			clusterId = prefs.getInt(key_cluster, -1);
		}
		Log.d("CLUSTER_ID", "" + clusterId);

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
			}, HANDLER_DELAY); // START_HANDLER_DELAY
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
			}, GET_HANDLER_DELAY); // START_HANDLER_DELAY
		}
		if (positions == null || positions.size() == 0) {
			getPositions();
		} else if (System.currentTimeMillis() - lastUpdate > 10000) { // 10 sec
																		// spam
																		// control.
			Log.d("Spam control", "no spam");
			getPositions();
		} else {
			Log.d("Spam control", "SPAM");
		}
		lastUpdate = System.currentTimeMillis();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.karta));
		// TODO
		// Fix both eng and swe.
	}

	@Override
	public void onPause() {
		sm.unregisterListener(this);
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);// TODO
																													// Auto-generated
																													// method
																													// stub
		super.onResume();
	}

	/**
	 * Send current lat/lng, id and section to the database.
	 * 
	 */
	private boolean sendPosition() {
		Log.d("GPS on:", "" + gpsOn);
		if (locMan == null) {
			locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
		// Only turn off get position with GPS. Ok with network...
		if (gpsOn && locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// if(isLocationListener){
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
			Log.d("Updateing GPS!", "Update");
			// }
		} else {
			Log.d("GPS off", "Avstängd GPS");
			if (locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
				Log.d("Updateing Position with network!", "Update");
			}
		}

		float lng;
		float lat;
		Location location = null;
		location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
			Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_skane);

			// Create an overlay bitmap
			bmOverlay = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
			Canvas canvas = new Canvas();
			canvas.setBitmap(bmOverlay);
			canvas.drawBitmap(mapBitmap, new Matrix(), null);

			// TODO
			// Change color to correct red
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
			for (Position p : positions) {
				float lat = (p.lat - startLatMap) / diffLat;
				float lon = (p.lng - startLonMap) / diffLon;
				float cur = minDotSize + ((p.quantity - smallestDot) / difference) * diffDotSize;

				float x = lon * mapBitmap.getWidth();
				float y = mapBitmap.getHeight() - lat * mapBitmap.getHeight();
				canvas.drawCircle(x, y + 6, cur, paintGray);
				// TODO Delete gray ?
				canvas.drawCircle(x, y, cur, paintRed);
			}
			se.lundakarnevalen.widget.LKTextViewBold text = (se.lundakarnevalen.widget.LKTextViewBold) getView().findViewById(R.id.nbr_of);
			// TODO change to english!
			text.setText("" + nbrOfPersons);
			img.setImageBitmap(bmOverlay);
		}
	}

	private void getPositions() {
		// TODO
		// TOKEN ???
		LKRemote remote = new LKRemote(context, new GetListener());
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

		if (clusterId == -1) {
			remote.requestServerForText("api/clusters", js, LKRemote.RequestType.POST, false);
		} else {
			// Står fel i mail, ska vara put istället för post....
			remote.requestServerForText("api/clusters" + "/" + clusterId, js, LKRemote.RequestType.PUT, false);
		}
	}

	private View.OnClickListener gpsCheckboxListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loadFragment(new InfoTextFragment(), false);
			// TODO start fragment
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

			if (checker.success) {
				Clusters clusters = gson.fromJson("{\"clusters\":" + checker.clusters + "}", Clusters.class);
				Log.d("GetListener sucess:", "true, size:" + clusters.clusters.size());
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
				// TODO
				Log.d("PostListener sucess:", "false");
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

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		synchronized (this) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				float x = (float) event.values[0];
				float y = (float) event.values[1];
				float z = (float) event.values[2];

				Matrix matrix = new Matrix();
				background.setScaleType(ImageView.ScaleType.MATRIX);
				matrix.set(background.getImageMatrix());
				float multFactor = 5;
				if (lastX == -1000) {

					if (x > 4) {
						x = 4;

					} else if (x < -4) {
						x = -4;
					}
					if (y < 0) {
						y = 0;

					} else if (y > 9) {
						y = 9;
					}

					// // 7 mitten

					matrix.postTranslate(x * multFactor, -(y - 7) * multFactor);

					lastX = x;
					lastY = y;

				} else {
					/*
					 * if(lastX < x && totX < 8) { Log.d("Heere","happens"); //
					 * move right totX+=1; matrix.postTranslate(1, 0);
					 * Log.d("TotX",""+totX); } else if(totX > -8){ totX -= 1;
					 * matrix.postTranslate(-1, 0); //move left } if(lastY < y
					 * && totY > -8) { totY -= 1; matrix.postTranslate(0, -1);
					 * // move down } else if(totY < 8){ totY += 1;
					 * matrix.postTranslate(0, 1); //move up }
					 */

					if (x > 4) {
						x = 4;

					} else if (x < -4) {
						x = -4;
					}
					if (y < 0) {
						y = 0;

					} else if (y > 9) {
						y = 9;
					}

					float resX = 0;
					float resY = 0;
					if (lastX < x) {
						resX = Math.abs(x - lastX);
						// move right
						// totX+=1;
						if (resX < 0.15) {
							resX = 0;
						} else {
							lastX = x;
						}
						// Log.d("TotX",""+totX);
					} else {
						// totX -= 1;
						resX = Math.abs(lastX - x);
						if (resX < 0.15) {
							resX = 0;
						} else {
							resX = -resX;
							lastX = x;
						}
						// move left
					}
					if (lastY < y) {
						resY = Math.abs(y - lastY);
						if (resY < 0.15 || z < 0) {
							resY = 0;
						} else {
							resY = -resY;
							lastY = y;
						}
						// matrix.postTranslate(0, -1);
						// move down
					} else {
						resY = Math.abs(lastY - y);
						// matrix.postTranslate(0, 1);
						// move up
						if (resY < 0.15 || z < 0) {
							resY = 0;
						} else {
							lastY = y;
						}

					}

					matrix.postTranslate(resX * multFactor, resY * multFactor);

				}
				background.setImageMatrix(matrix); // display the transformation
													// on screen

				break;
			}
		}
	}

	/*
	 * private void updateSpritePosition() { if ((accellerometerSpeedX != 0) ||
	 * (accellerometerSpeedY != 0)) { // Set the Boundary limits int tL = 0; int
	 * lL = 0; int rL = CAMERA_WIDTH - (int)sprite.getWidth(); int bL =
	 * CAMERA_HEIGHT - (int)sprite.getHeight();
	 * 
	 * // Calculate New X,Y Coordinates within Limits if (sX >= lL) sX +=
	 * accellerometerSpeedX; else sX = lL; if (sX <= rL) sX +=
	 * accellerometerSpeedX; else sX = rL; if (sY >= tL) sY +=
	 * accellerometerSpeedY; else sY = tL; if (sY <= bL) sY +=
	 * accellerometerSpeedY; else sY = bL;
	 * 
	 * // Double Check That New X,Y Coordinates are within Limits if (sX < lL)
	 * sX = lL; else if (sX > rL) sX = rL; if (sY < tL) sY = tL; else if (sY >
	 * bL) sY = bL;
	 * 
	 * sprite.setPosition(sX, sY); } }
	 */

}
