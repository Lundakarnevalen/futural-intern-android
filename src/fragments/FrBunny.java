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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;


public class FrBunny extends LKFragment {
	String token;
	private final int TRAIN_AUTO = 10000;
	private final int MY_AUTO = 5000;
	LKUser user;
	Runnable r;
	Handler h;
	Runnable r2;
	Handler h2;
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
		BunnyCreate bc = new BunnyCreate(1.22f, 1.11f, token, id);
		Gson g = new Gson();
		String json = g.toJson(bc);
		if(remote != null) { 
			remote.requestServerForText("api/train_positions/"+id, json, LKRemote.RequestType.PUT, false);
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
