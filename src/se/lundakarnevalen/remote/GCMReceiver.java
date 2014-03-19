package se.lundakarnevalen.remote;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Class for receiving messages from Google GCM .
 * @author alexander najafi
 *
 */
public class GCMReceiver extends WakefulBroadcastReceiver{
	
	public static String LOG_TAG = "GCM receiver";
	
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String SENDER_ID = "86134487709";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive");
		ComponentName cName = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
		startWakefulService(context, intent.setComponent(cName));
		setResultCode(Activity.RESULT_OK);
	}
}
