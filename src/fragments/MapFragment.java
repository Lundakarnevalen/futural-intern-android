package fragments;

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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;


public class MapFragment extends LKFragment {

	private final String token = "P6VmxzvTypzP3qb3TEW7";
	
	private final String SHARED_ID = "SHAREDID";
	
	private float maxDotSize = 40;
	private float minDotSize = 10;
	private float diffDotSize = maxDotSize - minDotSize;
	private float startLonMap = (float)12.445449839578941;
	private float startLatMap = (float)55.33715099913018;
	private float endLonMap = (float)14.580917368875816;
	private float endLatMap = (float)56.52300194685981;
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
		
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		View root = actionBar.getCustomView();
		RelativeLayout gpsCheckbox = (RelativeLayout) root.findViewById(R.id.gps_checkbox);	
		gpsCheckbox.setOnClickListener(gpsCheckboxListener);
		gpsCheckbox.setVisibility(View.VISIBLE);

		img = (ImageView) rootView.findViewById(R.id.map_id);
		
		SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, getContext().MODE_PRIVATE);
		clusterId = prefs.getInt(key_cluster, -1); 
		Log.d("CLUSTER_ID",""+clusterId);
		
		//TODO 
		// Call updatePositions(positions) instead.

		//TODO 
		// Call every ??
		//sendPosition();
		//getAndUpdatePositions();
		
		//TODO Do this in background..
		// If we dont want to update the position we do updatePositions(positions) in background...
		sendPosition();
		getPositions();
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.karta));
		//TODO
		// Fix both eng and swe.
	}

	/**
	 * Send current lat/lng, id and section to the database.
	 * 
	 */
	private boolean sendPosition() {
		Log.d("GPS on:",""+gpsOn);
		if(gpsOn) {
			float lng;
			float lat;
			LocationManager mlocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE); 
			Location location = null;
			if(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location= mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location!=null) {
					lng = (float)location.getLongitude();
					lat = (float)location.getLatitude();
					Log.d("Find GPS_position",lng +" "+lat);
				} else {
					if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location!=null){
							lng = (float)location.getLongitude();
							lat = (float)location.getLatitude();
							Log.d("Find Network_position",lng +" "+lat);
						} else {
							Log.d("No GPS or Network position","FAIL1");
							return false;
						}
					} else {
						Log.d("No GPS or Network position","FAIL2");
						return false;
					}
				}
			} else {
				if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(location!=null){ 
						lng = (float)location.getLongitude();
						lat = (float)location.getLatitude();
						Log.d("Find Network_position2",lng +" "+lat);
					} else {
						Log.d("No GPS or Network position","FAIL3");
						return false;
					}
				} else {
					Log.d("No GPS or Network position","FAIL4");
					return false;
				}
			}
			postPosition(lat, lng);

			return true;
		} else {
			Log.d("Don´t want to send position.","FAIL5");
			return false;
		}
	}

	private void updatePositions(List<Position> positions) {
		if(positions != null && (positions.size() != 0)) {
			float biggestDot = 0;
			float smallestDot = Float.MAX_VALUE;

			// create bitmap of the map
			Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_skane);

			// Create an overlay bitmap
			Bitmap bmOverlay = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
			Canvas canvas = new Canvas();
			canvas.setBitmap(bmOverlay);
			canvas.drawBitmap(mapBitmap, new Matrix(), null);

			//TODO
			// Change color to correct red
			Paint paintGray = new Paint();
			Paint paintRed = new Paint();
			paintRed.setColor(Color.RED);
			paintGray.setColor(Color.GRAY);
			nbrOfPersons = 0;
			for(Position p: positions)  {
				biggestDot = Math.max(biggestDot, p.quantity);
				smallestDot = Math.min(smallestDot, p.quantity);
				nbrOfPersons += p.quantity;
			}

			float difference = biggestDot - smallestDot; 

			for(Position p: positions)  {
				float lat = (p.lat - startLatMap) / diffLat;
				float lon = (p.lng - startLonMap) / diffLon;
				float cur = minDotSize + ((p.quantity - smallestDot)/difference)*diffDotSize;

				float x = lon*mapBitmap.getWidth();
				float y = mapBitmap.getHeight()-lat * mapBitmap.getHeight();
				canvas.drawCircle(x, y+6, cur ,paintGray); 
				//TODO Delete gray ?
				canvas.drawCircle(x, y, cur ,paintRed);	
			}
			se.lundakarnevalen.widget.LKTextView text = (se.lundakarnevalen.widget.LKTextView) getView().findViewById(R.id.nbr_of);
			//TODO change to english!
			text.setText(nbrOfPersons+getString(R.string.map_karnevalister));
			img.setImageBitmap(bmOverlay);
		}
	}

	/**
	 * Get lat/lng, cluster from the database and update positions.
	 * 
	 */
	private void getAndUpdatePositions() {
		// Connect and get positions...
		/*
		ArrayList<Position> newPositions = new ArrayList<Position>();
		newPositions.add(new Position((float)55.704660,(float)13.191007,65)); //LUND
		newPositions.add(new Position((float)55.374660,(float)13.191007,17));
		newPositions.add(new Position((float)55.374660,(float)13.391007,40));
		newPositions.add(new Position((float)55.934660,(float)13.951007,10));
		*/
		updatePositions(positions);

	}

	private void getPositions() {
		// TODO
		// TOKEN ???		
		LKRemote remote = new LKRemote(getContext(), new GetListener());
		remote.requestServerForText("api/clusters?token=" + token, "", LKRemote.RequestType.GET, false);
	}

	private void postPosition(float lat, float lng) {
		Gson g =  new Gson();
		
		MapPost mp = new MapPost(lat,lng,token);	
		String js = g.toJson(mp);
		Log.d("Send json: ",""+js);
	
		// TODO
		// TOKEN ???
		Log.d("Cluster id before post: ",""+clusterId);
		LKRemote remote = new LKRemote(getContext(), new PostListener());
		if(clusterId == -1) {
			remote.requestServerForText("api/clusters", js, LKRemote.RequestType.POST, false);			
		}else {
			// Står fel i mail, ska vara put istället för post....
			remote.requestServerForText("api/clusters"+"/"+clusterId, js, LKRemote.RequestType.PUT, false);			
		}
	}

	private View.OnClickListener gpsCheckboxListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ImageView iv= (ImageView)v.findViewById(R.id.gps_checkmark);
			if(gpsOn) {
				iv.setVisibility(View.GONE); 
				gpsOn = false;
			} else {
				iv.setVisibility(View.VISIBLE);
				gpsOn = true;
			}
		}
	};

	private class GetListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			if(result == null) {
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
				Log.d("GetListener sucess:","false");
			}
		}
	}

	private class PostListener implements LKRemote.TextResultListener {

		@Override
		public void onResult(String result) {			
			if(result == null) {
				Log.d("PostListener get result: ", "null");
				return;
			}

			Log.d("PostListener get result: ", result);
			
			Gson gson = new Gson();
			ResultPost res = gson.fromJson(result, ResultPost.class);

			if(res.success) {
				Log.d("PostListener sucess:","true, clusterId:"+clusterId);
				if(clusterId != res.cluster_id) {
					clusterId = res.cluster_id;
					SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, getContext().MODE_PRIVATE);
					prefs.edit().putInt(key_cluster, clusterId).commit();
					Log.d("Update clusterId to:",""+clusterId);					
				}
			} else {
				//TODO
				Log.d("PostListener sucess:","false");	
			}
		}
	}
	
	private class Position {
		private float lat;
		private float lng;
		private int quantity;
		public Position(float lat, float lng, int size) {
			this.lat = lat;
			this.lng = lng;
			this.quantity = size;
		}
	}

	private class Clusters {
		private List<Position> clusters;
	}
	
	private class ResultPost {
		private boolean success;
		private int cluster_id;
	}

	private Location getLastKnownLocation() {
		LocationManager mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);

			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {
				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}
}
