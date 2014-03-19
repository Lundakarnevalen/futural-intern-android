package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.android.R.id;
import se.lundakarnevalen.android.R.layout;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends LKFragment{
	TextView tv;
	int counter = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.about_fragment, null);
		tv = (TextView) root.findViewById(R.id.easter);
		tv.setOnClickListener(click);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Om appen");
	}
	
	private void music(){
		MediaPlayer mp = new MediaPlayer();
	    try {
	        mp.setDataSource("rickroll.mp3");
	        mp.prepare();
	        mp.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	View.OnClickListener click = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			counter ++;
			if(counter == 10){
				Log.d(LOG_TAG, "Easter egg");
				music();
			}
		}
	};
}
