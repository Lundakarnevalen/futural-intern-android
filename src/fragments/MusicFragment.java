package fragments;

import se.lundakarnevalen.android.R;
import sound.MySoundFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MusicFragment extends LKFragment {
	
	private MySoundFactory factory;
	private ImageView play;
	private boolean playing;
	private boolean started;
	private int songID = R.raw.lundakarneval; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_music, null);;
		
		//TODO Solve the repeat can/will work
		
		
		factory = new MySoundFactory(getActivity());
		factory.createLongMedia(songID, true);
		
		playing = false;
		started = false;
		
		play = (ImageView) rootView.findViewById(R.id.fr_layout_music_play);
		
		play.setOnClickListener(new PlayButton());
		
		return rootView;
	}
	
	
	private class PlayButton implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if(playing) {
				factory.pause(songID);
				play.setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
				playing = false;
			} else {
				playing = true;
				
				if(started) {
					factory.resume(songID);
				} else {
					factory.start(songID);
					started = true;
				}
				play.setImageResource(R.drawable.pause);
				
			}
		}	
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(factory == null) {
			return;
		}
		factory.resumeAll();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if(factory == null) {
			return;
		}
		factory.pauseAll();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Music");
	}
}
