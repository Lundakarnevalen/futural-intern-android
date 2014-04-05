package fragments;

import se.lundakarnevalen.android.R;
import sound.MySoundFactory;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicFragment extends LKFragment {

	private MySoundFactory factory;
	private ImageView play;
	private boolean playing;
	private boolean started;
	private int songID = R.raw.lundakarneval; 

	// Lyrics
	int text = 0;
	private long startTime;
	private int totTime;
	private TextView lyric1;
	private TextView lyric2;
	private TextView lyric3;
	private final Handler handler = new Handler();   
	private Runnable r;
	private long pauseTime = -1;
	private	int[] delays ={11060,3456,2578,4451,4002,3671,3849,2518,5784,3684,3656,3707,2263,1786,1929,1735,1976,1770,4701,1344,1922,3856,1060,2269,3289,2462,2481,3802,2486,3166,6320,3723,3775,3809,2234,1858,1920,1828
			,1980,2055,4088,1824,1908,4479,1094,1782,3146,14278,4617,1638,1898,1851,1985,1554,3977,1863,1855,5567,1994,3678,1970,1859,3603,1810,2296,3347,1940,2047,3259,2317};
	//38								
	private String[] lyrics;
	private int delay;
	private boolean pauseOnPause = false;
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_music, null);;
		lyric1 = (TextView) rootView.findViewById(R.id.lyric1);
		lyric2 = (TextView) rootView.findViewById(R.id.lyric2);
		lyric3 = (TextView) rootView.findViewById(R.id.lyric3);
		//TODO Solve the repeat can/will work
		if(lyrics == null) {
			Resources r = getResources();
			String [] lyrics ={r.getString(R.string.playPart1),r.getString(R.string.playPart2),
					r.getString(R.string.playPart3),r.getString(R.string.playPart4),
					r.getString(R.string.playPart5),r.getString(R.string.playPart6),
					r.getString(R.string.playPart7),r.getString(R.string.playPart8),
					r.getString(R.string.playPart9),r.getString(R.string.playPart10),
					r.getString(R.string.playPart11),r.getString(R.string.playPart12),
					r.getString(R.string.playPart13),r.getString(R.string.playPart14),
					r.getString(R.string.playPart15),r.getString(R.string.playPart16),
					r.getString(R.string.playPart17),r.getString(R.string.playPart18),
					r.getString(R.string.playPart19),r.getString(R.string.playPartRef1),
					r.getString(R.string.playPartRef2),r.getString(R.string.playPartRef3),
					r.getString(R.string.playPartRef4),r.getString(R.string.playPartRef5),
					r.getString(R.string.playPartRef6),r.getString(R.string.playPartRef7),
					r.getString(R.string.playPart20),r.getString(R.string.playPart21),
					r.getString(R.string.playPart22),r.getString(R.string.playPart23),
					r.getString(R.string.playPart24),r.getString(R.string.playPart25),
					r.getString(R.string.playPart26),r.getString(R.string.playPart27),
					r.getString(R.string.playPart13),r.getString(R.string.playPart14),
					r.getString(R.string.playPart15),r.getString(R.string.playPart16),
					r.getString(R.string.playPart17),r.getString(R.string.playPart18),
					r.getString(R.string.playPart19),r.getString(R.string.playPartRef1),
					r.getString(R.string.playPartRef2),r.getString(R.string.playPartRef3),
					r.getString(R.string.playPartRef4),r.getString(R.string.playPartRef5),
					r.getString(R.string.playPartRef6),r.getString(R.string.playPartRef7),
					r.getString(R.string.playPart28),r.getString(R.string.playPart29),
					r.getString(R.string.playPart30),r.getString(R.string.playPart31),
					r.getString(R.string.playPart32),r.getString(R.string.playPart33),
					r.getString(R.string.playPart34),r.getString(R.string.playPartRef1),
					r.getString(R.string.playPartRef2),r.getString(R.string.playPartRef1),
					r.getString(R.string.playPartRef2),r.getString(R.string.playPartRef3),
					r.getString(R.string.playPartRef4),r.getString(R.string.playPartRef5),
					r.getString(R.string.playPartRef6),r.getString(R.string.playPartRef7),
					r.getString(R.string.playPartRef2),r.getString(R.string.playPartRef3),
					r.getString(R.string.playPartRef4),r.getString(R.string.playPartRef5),
					r.getString(R.string.playPartRef6),r.getString(R.string.playPartRef7)};
			this.lyrics = lyrics;
		}

		factory = new MySoundFactory(getActivity());
		factory.createLongMedia(songID, true);

		playing = false;
		started = false;

		play = (ImageView) rootView.findViewById(R.id.fr_layout_music_play);

		play.setOnClickListener(new PlayButton());

		return rootView;
	} 

	public void startLyrics() {
		text = 0;
		totTime = 0;
		startTime = System.currentTimeMillis();
		lyric1.setText(lyrics[1]);
		lyric2.setText(lyrics[0]);
		r = new Runnable() {
			public void run() {
				totTime += delays[text];
				text++;
				if(text>=delays.length-1 ) {
					return;
				}
				if(text == delays.length-2) { 
					lyric1.setText("");
				} else {
					lyric1.setText(lyrics[text+1]);
				}
				lyric2.setText(lyrics[text]);
				lyric3.setText(lyrics[text-1]);
				Log.d("Delay",""+delays[text-1]);
		Log.d("Music",lyrics[text]);

				long diff = delays[text]-(System.currentTimeMillis()-startTime - totTime);
				if(diff >0) {
					handler.postDelayed(this, diff);
				} else {
					handler.postDelayed(this, 0);	
				}
			}
		};
		handler.postDelayed(r, delays[0]); 
	}

	public void resumeLyrics(int delay) {
		handler.postDelayed(r, delay); 
	}

	private class PlayButton implements OnClickListener {
		@Override
		public void onClick(View v) {

			if(playing) {
				handler.removeCallbacks(r);
				pauseTime = System.currentTimeMillis();
				delay = (totTime + delays[text])-(int)(pauseTime-startTime);
				factory.pause(songID);
				play.setImageResource(R.drawable.abc_ic_go_search_api_holo_light);
				playing = false;
			} else {
				playing = true;
				if(started) {
					startTime += (System.currentTimeMillis()-pauseTime);
					resumeLyrics(delay);
					factory.resume(songID);
				} else {
					startLyrics();
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
		if(pauseOnPause) {
			startTime += (System.currentTimeMillis()-pauseTime);
			resumeLyrics(delay);
			pauseOnPause = false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if(factory == null) {
			return;
		}
		if(playing) {
			Log.d("KOM HIT","BLAA");
			handler.removeCallbacks(r);
			pauseTime = System.currentTimeMillis();
			delay = (totTime + delays[text])-(int)(pauseTime-startTime);
			pauseOnPause = true;
		}
		factory.pauseAll();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Music");
	}
}
