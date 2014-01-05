package se.lundakarnevalen.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Class representing a user. 
 * @author alexander
 *
 */
public class LKUser {
	private static final String LOG_TAG = "LKUser";
	
	private static final String SHARED_PREFS_NAME = "LKUserStorage";
	private static final String SHARED_PREFS_USERNAME = "LKUsername";
	private static final String SHARED_PREFS_TOKEN = "LKToken";
	Context context;
	String username, token;
	SharedPreferences sp;
	
	public LKUser(Context context){
		this.context = context;
		sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
	}
	
	/**
	 * Check if there is a user stored localy on this device .
	 * @param context The application context
	 * @return True if there is a user, false if there is not. 
	 */
	public static boolean localUserStored(Context context){
		SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		String username = sp.getString(SHARED_PREFS_USERNAME, null);
		String token = sp.getString(SHARED_PREFS_TOKEN, null);
		Log.d(LOG_TAG, "Username: "+username+", token: "+token);
		return !(username == null || token == null);
	}
	
	/**
	 * Store instance of user in SP. 
	 */
	public void storeUserLocaly(){
		Editor editor = sp.edit();
		editor.putString(SHARED_PREFS_USERNAME, username);
		editor.putString(SHARED_PREFS_TOKEN, token);
		editor.commit();
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setToken(String token){
		this.token = token;
	}
}
