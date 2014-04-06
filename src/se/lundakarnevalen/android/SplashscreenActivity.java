package se.lundakarnevalen.android;

import java.io.IOException;

import json.Notification;
import json.Response;
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

import fragments.LKFragment;
//github.com/Lundakarnevalen/futural-intern-android.git

public class SplashscreenActivity extends Activity{

	private boolean stop;
	RelativeLayout wrapper;
	Context context;

	private final int THREAD_DELAY = 2000; //Splashscreen shown in milliseconds
//	private final int THREAD_DELAY = 200; //Splashscreen shown in milliseconds

	private static final String LOG_TAG = "splash";
	GoogleCloudMessaging gcm;
	String regId;
	SharedPreferences sp;
	boolean shown = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen_layout);
		this.context = this;
		stop = false;
		// init some stuff
		populateSp();

		wrapper = (RelativeLayout) findViewById(R.id.splash);
		Log.d(LOG_TAG, (wrapper == null) ? "wrapper was null" : "wrapper was NOT null");
		//wrapper.setOnClickListener(cont);
		createMenuThread();

		sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, Context.MODE_PRIVATE);
		addDataSekBg(); 
		if(checkForGooglePlay()){
			// GCM registration
			Log.d("SplashScreen", "starting gcmRegistration()");
			gcmRegistration();
		}else{
			// What to do???
		}

		getMessages();
		//EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		stop = false;
		checkForGooglePlay(); 
	}

	@Override
	public void onStop(){
		super.onStop();
		//EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public void onPause(){
		Log.d("hej","hej");
		System.exit(0);
		//EasyTracker.getInstance().activityStop(this);
	}

	public void getMessages() {
		Log.d("SplashScreen", "Starting getMessages()");
		LKRemote remote = new LKRemote(context, new LKRemote.TextResultListener() {

			@Override
			public void onResult(String result) {
				Log.d("SplashScreen", "onResult(): "+result);
				if(result == null) {
					Log.d("SplashScreen", "Result from server was null");
					return;
				}
				Gson gson = new Gson();
				Response.Notifications notifications = gson.fromJson(result, Response.Notifications.class);
				Notification[] messages = notifications.notifications;
				LKSQLiteDB db = new LKSQLiteDB(context);
				Log.d("SplashScreen", "Created db object. Starting loop. messages.length = "+messages.length);
				for(int i=0;i<messages.length;i++) {
					Log.d("SplashScreen", "loop counter i = "+i);
					if(!db.messageExistsInDb(messages[i].id)) {
						Log.d("SplashScreen", "Message not in db");
						Time time = new Time();
						time.parse3339(messages[i].created_at);
						String date = time.format("%Y-%m-%d %H:%M");
						addMessage(messages[i].title, messages[i].message, date, messages[i].id, db);
					}
					Log.d(LOG_TAG, "done");
				}
				Log.d(LOG_TAG, "loop done");
				db.close();
				Log.d("SplashScreen", "Completed getMessages");
			}
		});
		remote.showProgressDialog(false);
		Log.d("SplashScreen", "Starting server request");
		remote.requestServerForText("notifications.json", "", RequestType.GET, false);
	}

	public void addMessage(String title, String message, String date, int id, LKSQLiteDB db) {
		//LKSQLiteDB db = new LKSQLiteDB(context);
		db.addItem(new LKMenuListItem(title, message, date, id, true, null));
		//db.close();
		Log.d("SplashScreen", "Added message with id = "+id);
	}

	private String getRegId(){
		Log.d("SplashScreen", "Starting getRegId()");
		String regId = sp.getString(LKFragment.SP_GCM_REGID, "");
		if(regId.length() <= 0)
			return "";
		String regAppV = sp.getString(LKFragment.SP_GCM_REG_APP, "-1");
		String currentVersion = LKFragment.getAppVersion(this);
		if(!regAppV.equals(currentVersion)){
			Log.d(LOG_TAG, "New app version installed");
			return "";
		}
		Log.d("SplashScreen", "Finished getRegId()");
		return regId;
	}

	private void gcmRegistration(){
		gcm = GoogleCloudMessaging.getInstance(context);
		Log.d("SplashScreen", "Got gcm instance");
		regId = getRegId();
		if(regId.length() <= 0){
			Log.d(LOG_TAG, "Will try to register");
			// Register for gcm.
			regInBackground(context, gcm);
		}else{
			Log.d(LOG_TAG, "found regId");
		}
	}

	public static void storeAsRegId(String regId, Context context){
		SharedPreferences sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(LKFragment.SP_GCM_REGID, regId);
		editor.putString(LKFragment.SP_GCM_REG_APP, LKFragment.getAppVersion(context));
		editor.commit();	
	}
	
	/*
	private void storeSekData(){
		// Information about the sections
		SectionSQLiteDB db = new SectionSQLiteDB(this);
		db.dropEntiresInDatabase();

		db.addItem(new LKSectionsItem(
				"Barnevalen", 
				R.drawable.barnevalen,
				q1("Barnevalen") + getString(R.string.barnevalen_b1) + q2() + getString(R.string.barnevalen_b2) + q3() + getString(R.string.barnevalen_b3) + q4("Barnevalen") + getString(R.string.barnevalen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Biljetteriet",
				R.drawable.biljetteriet,
				q1("Biljetteriet") + getString(R.string.biljetteriet_b1) + q2() + getString(R.string.biljetteriet_b2) + q3() + getString(R.string.biljetteriet_b3) + q4("Biljetteriet") + getString(R.string.biljetteriet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Blådderiet",
				R.drawable.bladderiet,
				q1("Blädderiet") + getString(R.string.bladderiet_b1) + q2() + getString(R.string.bladderiet_b2) + q3() + getString(R.string.bladderiet_b3) + q4("Blädderiet") + getString(R.string.bladderiet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Cirkusen",
				R.drawable.cirkusen,
				q1("Cirkusen") + getString(R.string.cirkusen_b1) + q2() + getString(R.string.cirkusen_b2) + q3() + getString(R.string.cirkusen_b3) + q4("Cirkusen") + getString(R.string.cirkusen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Dansen",
				R.drawable.dansen,
				q1("Dansen") + getString(R.string.dansen_b1) + q2() + getString(R.string.dansen_b2) + q3() + getString(R.string.dansen_b3) + q4("Dansen") + getString(R.string.dansen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Ekonomi",
				R.drawable.ekonomi,
				q1("Ekonomisektionen") + getString(R.string.ekonomi_b1) + q2() + getString(R.string.ekonomi_b2) + q3() + getString(R.string.ekonomi_b3) + q4("Ekonomisektionen") + getString(R.string.ekonomi_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Fabriken",
				R.drawable.fabriken,
				q1("Fabriken") + getString(R.string.fabriken_b1) + q2() + getString(R.string.fabriken_b2) + q3() + getString(R.string.ekonomi_b3) + q4("Fabriken") + getString(R.string.ekonomi_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Festmästeriet",
				R.drawable.festmasteriet,
				q1("Festmästeriet") + getString(R.string.festmasteriet_b1) + q2() + getString(R.string.festmasteriet_b2) + q3() + getString(R.string.festmasteriet_b3) + q4("Festmästeriet") + getString(R.string.festmasteriet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Filmen",
				R.drawable.filmen,
				q1("Filmen") + getString(R.string.filmen_b1) + q2() + getString(R.string.filmen_b2) + q3() + getString(R.string.filmen_b3) + q4("Filmen") + getString(R.string.filmen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Kabarén",
				R.drawable.kabaren,
				q1("Kabarén") + getString(R.string.kabaren_b1) + q2() + getString(R.string.kabaren_b2) + q3() + getString(R.string.kabaren_b3)  + q4("Kabarén") + getString(R.string.kabaren_b4) ,
				true));
		db.addItem(new LKSectionsItem(
				"Klipperiet",
				R.drawable.klipperiet,
				q1("Klipperiet") + getString(R.string.klipperiet_b1) + q2() + getString(R.string.klipperiet_b2) + q3() + getString(R.string.klipperiet_b3) + q4("Klipperiet") + getString(R.string.klipperiet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Kommunikation",
				R.drawable.kommunikation,
				q1("Kommunikationssektionen") + getString(R.string.kommunikationssektionen_b1) + q2() + getString(R.string.kommunikationssektionen_b2) + q3() + getString(R.string.kommunikationssektionen_b3) + q4("Kommunikationssektionen") + getString(R.string.kommunikationssektionen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Krog Fine Dine",
				R.drawable.krog,
				q1("Krog Fine Dine") + getString(R.string.krog_fine_dine_b1) + q2() + getString(R.string.krog_fine_dine_b2) + q3() + getString(R.string.krog_fine_dine_b3) + q4("Krog Fine Dine") + getString(R.string.krog_fine_dine_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Krog l[a]unchingpad",
				R.drawable.krog, 
				q1("Krog l[a]unchingpad") + getString(R.string.krog_launchingpad_b1) + q2() + getString(R.string.krog_launchingpad_b2) + q3() + getString(R.string.krog_launchingpad_b3) + q4("Krog l[a]unchingpad") + getString(R.string.krog_launchingpad_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Krog Molnet",
				R.drawable.krog,
				q1("Krog Molnet") + getString(R.string.krog_molnet_b1) + q2() + getString(R.string.krog_molnet_b2) + q3() + getString(R.string.krog_molnet_b3) + q4("Krog Molnet") + getString(R.string.krog_molnet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Krog Nangilima",
				R.drawable.krog,
				q1("Krog Nangilima") + getString(R.string.krog_nangilima_b1) + q2() + getString(R.string.krog_nangilima_b2) + q3() + getString(R.string.krog_nangilima_b3) + q4("Krog Nangilima") + getString(R.string.krog_nangilima_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Krog Undervatten",
				R.drawable.krog,
				q1("Krog Undervatten") + getString(R.string.krog_undervatten_b1) + q2() + getString(R.string.krog_undervatten_b2) + q3() + getString(R.string.krog_undervatten_b3) + q4("Krog Undervatten") + getString(R.string.krog_undervatten_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Musik",
				R.drawable.musiken,
				q1("Musiksektionen") + getString(R.string.musik_b1) + q2() + getString(R.string.musik_b2) + q3() + getString(R.string.musik_b3) + q4("Musiksektionen") + getString(R.string.musik_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Nöjen",
				R.drawable.nojen,
				q1("Nöjessektionen") + getString(R.string.noje_b1) + q2() + getString(R.string.noje_b2) + q3() + getString(R.string.noje_b3) + q4("Nöjessektionen") + getString(R.string.noje_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Område",
				R.drawable.omrade,
				q1("Område") + getString(R.string.omradet_b1) + q2() + getString(R.string.omradet_b2) + q3() + getString(R.string.omradet_b3) + q4("Områdessektionen") + getString(R.string.omradet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Radio",
				R.drawable.radio,
				q1("Radion") + getString(R.string.radio_b1) + q2() + getString(R.string.radio_b2) + q3() + getString(R.string.radio_b3) + q4("Radion") + getString(R.string.radio_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Revy",
				R.drawable.revyn,
				q1("Revyn") + getString(R.string.revyn_b1) + q2() + getString(R.string.revyn_b2) + q3() + getString(R.string.revyn_b3) + q4("Revyn")+ getString(R.string.revyn_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Säkerhet",
				R.drawable.sakerhet,
				q1("Säkerhetssektionen") + getString(R.string.sakerhet_b1) + q2() + getString(R.string.sakerhet_b2) + q3() + getString(R.string.sakerhet_b3) + q4("Säkerhetssektionen") + getString(R.string.sakerhet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Shoppen",
				R.drawable.shoppen,
				q1("Shoppen") + getString(R.string.shoppen_b1) + q2() + getString(R.string.shoppen_b2) + q3() + getString(R.string.shoppen_b3) + q4("Shoppen") + getString(R.string.shoppen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Show",
				R.drawable.show,
				q1("Show") + getString(R.string.show_b1) + q2() + getString(R.string.show_b2) + q3() + getString(R.string.show_b3) + q4("Showen") + getString(R.string.show_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Smånöjen",
				R.drawable.smanojen,
				q1("Smånöjen") + getString(R.string.smanojen_b1) + q2() + getString(R.string.smanojen_b2) + q3() + getString(R.string.smanojen_b3) + q4("Smånöjen") + getString(R.string.smanojen_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Snaxeriet",
				R.drawable.snaxeriet,
				q1("Snaxeriet") + getString(R.string.snaxeri_b1) + q2() + getString(R.string.snaxeri_b2) + q3() + getString(R.string.snaxeri_b3) + q4("Snaxeriet") + getString(R.string.snaxeri_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Spexet",
				R.drawable.spexet,
				q1("Spexet") + getString(R.string.spexet_b1) + q2() + getString(R.string.spexet_b1) + q3() + getString(R.string.spexet_b3) + q4("Spexet") + getString(R.string.spexet_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Tåget",
				R.drawable.tag,
				q1("Tåget") + getString(R.string.tag_b1) + q3() + getString(R.string.tag_b3) + q4("Tåget") + getString(R.string.tag_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Tältnöje",
				R.drawable.nojen,
				q1("Tältnöje") + getString(R.string.taltnoje_b1) + q2() + getString(R.string.taltnoje_b2) + q3() + getString(R.string.taltnoje_b3) + q4("Tältnöjena") + getString(R.string.taltnoje_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Tombolan",
				R.drawable.tombolan,
				q1("Tombolan") + getString(R.string.tombolan_b1) + q2() + getString(R.string.tombolan_b2) + q3() + getString(R.string.tombolan_b3) + q4("Tombolan") + getString(R.string.tombolan_b4),
				true));
		db.addItem(new LKSectionsItem(
				"Vieriet",
				R.drawable.vieriet,
				q1("Vieriet") + getString(R.string.vieriet_b1) + q2() + getString(R.string.vieriet_b2) + q3() + getString(R.string.vieriet_b3) + q4("Vieriet") + getString(R.string.vieriet_b1),
				true));
	}
*/
	/* The questions*/
	public String q1(String sName) {
		return "<b><i>Vad ska " + sName + " göra? Var är den övergripande \"uppgiften?\"</i></b><br>";
	}
	public String q2() {
		return "<br><br><b><i>Vad finns det för olika saker att göra i din sektion?</i></b><br>";
	}
	public String q3() {
		return "<br><br><b><i>Hur många kommer ni vara i sektionen (ungefär)?</i></b><br>";
	}
	public String q4(String sName) {
		return "<br><br><b><i>Vad är det bästa med " + sName + "? Vad är speciellt?</i></b><br>";
	}

	public void addDataSekBg(){
		new AsyncTask<Object, String, String>(){

			@Override
			protected String doInBackground(Object... params) {
//				storeSekData();
				Log.d(LOG_TAG, "added sek data!");
				return null;
			}

		}.execute();
	}

	private void createMenuThread() {
		/*start up the splash screen and main menu in a time delayed thread*/
		new Handler().postDelayed( new Thread() {
			@Override
			public void run() {
				
				Intent intent;
				
				if(LKUser.localUserStored(context)) {
					Log.d(SplashscreenActivity.class.getSimpleName(), "User stored locally");
					
					intent = new Intent(SplashscreenActivity.this, ContentActivity.class);
				} else {
					intent = new Intent(SplashscreenActivity.this, AcLogin.class);					
				}
				
				SplashscreenActivity.this.startActivity(intent);

				SplashscreenActivity.this.finish();
				overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
			}
		}, THREAD_DELAY);
	}

	public static void regInBackground(final Context context, final GoogleCloudMessaging gcm){
		new AsyncTask<Object, String, String>(){

			@Override
			protected String doInBackground(Object... params) {
				String regId = null;
				try{
					/*if(gcm == null){
						gcm = GoogleCloudMessaging.getInstance(context);
					}*/
					regId = gcm.register(GCMReceiver.SENDER_ID);
					Log.d(LOG_TAG, "Got regId: "+regId);
					storeAsRegId(regId, context);					
				}catch(IOException e){
					Log.e(LOG_TAG, "Exception thrown when trying to register"+e);
				}

				// POST reg id to server
				LKRemote remote = new LKRemote(context, new TextResultListener(){
					@Override
					public void onResult(String result) {
						Log.d(LOG_TAG, "regId result: "+result);
					}
				});
				remote.requestServerForText("phones.json", "{\"google_token\":\""+regId+"\"}", LKRemote.RequestType.POST, false);

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
	        }
		}
		return true;
	}


	//	View.OnClickListener cont = new View.OnClickListener() {
	//		@Override
	//		public void onClick(View v) {
	//			launchApp();
	//		}
	//	};
	//	
	//	private void launchApp(){
	//		Intent intent = new Intent(context, ContentActivity.class);
	//		startActivity(intent);
	//		finish();
	//	}

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


		//		LKSQLiteDB db = new LKSQLiteDB(context);
		//		int heigestLocalId = db.heighestMessageId();
		//		db.close();

		//		LKRemote remote = new LKRemote(this,new LKRemote.TextResultListener() {
		//			@Override
		//			public void onResult(String result) {
		//				LKSQLiteDB db = new LKSQLiteDB(context);
		//				Log.d(LOG_TAG, result);
		//				Gson gson = new Gson();
		//				Response.Message resp = gson.fromJson(result, Response.Message.class);
		//				if(resp.status.equals("success")){
		//					Log.d("SplashScreenActivity", resp.notifications);
		//				}else{
		//					Log.d("SplashScreenActivity", "Something went horribly wrong...");
		//				}
		//				db.close();
		//			}
		//		});
	}
}
