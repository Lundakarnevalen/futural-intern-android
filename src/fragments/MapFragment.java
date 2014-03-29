package fragments;

import java.util.ArrayList;
import java.util.List;

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
	private LKRemote remote;

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

	
	private float myLat;
	private float myLng;

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
		remote = new LKRemote(getContext());
		
		SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, getContext().MODE_PRIVATE);
		
		clusterId = prefs.getInt(key_cluster, -1); 
		
		
		/*
		//remote = new LKRemote(getContext(), new ButtonLogin());
		//TODO 
		// Call updatePositions(positions) instead.

		//testCreatePosition((float)55.704660,(float)13.191007);

		//TODO 
		// Call every ??
		//sendPosition();
		Log.d("GPS!:",""+gpsOn);
		 */
		//getAndUpdatePositions();
		//TODO Do this in background..
		
		sendPosition();
		testGetPosition();
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Karta");
		//TODO
		// Fix both eng and swe.
	}

	/**
	 * Send current lat/lng, id and section to the database.
	 * 
	 */
	private boolean sendPosition() {
		Log.d("Går in här: ","På: "+gpsOn);

		if(gpsOn) {
			LocationManager mlocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE); 
			Log.d("denna hiottade: ",":"+getLastKnownLocation());
			Location location = null;
			if(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location= mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location!=null) {
					myLng = (float)location.getLongitude();
					myLat = (float)location.getLatitude();
					Log.d("GPS:Position",myLng +" "+myLat);
				} else {
					if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location!=null){
							myLng = (float)location.getLongitude();
							myLat = (float)location.getLatitude();
							Log.d("Network1:Position",myLng +" "+myLat);
						} else {
							Log.d("Fail1","FAIL1");
							return false;
						}
					} else {
						Log.d("Fail2","FAIL2");
						return false;
					}
				}
			} else {
				if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(location!=null){ 
						myLng = (float)location.getLongitude();
						myLat = (float)location.getLatitude();
						Log.d("Network2:Position",myLng +" "+myLat);
					} else {

						Log.d("Fail3","FAIL3");
						return false;
					}
				} else {

					Log.d("Fail4","FAIL4");
					return false;
				}
			}

			//TODO
			// Send position here!
			testCreatePosition(myLat, myLng);

			return true;
		} else {

			Log.d("Fail5","FAIL5");
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

			Paint paintGray = new Paint();
			Paint paintRed = new Paint();
			paintRed.setColor(Color.RED);
			paintGray.setColor(Color.GRAY);
			nbrOfPersons = 0;
			for(Position p: positions)  {
				biggestDot = Math.max(biggestDot, p.getSize());
				smallestDot = Math.min(smallestDot, p.getSize());
				nbrOfPersons += p.getSize();
			}

			float difference = biggestDot - smallestDot; 

			for(Position p: positions)  {
				float lat = (p.getLat() - startLatMap) / diffLat;

				float lon = (p.getLng() - startLonMap) / diffLon;
				float cur = minDotSize + ((p.getSize() - smallestDot)/difference)*diffDotSize;

				float x = lon*mapBitmap.getWidth();
				float y = mapBitmap.getHeight()-lat * mapBitmap.getHeight();
				canvas.drawCircle(x, (float)(y+6), cur ,paintGray); 
				//TODO
				// Delete gray ?
				canvas.drawCircle(x, y, cur ,paintRed);	
			}
			Log.d("view1",""+getView());
			se.lundakarnevalen.widget.LKTextView text = (se.lundakarnevalen.widget.LKTextView) getView().findViewById(R.id.nbr_of);

			Log.d("view2",""+text);
			//TODO change to english!
			text.setText(nbrOfPersons+"\nkarnevalister");
			img.setImageBitmap(bmOverlay);
		}

	}

	/**
	 * Get lat/lng, cluster from the database and update positions.
	 * 
	 */
	private void getAndUpdatePositions() {

		// Connect and get positions...
		ArrayList<Position> newPositions = new ArrayList<Position>();
		newPositions.add(new Position((float)55.704660,(float)13.191007,65)); //LUND
		newPositions.add(new Position((float)55.374660,(float)13.191007,17));
		newPositions.add(new Position((float)55.374660,(float)13.391007,40));
		newPositions.add(new Position((float)55.934660,(float)13.951007,10));
		updatePositions(newPositions);

	}

	private void testGetPosition() {
		Gson g = new Gson();
		// String js = g.toJson(credentials);
		// TODO
		// TOKEN ???		
		remote.setTextResultListener(new ResponseListener());
		LKRemote remote = new LKRemote(getContext(), new ResponseListener());
		remote.requestServerForText("api/clusters?token=P6VmxzvTypzP3qb3TEW7", "", LKRemote.RequestType.GET, false);
	}

	private void testCreatePosition(float lat, float lng) {
		Gson g =  new Gson();
		String token = "P6VmxzvTypzP3qb3TEW7";

		MyPos mp = new MyPos(lat,lng,token);

		
		String js = g.toJson(mp);

		Log.d("Gettt",""+js);
		// TODO
		// TOKEN ???
		Log.d("Getid: ",""+clusterId);
		LKRemote remote = new LKRemote(getContext(), new CreateListener());
		//remote.setTextResultListener(new CreateListener());
		if(clusterId == -1) {
			remote.requestServerForText("api/clusters", js, LKRemote.RequestType.POST, false);			
		}else {
			// Står fel i mail, ska vara put istället för post....
			remote.requestServerForText("api/clusters"+"/"+clusterId, js, LKRemote.RequestType.PUT, false);			

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
		public float getLat() {
			return lat;
		}
		public float getLng() {
			return lng;
		}
		public int getSize() {
			return quantity;
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

	private class ResponseListener implements LKRemote.TextResultListener {

		@Override
		public void onResult(String result) {
			Log.d("Success", "Yay, some result!");

			if(result == null) {
				return;
			}

			Log.d("Success", result);

			Gson gson = new Gson();
			CheckerJson checker = gson.fromJson(result, CheckerJson.class);

			Log.d("Get!",""+checker.toString());
			if(checker.sucess) {	
				Cluster cluster = gson.fromJson("{\"clusters\":" + checker.getClusters()+"}", Cluster.class);
				Log.d("Get!",""+cluster.toString());
				updatePositions(cluster.getPositions());
			}
		}
	}

	private class CreateListener implements LKRemote.TextResultListener {

		@Override
		public void onResult(String result) {
			Log.d("Success", "Result:" + result);

			if(result == null) {
				return;
			}

			Gson gson = new Gson();

			ResultPost res = gson.fromJson(result, ResultPost.class);

			Log.d("Success?:",""+res.getSuccess());
			if(res.getSuccess()) {
				clusterId = res.getId();
				SharedPreferences prefs = getContext().getSharedPreferences(SHARED_ID, getContext().MODE_PRIVATE);
				prefs.edit().putInt(key_cluster, clusterId).commit();
				Log.d("update clusterId to:",""+clusterId);
			} else {
				//TODO
			}
		}
	}


	private class CheckerJson {
		private boolean sucess;
		private String clusters;

		@Override
		public String toString() {
			return "Checker [success=" + sucess 
					+ ", clusters=" + clusters + "]";
		}
		public String getClusters() {
			return clusters;
		}

	}

	private class Cluster {
		private List<Position> clusters;
		@Override
		public String toString() {
			return "Cluster [clusters=" + clusters.get(0) + "]";
		}
		public List<Position> getPositions() {
			return clusters;
		}
	}
	private class MyPos {
		private GPSpos cluster;
		private String token;
		public MyPos(float lat, float lng,String token) {
			this.token = token;
			cluster = new GPSpos(lat,lng);
		}
	}
	private class GPSpos {
		private float lat;
		private float lng;
		public GPSpos(float lat, float lng) {
			this.lat = lat;
			this.lng = lng;
		}
	}
	private class ResultPost {
		private boolean success;
		private int cluster_id;

		public boolean getSuccess() {
			return success;
		}
		public int getId() {
			return cluster_id;
		}
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
