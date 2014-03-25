package sound;

import se.lundakarnevalen.android.R;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class ServiceSound extends Service {

	private static final String TAG = ServiceSound.class.getSimpleName();
	private SoundFactory factory;
	
	private int songId = R.raw.lundakarneval;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		factory.start(songId);
		
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		factory = new SoundFactory(this);
		factory.createLongMedia(R.raw.lundakarneval, true);
	}

	public IBinder onUnBind(Intent arg0) {
		// TO DO Auto-generated method
		return null;
	}

	public void onStop() {

	}
	
	public void onResume() {
		factory.resume(songId);
	}
	
	public void onPause() {
		factory.pause(songId);
	}

	@Override
	public void onDestroy() {
		factory.stopAll();
	}

	@Override
	public void onLowMemory() {
		factory.stopAll();
	}
}
