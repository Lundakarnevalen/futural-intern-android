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
	private static final String SHARED_PREFS_TOKEN = "LKToken";
	
	Context context;
	public String persNr, firstname, lastname, address, zipCode, zipTown, email, phone, foodPrefs, ddKar, ddNation, ddStud, ddOther, other;
	public int sex, nation, shirtSize, terms, driverLicens, starIntresse, starSektion;
	public int[] intresse, sektions;
	public boolean ddHeltidare, ddStyrelse, ddForman, ddJobb, LK2010, responsibility, afMember, karMember, nationMember, karnevelj;
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
		return false;
	}
	
	/**
	 * Store instance of user in SP. 
	 */
	public void storeUserLocaly(){
		Editor editor = sp.edit();
		editor.commit();
	}
	
	public String getJson(){
		return null;
	}
	
	public void parseJson(){
		
	}
}
