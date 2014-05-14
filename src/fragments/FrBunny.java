package fragments;


import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;

import se.lundakarnevalen.android.IDActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.GPSTracker;
import se.lundakarnevalen.widget.GPSTracker.GPSListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


public class FrBunny extends LKFragment implements GPSListener {
	String token;
	private final int TRAIN_AUTO = 10000;
	private final int MY_AUTO = 5000;
	TextView textLng;
	TextView textLat;
	TextView textAcc;
	TextView textTime;
	LKUser user;
	Runnable r;
	Handler h;
	Runnable r2;
	Handler h2;
	double lat = 0;
	double lng = 0;
	DateFormat df;
	double markuslat = 0;
	double markuslng = 0;
	double filiplat = 0;
	double filiplng = 0;
	double fredriklat = 0;
	double fredriklng = 0;
	double trainlat = 0;
	double trainlng = 0;
	
	TextView textmarkuslat;
	TextView textmarkuslng;
	TextView textfiliplat;
	TextView textfiliplng;
	TextView textfredriklat;
	TextView textfredriklng;
	TextView texttrainlat;
	TextView texttrainlng;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.bunny_layout, null);
		user = new LKUser(getContext());
		user.getUserLocaly();
		token = user.token;
		final LKRemote remote1 = new LKRemote(getContext(), new TrainListener());
		final LKRemote remote2 = new LKRemote(getContext(), new MyPositionListener());
		final LKRemote remote3 = new LKRemote(getContext(), new FriendListener());
		GPSTracker gpstracker = new GPSTracker(getContext());
		gpstracker.addListener(this);

		Button myPosition = ((Button)root.findViewById(R.id.my_position));
		Button trainPosition = ((Button)root.findViewById(R.id.train_position));
		textLng = ((TextView)root.findViewById(R.id.my_lng));
		textLat = ((TextView)root.findViewById(R.id.my_lat));
		textAcc = ((TextView)root.findViewById(R.id.accuracy));
		textTime = ((TextView)root.findViewById(R.id.gps_time));
		textmarkuslat = ((TextView)root.findViewById(R.id.markus_lat));
		textmarkuslng = ((TextView)root.findViewById(R.id.markus_lng));
		textfiliplat = ((TextView)root.findViewById(R.id.filip_lat));
		textfiliplng = ((TextView)root.findViewById(R.id.filip_lng));
		textfredriklat = ((TextView)root.findViewById(R.id.fredrik_lat));
		textfredriklng = ((TextView)root.findViewById(R.id.fredrik_lng));
		texttrainlat = ((TextView)root.findViewById(R.id.train_lat));
		texttrainlng = ((TextView)root.findViewById(R.id.train_lng));
			df = DateFormat.getTimeInstance();
		Log.d("MARKUS!","MARKUS!");
		myPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MARKUS!",""+user.efternamn);
				if(user.efternamn.equals("Larsson")) {
					Log.d("MARKUS!","yes");
					int id = 11;
					send(remote2,id,user.token);

				} else if(user.efternamn.equals("Lindqvist")){
					int id = 21;
					send(remote2,id,user.token);

				} else if(user.efternamn.equals("Persson")){
					int id = 31;
					send(remote2,id,user.token);
				}

			}
		});

		trainPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("MARKUS!","yes");
				int id = 1;
				send(remote1,id,user.token);
			}
		});

		//getActivity().getActionBar().hide();

		ToggleButton button = ((ToggleButton)root.findViewById(R.id.train_auto));
		if(h == null) {
			h = new Handler();	
		}
		if(r == null){
			r = new Runnable() {	
				@Override
				public void run() {
					int id = 1;
					send(remote1,id,user.token);
					h.postDelayed(this, TRAIN_AUTO);
				}
			};
		}
		ToggleButton button2 = ((ToggleButton)root.findViewById(R.id.my_auto));

		if(h2 == null) {
			h2 = new Handler();	
		}
		if(r2 == null){
			r2 = new Runnable() {	
				@Override
				public void run() {
					if(user.efternamn.equals("Larsson")) {
						Log.d("MARKUS!","yes");
						int id = 11;
						send(remote2,id,user.token);

					} else if(user.efternamn.equals("Lindqvist")){
						int id = 21;
						send(remote2,id,user.token);

					} else if(user.efternamn.equals("Persson")){
						int id = 31;
						send(remote2,id,user.token);
					}
					h2.postDelayed(this, MY_AUTO);
				}
			};
		} 

		button.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					// SET AUTO ON
					h.post(r);
				}else {
					h.removeCallbacks(r);
					// SET AUTO OFF
				}
			}
		});

		button2.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					// SET AUTO ON
					h2.post(r2);
				}else {
					h2.removeCallbacks(r2);
					// SET AUTO OFF
				}
			}
		});
		Button map = ((Button)root.findViewById(R.id.show_on_map));
		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat!=0) {
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					getContext().startActivity(intent);
				}
			}
		});

		Button markusmap = ((Button)root.findViewById(R.id.show_markus_map));
		markusmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat!=0) {
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", markuslat, markuslng);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					getContext().startActivity(intent);
				}
			}
		});

		Button filipmap = ((Button)root.findViewById(R.id.show_filip_map));
		filipmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat!=0) {
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", filiplat, filiplng);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					getContext().startActivity(intent);
				}
			}
		});
		Button fredrikmap = ((Button)root.findViewById(R.id.show_fredrik_map));
		fredrikmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat!=0) {
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", fredriklat, fredriklng);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					getContext().startActivity(intent);
				}
			}
		});
		
		Button trainmap = ((Button)root.findViewById(R.id.show_train_map));
		trainmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(lat!=0) {
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", trainlat, trainlng);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					getContext().startActivity(intent);
				}
			}
		});


		

		
		
		Button clear_my = ((Button)root.findViewById(R.id.clear_my));
		Button clear_train = ((Button)root.findViewById(R.id.clear_train));
		clear_my.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = 0;
				if(user.efternamn.equals("Larsson")) {
					id = 11;

				} else if(user.efternamn.equals("Lindqvist")){
					id = 21;

				} else if(user.efternamn.equals("Persson")){
					id = 31;
				}
				BunnyCreate bc = new BunnyCreate(0, 0, token, id);
				Gson g = new Gson();
				String json = g.toJson(bc);
				if(remote2 != null) { 
					remote2.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
				}

			}
		});
		clear_train.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = 1;
				BunnyCreate bc = new BunnyCreate(0, 0, token, id);
				Gson g = new Gson();
				String json = g.toJson(bc);
				if(remote1 != null) { 
					remote1.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
				}

			}
		});
		Button updateAll = ((Button)root.findViewById(R.id.update_all));
		updateAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(remote3 != null) {
		            remote3.requestServerForText("api/train_positions", "", LKRemote.RequestType.GET, false);
		        }

			}
		});


		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("BUNNY");
	}

	@Override
	public void onPause() {
		super.onResume();
	}


	@Override
	public void onResume() {
		super.onResume();
	}
	private class TrainListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			Log.d("MARKUS!","MARKUS!"+result);
			if (result == null) {
				return;
			}
			Log.d("GetListener get result: ", result);
			/*
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
			 */
		}
	}
	private class MyPositionListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			Log.d("MARKUS!","MARKUS!"+result);
			Log.d("GetListener get result: ", result);

			if (result == null) {
				return;
			}
			Log.d("GetListener get result: ", result);
		}
	}

	public void send(LKRemote remote, int id,String token ){
		if(lat != 0) {

			BunnyCreate bc = new BunnyCreate((float)lat, (float)lng, token, id);
			Gson g = new Gson();
			String json = g.toJson(bc);
			if(remote != null) { 
				remote.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
			}
		}
	}


	public class BunnyCreate {
		public GPSpos train_position;
		public String token;

		public BunnyCreate(float lat, float lng,String token, int id) {
			this.token = token;
			train_position = new GPSpos(lat,lng,id);
		}
		private class GPSpos {
			private float lat;
			private float lng;
			private int id;
			public GPSpos(float lat, float lng, int id) {
				this.lat = lat;
				this.lng = lng;
				this.id = id;
			}
		}

	}


	@Override
	public void onNewLocation(double lat, double lng,double accuracy,long time) {
		// TODO Auto-generated method stub
		Log.d("MARKUS!","lat: "+lat +"lng: "+lng);
		this.lat = lat;
		this.lng = lng;
		textLat.setText("Lat: "+lat);
		textLng.setText("Lng: "+lng);
		textTime.setText("Tid: "+df.format(new Date(time)));
		textAcc.setText("Accuracy: "+accuracy);
	}
	
	private class FriendListener implements LKRemote.TextResultListener {
		@Override
		public void onResult(String result) {
			Log.d("MARKUS!","MARKUS!"+result);
			if (result == null) {
				return;
			}
			Gson gson = new Gson();
            Bunny bc = gson.fromJson(result,Bunny.class);
            Log.d("GetListener get result: ", result);
            if(bc.success) {
                for(Bunny.Position p:bc.train_positions) {
                    Log.d("GET: ",p.id+" lat: "+p.lat+" lng: "+p.lng);
                    // TODO update position on map...
                    switch(p.id) {
                        case 1:
                        	trainlat = p.lat;
                        	trainlng = p.lng;
                        	texttrainlat.setText("Lat: "+p.lat );
                            texttrainlng.setText("Lng: "+p.lng );
                            break;
                        case 11:
                        	markuslat = p.lat;
                        	markuslng = p.lng;
                            textmarkuslat.setText("Lat: "+p.lat );
                            textmarkuslng.setText("Lng: "+p.lng );
                            break;
                        case 21:
                        	filiplat = p.lat;
                        	filiplng = p.lng;
                            textfiliplat.setText("Lat: "+p.lat );
                            textfiliplng.setText("Lng: "+p.lng );
                            break;
                        case 31:
                        	fredriklat = p.lat;
                        	fredriklng = p.lng;
                            textfredriklat.setText("Lat: "+p.lat );
                            textfredriklng.setText("Lng: "+p.lng );
                            break;
                    }
                }
            }
        
		}
	}
	public class Bunny {
        public List<Position> train_positions;
        public boolean success;
        public Bunny() {

        }
        private class Position {
            private float lat;
            private float lng;
            private int id;
            public Position(int id, float lat, float lng) {
                this.lat = lat;
                this.lng = lng;
                this.id = id;
            }
        }

    }
    


}
