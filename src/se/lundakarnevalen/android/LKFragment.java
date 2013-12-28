package se.lundakarnevalen.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class LKFragment extends Fragment{
	private final String LOG_TAG = "LKFragment";
	
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
