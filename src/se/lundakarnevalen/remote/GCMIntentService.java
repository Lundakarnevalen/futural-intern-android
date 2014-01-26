package se.lundakarnevalen.remote;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends IntentService{
	private static final String LOG_TAG = "GCM service";
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "Started service");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		Bundle extras = intent.getExtras();
		if(!extras.isEmpty()){
			// Handle data.
			Log.d(LOG_TAG, extras.getString("title"));
		}
	}
}
