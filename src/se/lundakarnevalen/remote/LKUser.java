package se.lundakarnevalen.remote;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class representing a user. 
 * @author alexander
 *
 */
public class LKUser {
	private static final String SHARED_PREFS_NAME = "LKUserStorage";
	private static final String SHARED_PREFS_USERNAME = "LKUsername";
	private static final String SHARED_PREFS_TOKEN = "LKToken";
	
	/**
	 * Check if there is a user stored localy on this device .
	 * @param context The application context
	 * @return True if there is a user, false if there is not. 
	 */
	public static boolean localUserStored(Context context){
		SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
		String username = sp.getString(SHARED_PREFS_USERNAME, null);
		String token = sp.getString(SHARED_PREFS_TOKEN, null);
		return (username == null && token == null);
	}
}
