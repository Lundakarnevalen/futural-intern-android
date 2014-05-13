package fragments;


import com.google.gson.Gson;

import se.lundakarnevalen.android.IDActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class FrBunny extends LKFragment {
	String token;
	LKUser user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.bunny_layout, null);
		user = new LKUser(getContext());
		user.getUserLocaly();
		token = user.token;
		final LKRemote remote1 = new LKRemote(getContext(), new TrainListener());
		final LKRemote remote2 = new LKRemote(getContext(), new MyPositionListener());
	
 
		Button myPosition = ((Button)root.findViewById(R.id.my_position));
		Button trainPosition = ((Button)root.findViewById(R.id.train_position));
		Log.d("MARKUS!","MARKUS!");
		myPosition.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Log.d("MARKUS!",""+user.efternamn);
				if(user.efternamn.equals("Larsson")) {
					Log.d("MARKUS!","yes");
					int id = 11;
					BunnyCreate bc = new BunnyCreate(1.22f, 1.11f, user.token, id);
					Gson g = new Gson();
					String json = g.toJson(bc);
					if(remote2 != null) { 
						remote2.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
					}
				} else if(user.efternamn.equals("Lindqvist")){
					int id = 21;
					BunnyCreate bc = new BunnyCreate(1.22f, 1.11f, user.token, id);
					Gson g = new Gson();
					String json = g.toJson(bc);
					if(remote2 != null) { 
						remote2.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
					}
				
				} else if(user.efternamn.equals("Persson")){
					int id = 31;
					BunnyCreate bc = new BunnyCreate(1.22f, 1.11f, user.token, id);
					Gson g = new Gson();
					String json = g.toJson(bc);
					if(remote2 != null) { 
						remote2.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
					}
					
				}

			}
		});

		trainPosition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("MARKUS!","yes");
				int id = 1;
				BunnyCreate bc = new BunnyCreate(1.22f, 1.11f, user.token, id);
				Gson g = new Gson();
				String json = g.toJson(bc);
				if(remote1 != null) { 
					remote1.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
				}
			
			}
		});

		//getActivity().getActionBar().hide();

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

}
