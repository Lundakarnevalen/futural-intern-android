package fragments;

import java.util.Date;
import java.util.GregorianCalendar;

import se.lundakarnevalen.android.R;
import sound.MySoundFactory;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CountdownFragment extends LKFragment {
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
	private	int[] delays ={11260,3456,2578,4451,4002,3671,3849,2518,5784,3684,3656,3707,2263,1786,1929,1735,1976,1770,4701,1344,1922,3856,1060,2269,3289,2462,2481,3802,2486,3166,6320,3723,3775,3809,2234,1858,1920,1828
			,1980,2055,4088,1824,1908,4479,1094,1782,3146,14278,4617,1638,1898,1851,1985,1554,3977,1863,1855,5567,1994,3678,1970,1859,3603,1810,2296,3347,1940,2047,3259,2317};
	//38								
	private String[] lyrics;
	private int delay;
	//


	TextView tv;
	long diff;
	private String antonsTestHAHAHAAHA;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Change the layout
		View rootView = inflater.inflate(R.layout.fr_layout_countdown,null);
		tv = (TextView) rootView.findViewById(R.id.tvCountDown);
		//new DrawingTheCloud(this.getContext());

		lyric1 = (TextView) rootView.findViewById(R.id.lyric1);
		lyric2 = (TextView) rootView.findViewById(R.id.lyric2);
		lyric3 = (TextView) rootView.findViewById(R.id.lyric3);



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
		play = (ImageView) rootView.findViewById(R.id.countdown_playbutton);

		play.setOnClickListener(new PlayButton());
		if(!started) {

			factory = new MySoundFactory(getActivity());
			factory.createLongMedia(songID, false);

			playing = false;
			started = false;

		} else {
			if(text != delays.length-1) {				

				lyric1.setText(lyrics[text+1]);

			}
			if(text != 0) {				
				lyric3.setText(lyrics[text-1]);			
			}
			lyric2.setText(lyrics[text]);

			if(playing) {
				play.setImageResource(R.drawable.pause);
			} else {
				play.setImageResource(R.drawable.playerbutton);
			}
		}


		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.countdown_title));


		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
		tv.setTypeface(tf);
		Date karneval = new GregorianCalendar(2014, 4, 16, 14, 0, 0).getTime();
		Date today = new Date();

		diff = karneval.getTime() - today.getTime();

		new CountDownTimer(diff, 1000) {

			public void onTick(long diff) {
				long seconds = diff / 1000;
				long minutes = seconds / 60;
				long hours = minutes / 60;
				long days = hours / 24;

				seconds = seconds % 60;
				minutes = minutes % 60;
				hours = hours % 24;

				tv.setText(String.format("%02d:%02d:%02d:%02d", days, hours,
						minutes, seconds));
			}

			public void onFinish() {
				tv.setText("done!");
			}
		}.start();


	}

	/*public class DrawingTheCloud extends View{

		int x ,y;
		Bitmap cloud;
		int color = R.color.light_blue;

		public DrawingTheCloud(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			tv.setText("asdas");
			cloud = BitmapFactory.decodeResource(getResources(),R.drawable.moln1);
			x = 0;
			y = 0;
			invalidate();

		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			tv.setText("grsd");
			Rect ourRect = new Rect();
			ourRect.set(0, 0, canvas.getWidth(), canvas.getHeight()/2);
			Paint background = new Paint();
			background.setColor(color);
			background.setStyle(Paint.Style.FILL);
			canvas.drawRect(ourRect, background);
			if(x < canvas.getWidth()){
				x +=10;
			}else{
				x = 0;
			}
			if(y<canvas.getHeight()){
				y +=10;
			}else{
				y = 0;
			}
			canvas.drawBitmap(cloud, x, y, new Paint());
			invalidate();
		}	
	}*/
	public void startLyrics() {
		text = 0;
		totTime = 0;
		startTime = System.currentTimeMillis();
		lyric3.setText("");
		lyric1.setText(lyrics[1]);
		lyric2.setText(lyrics[0]);
		r = new Runnable() {
			public void run() {
				totTime += delays[text];
				text++;
				if(text>=delays.length ) {
					factory = new MySoundFactory(getActivity());
					factory.createLongMedia(songID, false);
					started = false;
					playing = false;
					play.setImageResource(R.drawable.playerbutton);						
					return;
				}
				if(text == delays.length-1) { 
					lyric1.setText("");
				} else {
					lyric1.setText(lyrics[text+1]);
				}
				lyric2.setText(lyrics[text]);
				lyric3.setText(lyrics[text-1]);

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
				play.setImageResource(R.drawable.playerbutton);
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

	public void stopMusic() {
		if(playing) {
			factory.pause(songID);
		}
	}


}

