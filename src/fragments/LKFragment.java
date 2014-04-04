package fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.remote.LKUser;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

public class LKFragment extends Fragment {
	protected final static String LOG_TAG = "LKFragment";

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
	public static final int VERSION = 0x010;

	// Shared preferences keys
	public static final String SP_JSON_VERSION = "0.4";
	public static final String SP_GCM_NAME = "LKGCM";
	public static final String SP_GCM_REGID = "LKGCM_REG_ID";
	public static final String SP_GCM_REG_APP = "LKGCM_APPV";
	public static final String SP_NAME = "LKSharedPreferences";
	public static final String SP_KEY_REGISTRATION_STEP = "LKRegistrationStep";
	public static final String SP_KEY_REGISTRATION_LOCK = "LKRegistrationLock";

	public static String timeF = "2014-02-09, 23:59:59";

	boolean messageExists;

	/**
	 * Gets the application context
	 * 
	 * @return The context.
	 */
	public Context getContext() {
		return super.getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			messanger = (Messanger) activity;
		} catch (ClassCastException e) {
			Log.wtf(LOG_TAG, "could not get messanger");
		}
		Log.d(LOG_TAG, "Got messanger");
	}

	/**
	 * Set the Titlebar title
	 */

	public void setTitle(String title) {
		Bundle data = new Bundle();
		data.putString("title", title);
		messanger.message(MessangerMessage.SET_TITLE, data);
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		View root = actionBar.getCustomView();
		RelativeLayout infoPic = (RelativeLayout) root.findViewById(R.id.info_pic);		
		if(title.equals(getString(R.string.karta))) {
			infoPic.setVisibility(View.VISIBLE);
			
		} else {
			infoPic.setVisibility(View.INVISIBLE);
			
		}
			
		RelativeLayout menuPic = (RelativeLayout) root.findViewById(R.id.menu_drawer_toggle_wrapper);		
		if(title.equals(getString(R.string.info_text_actionbar))) {
			menuPic.setVisibility(View.GONE);
		} else {
			menuPic.setVisibility(View.VISIBLE);
				
		}
		showActionBarLogo(false);
	}

	public static String getAppVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		String version = "";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.wtf(LOG_TAG, "Could not get package info.");
		}
		try {
			version = info.versionName;
		} catch (NullPointerException e) {
			return "";
		}
		return version;
	}

	// public String getGcmRegId(){
	// SharedPreferences sp =
	// getContext().getSharedPreferences(LKFragment.SP_GCM_NAME,
	// Context.MODE_PRIVATE);
	// return sp.getString(LKFragment.SP_GCM_REGID, null);
	// }

	// public void showPopup(String msg, String title){
	// AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
	// builder.setTitle(title);
	// builder.setMessage(msg);
	// builder.setPositiveButton("OK", null);
	// builder.create().show();
	// }

	/**
	 * Converts dp to pixels.
	 * 
	 * @param dp
	 *            The number of dp.
	 * @param context
	 *            The application context.
	 * @return The number of dp.
	 */
	public static float dpToPx(int dp, Context context) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
		return px;
	}

	/**
	 * Returns if app is locked for some reason.
	 * 
	 * @return
	 */
	public boolean appIsLocked(LKUser user) {
		long time = System.currentTimeMillis();

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd, HH:mm:ss");
		formatter.setLenient(false);

		Date timeDate = null;
		try {
			timeDate = formatter.parse(timeF);
		} catch (ParseException e) {
			Log.d(LOG_TAG, "could not parse time");
		}
		long timeClose = timeDate.getTime();
		return (user.step >= 3) || (time > timeClose);
	}

	/**
	 * Updates UI inbox count in actionbar and menu.
	 * 
	 * @param count
	 *            Number of unread messages.
	 */
	public void setInboxCount() {
		Bundle data = new Bundle();
		messanger.message(MessangerMessage.SET_INBOX_COUNT, data);
	}

	/**
	 * Show or hide actionbar logo.
	 * 
	 * @param show
	 *            If true the logo will be shown, if false the text will be
	 *            shown.
	 */
	public void showActionBarLogo(boolean show) {
		Bundle data = new Bundle();
		data.putBoolean("show", show);
		messanger.message(MessangerMessage.SHOW_ACTION_BAR_LOGO, data);
	}

	/**
	 * Loads fragment into framelayout.
	 * 
	 * @param fragment
	 *            The fragment to launch h
	 * @param addToBackstack
	 *            If true it will be added to backstack on launch.
	 */
	public void loadFragment(Fragment fragment, boolean addToBackstack) {
		Log.d(LOG_TAG, "messenger was"
				+ ((messanger == null) ? "null" : "not null"));

		messanger.loadFragment(fragment, addToBackstack);
	}

	/**
	 * 
	 * @see fragments.LKFragment.Messanger#popFragmentStack()
	 */
	public void popFragmentStack() {
		messanger.popFragmentStack();
	}

	/**
	 * Handles radiobuttons in the fragment
	 * 
	 * @param view
	 *            radio button view
	 */
	public void onIntrestsRadioButtonClicked(View view) {

	}

	/**
	 * Handles checkboxes in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onCheckBoxClicked(View view) {

	}

	/**
	 * Handles checkboxes for sections in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onSectionCheckBoxClicked(View view) {

	}

	/**
	 * Handles checkboxes for intrests in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onIntrestsCheckBoxClicked(View view) {

	}

	public boolean messageExistsInDb(int id) {
		final Context context = getContext();
		final int msgId = id;
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				LKSQLiteDB db = new LKSQLiteDB(context);
				boolean b = db.messageExistsInDb(msgId);
				db.close();
				return b;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				messageExists = result;
			}
 
		}.execute();

		return messageExists;
	}

	/**
	 * Interface to be implemented by activity containing the fragment.
	 * 
	 * @author alexander
	 * 
	 */
	public interface Messanger {
		public void message(MessangerMessage message, Bundle data);

		public void loadFragment(Fragment fragment, boolean addToBackstack);
		public void popFragmentStack();
	}

	/**
	 * Enum for different types of messages.
	 * 
	 * @author alexander
	 * 
	 */
	public enum MessangerMessage {
		SET_TITLE, SET_INBOX_COUNT, SHOW_ACTION_BAR_LOGO;
	}

	

	
}
