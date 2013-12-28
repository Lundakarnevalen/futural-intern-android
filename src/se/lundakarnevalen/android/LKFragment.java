package se.lundakarnevalen.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;

public class LKFragment extends Fragment{
	protected final String LOG_TAG = "LKFragment";
	
	Messanger messanger;
	
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
		messanger.loadFragment(fragment, addToBackstack);
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
