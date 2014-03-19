package se.lundakarnevalen.remote;

import se.lundakarnevalen.android.R;
import activities.ContentActivity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import fragments.InboxFragment;
import fragments.LKFragment;

public class GCMIntentService extends IntentService{
	public static final int TYPE_MESSAGE = 0, TYPE_UPDATEUSER = 1;
	
	private static final String LOG_TAG = "GCM service";
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "Started service");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		Bundle extras = intent.getExtras();
		if(!extras.isEmpty()){
			// Handle data.
			Log.d(LOG_TAG, extras.toString());
			int type = Integer.parseInt(extras.getString("message_type"));
			switch(type){ 
			case TYPE_MESSAGE:
				Log.d("GCMIntentService", "adding message with id = "+Integer.parseInt(extras.getString("id")));
				addMessage(extras.getString("title"), extras.getString("message"), extras.getString("created_at"), Integer.parseInt(extras.getString("id")));
				break;
			case TYPE_UPDATEUSER:
				updateUser(extras);
				break;
			}
			GCMReceiver.completeWakefulIntent(intent);
		}
	}
	
	private void addMessage(String title, String message, String date, int id){
		// Add message to db.
		Log.d("GCMIntentService", "GCMIntentService.id = "+id);
		float res = InboxFragment.addMessage(this, title, message, date, id);
		
		if(res == -1){
			Log.e(LOG_TAG, "error inserting message, maybe already in db? with id: "+id);
			return;
		}
		// Show notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentTitle(this.getString(R.string.notification_title));
		builder.setContentText(message);
		builder.setSmallIcon(R.drawable.icon);
		builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
		builder.setLights(0xfff15d4c, 400, 200);
		builder.setAutoCancel(true);
		
		Intent resultIntent = new Intent(this, ContentActivity.class);
		resultIntent.putExtra("fragment", LKFragment.INBOX_FRAGMENT);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ContentActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pIntent);
		
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mgr.notify(1, builder.build());
	}
	
	private void updateUser(Bundle data){
		// Update user.
		Log.d(LOG_TAG, "Will now update user from remote");
		LKUser user = new LKUser(this);
		user.getUserLocaly();
		user.updateFromRemote();
	}
	
	
}
