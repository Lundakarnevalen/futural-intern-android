package se.lundakarnevalen.android;

import java.io.IOException;

import se.lundakarnevalen.remote.GCMReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
<<<<<<< HEAD
import android.os.AsyncTask;
=======
>>>>>>> registrering-feature-branch
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashscreenActivity extends Activity{
	
	RelativeLayout wrapper;
	Context context;
	private static final String LOG_TAG = "splash";
	GoogleCloudMessaging gcm;
	String regId;
	SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen_layout);
		this.context = this;
		
		// init some stuff
		populateSp();
		
		wrapper = (RelativeLayout) findViewById(R.id.splash_wrapper);
		wrapper.setOnClickListener(cont);
		sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, Context.MODE_PRIVATE);
		
		if(checkForGooglePlay()){
			// GCM registration
			gcmRegistration();
		}else{
			// What to do???
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		checkForGooglePlay();
	}
	
	private String getRegId(){
		String regId = sp.getString(LKFragment.SP_GCM_REGID, "");
		if(regId.length() <= 0)
			return "";
		String regAppV = sp.getString(LKFragment.SP_GCM_REG_APP, "-1");
		String currentVersion = LKFragment.getAppVersion(this);
		if(!regAppV.equals(currentVersion)){
			Log.d(LOG_TAG, "New app version installed");
			return "";
		}
		return regId;
	}
	
	private void gcmRegistration(){
		gcm = GoogleCloudMessaging.getInstance(context);
		regId = getRegId();
		if(regId.length() <= 0){
			Log.d(LOG_TAG, "Will try to register");
			// Register for gcm.
			regInBackground();
		}else{
			Log.d(LOG_TAG, "found regId");
		}
	}
	
	private void storeAsRegId(String regId){
		if(sp == null)
			sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(LKFragment.SP_GCM_REGID, regId);
		editor.putString(LKFragment.SP_GCM_REG_APP, LKFragment.getAppVersion(context));
		editor.commit();
	}
	
	private void regInBackground(){
		AsyncTask<?, String, String> regInBackground = new AsyncTask<Object, String, String>(){

			@Override
			protected String doInBackground(Object... params) {
				String regId = null;
				try{
					if(gcm == null){
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(GCMReceiver.SENDER_ID);
					Log.d(LOG_TAG, "Got regId: "+regId);
					storeAsRegId(regId);
				}catch(IOException e){
					Log.e(LOG_TAG, "Exception thrown when trying to register"+e);
				}
				return regId;
			}
			
			@Override
			protected void onPostExecute(String message){
				
			}
			
		}.execute();
	}
	
	public boolean checkForGooglePlay(){
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(result != ConnectionResult.SUCCESS){
			if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
	            GooglePlayServicesUtil.getErrorDialog(result, this, GCMReceiver.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.e(LOG_TAG, "This device is not supported.");
	            // TODO: What to do?? 
	        }
		}
		return true;
	}
	
	
	
	View.OnClickListener cont = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, ContentActivity.class);
			context.startActivity(intent);
		}
	};
	
	/** 
	 * Populate SP with some basic data if there is none.
	 */
	private void populateSp(){
		// Init with registration data if none exists. 
		SharedPreferences sp = this.getSharedPreferences(LKFragment.SP_NAME, MODE_PRIVATE);
		int registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, -1);
		
		// Set values
		Editor editor = sp.edit();
		if(registrationStep == -1){
			editor.putInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		}
		
		editor.commit();
	}
}
