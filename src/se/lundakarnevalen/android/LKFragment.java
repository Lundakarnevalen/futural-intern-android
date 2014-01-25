package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class LKFragment extends Fragment{
	protected final String LOG_TAG = "LKFragment";
	
	public Messanger messanger;
	
	public static final int ABOUT_FRAGMENT = 0x001;
	public static final int INBOX_FRAGMENT = 0x002;
	public static final int MESSAGE_FRAGMENT = 0x003;
	public static final int REGISTRATION_FRAGMENT = 0x004;
	public static final int REGISTRATION_OH_NO_FRAGMENT = 0x005;
	public static final int REGISTRATION_PROGRESS_FRAGMENT = 0x006;
	public static final int RESET_PASSWORD_FRAGMENT = 0x007;
	public static final int SEKTIONER_FRAGMENT = 0x008;
	public static final int SIGN_IN_FRAGMENT = 0x009;
	
	//Shared preferences keys
	public static final String SP_NAME = "LKSharedPreferences";
	public static final String SP_KEY_REGISTRATION_STEP = "LKRegistrationStep";
	
	
	/**
	 * Gets the application context
	 * @return The context.
	 */
	public Context getContext(){
		return super.getActivity().getApplicationContext();
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			messanger = (Messanger) activity;
		}catch(ClassCastException e){
			Log.wtf(LOG_TAG, "could not get messanger");
		}
		Log.d(LOG_TAG, "Got messanger");
	}
	
	/**
	 * Set the Titlebar title
	 */
	public void setTitle(String title){
		Bundle data = new Bundle();
		data.putString("title", title);
		messanger.message(MessangerMessage.SET_TITLE, data);
		showActionBarLogo(false);
	}
	
	/**
	 * Converts dp to pixels.
	 * @param dp The number of dp.
	 * @param context The application context.
	 * @return The number of dp.
	 */
	public static float dpToPx(int dp, Context context){
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return px;
	}
	
	/** 
	 * Gets a new fragment to use for startup/on click of start button in menu. 
	 * @param context Application context
	 * @return Fragment to launch. 
	 */
	public static LKFragment getStartFragment(Context context){
		if(LKUser.localUserStored(context))
			return new RegistrationProgressFragment();
		else
			return new RegistrationOhNoFragment();
	}
	
	/**
	 * Updates UI inbox count in actionbar and menu.
	 * @param count Number of unread messages. 
	 */
	public void setInboxCount(int count){
		Bundle data = new Bundle();
		data.putInt("count", count);
		messanger.message(MessangerMessage.SET_INBOX_COUNT, data);
	}
	
	/**
	 * Show or hide actionbar logo.
	 * @param show If true the logo will be shown, if false the text will be shown. 
	 */
	public void showActionBarLogo(boolean show){
		Bundle data = new Bundle();
		data.putBoolean("show", show);
		messanger.message(MessangerMessage.SHOW_ACTION_BAR_LOGO, data);
	}
	
	/**
	 * Loads fragment into framlayout.
	 * @param fragment The fragment to launch
	 * @param addToBackstack If true it will be added to backstack on launch. 
	 */
	public void loadFragment(Fragment fragment, boolean addToBackstack){
		Log.d(LOG_TAG, "messenger was" + ((messanger == null) ? "null" : "not null"));
		messanger.loadFragment(fragment, addToBackstack);
	}
	/**
	 * Handles radiobuttons in the fragment
	 * @param view radio button view
	 */
	public void onRadioButtonClicked(View view) {
		
	}
	/**
	 * Handles checkboxes in the fragment
	 * @param view checkbox view
	 */
	public void onCheckBoxClicked(View view) {
		
	}
	
	
	/**
	 * Interface to be implemented by activity containing the fragment.
	 * @author alexander
	 *
	 */
	public interface Messanger{
		public void message(MessangerMessage message, Bundle data);
		public void loadFragment(Fragment fragment, boolean addToBackstack);
	}
	
	/**
	 * Enum for different types of messages. 
	 * @author alexander
	 *
	 */
	public enum MessangerMessage{
		SET_TITLE, SET_INBOX_COUNT, SHOW_ACTION_BAR_LOGO;
	}
}
