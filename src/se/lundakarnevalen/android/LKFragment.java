package se.lundakarnevalen.android;

import android.content.Context;
import android.support.v4.app.Fragment;

public class LKFragment extends Fragment{
	
	/**
	 * Gets the application context
	 * @return The context.
	 */
	public Context getContext(){
		return super.getActivity().getApplicationContext();
	}
}
