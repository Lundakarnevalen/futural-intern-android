package se.lundakarnevalen.android;

import java.io.IOException;

import json.Karnevalist;
import json.Notification;
import json.Response;
import se.lundakarnevalen.remote.GCMHelper;
import se.lundakarnevalen.remote.GCMReceiver;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;


public class IDActivity extends Activity{


	private static final String LOG_TAG = "IDACTIVITY";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_id);

		//wrapper = (RelativeLayout) findViewById(R.id.);
		}

	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onStop(){
		super.onStop();
	}

	@Override
	public void onPause(){
		super.onPause();
	}


}
