package fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import se.lundakarnevalen.android.R;
import sound.MySoundFactory;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class CountdownFragment extends LKFragment {
	private MySoundFactory factory;
	private ImageView play;
	private boolean playing;
	private boolean started;
	private int songID = R.raw.lundakarneval; 
	private float tot;
	// Lyrics
	int text = 0;
	private long startTime;
	private int totTime;
	private TextView lyric1;
	private TextView lyric2;
	private TextView lyric3;
	private final Handler handler = new Handler();   
	private final Handler moveHandler = new Handler();   
	private Runnable r;
	private Runnable r2;
	private long pauseTime = -1;
	private	int[] delays ={11260,3456,2578,4451,4002,3671,3849,2518,5784,3684,3656,3707,2263,1786,1929,1735,1976,1770,4701,1344,1922,3856,1060,2269,3289,2462,2481,3802,2486,3166,6320,3723,3775,3809,2234,1858,1920,1828
			,1980,2055,4088,1824,1908,4479,1094,1782,3146,14278,4617,1638,1898,1851,1985,1554,3977,1863,1855,5567,1994,3678,1970,1859,3603,1810,2296,3347,1940,2047,3259,2317};
	//38
	private ArrayList<Countdown> list = new ArrayList<Countdown>();
	
	private Date karneVal;
	private Date karneLan;
	private Date preKarneval;
	private Date karneBeer;
	private Date postKarneval;
	private String[] lyrics;
	private int delay;
	//
	private ImageView mover;

	TextView tv;
	long diff;

	private TextView tvKarnevalTitle;
	private TextView tvKarneval;
	private TextView tvPreKarneval;
	private TextView tvPostKarneval;
	private TextView tvKarnevalBeer;
	private TextView tvKarneLan;
	long diffKarneVad;

 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Change the layout
		View rootView = inflater.inflate(R.layout.fr_layout_countdown,null);

		tvKarnevalTitle = (TextView) rootView.findViewById(R.id.tvKarnevalTitle);
		tvKarneval = (TextView) rootView.findViewById(R.id.tvKarneval);
		tvPreKarneval = (TextView) rootView.findViewById(R.id.tvPreKarneval);
		tvPostKarneval = (TextView) rootView.findViewById(R.id.tvPostKarneval);
		tvKarnevalBeer = (TextView) rootView.findViewById(R.id.tvKarnevalBeer);
		tvKarneLan = (TextView) rootView.findViewById(R.id.tvKarneLan);

		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Bold.ttf");
		tvKarneval.setTypeface(tf);

		karneVal = new GregorianCalendar(2014, 4, 16, 0, 0, 0).getTime();
	
		karneLan = new GregorianCalendar(2014, 3, 5, 0, 32, 30).getTime();
		//TODO Change time here before release
		//karneLan = new GregorianCalendar(2014, 3, 16, 18, 0, 0).getTime();
		preKarneval = new GregorianCalendar(2014, 4, 11, 0, 0, 0)
		.getTime();
		karneBeer = new GregorianCalendar(2014, 5, 25, 11, 0, 0).getTime();
		postKarneval = new GregorianCalendar(2014, 8, 20, 0, 0, 0)
		.getTime();

		Date today = new Date();
		long timeOfToday = today.getTime();

		long diffKarneval = postKarneval.getTime() - timeOfToday;

		if(karneVal.before(today)) {
			karneVal = null;
			tvKarneval.setText(getCountdownMessage(0));
		} else {
			list.add(new Countdown(tvKarneval, karneVal));	
		}
		if(karneLan.before(today)) {
			karneLan = null;	
			tvKarneLan.setText(getCountdownMessage(0));
			setFinish(rootView.findViewById(R.id.countDownKarnevalFour), ((ImageView) rootView.findViewById(R.id.checkMark4)));
		} else {
			list.add(new Countdown(tvKarneLan, karneLan));
		}
		if(preKarneval.before(today)) {
			preKarneval = null;
			tvPreKarneval.setText(getCountdownMessage(0));
			setFinish(rootView.findViewById(R.id.countDownKarnevalTwo),((ImageView) rootView.findViewById(R.id.checkMark2)));
		} else {
			list.add(new Countdown(tvPreKarneval, preKarneval));
		}
		if(karneBeer.before(today)) {
			karneBeer = null;
			tvKarnevalBeer.setText(getCountdownMessage(0));
			setFinish(rootView.findViewById(R.id.countDownKarnevalThree),((ImageView) rootView.findViewById(R.id.checkMark3)));
		} else {
			list.add(new Countdown(tvKarnevalBeer, karneBeer));
		}
		if(postKarneval.before(today)) {
			postKarneval = null;
			tvPostKarneval.setText(getCountdownMessage(0));
			setFinish(rootView.findViewById(R.id.countDownKarnevalOne),((ImageView) rootView.findViewById(R.id.checkMark1)));
		} else {
			list.add(new Countdown(tvPostKarneval, postKarneval));
		}
		
		CountDownTask countDownTask = new CountDownTask();
		//countDownTask.execute(diffKarneval, diffKarnelan, diffPreKarneval,
		//		diffKarnebeer, diffPostKarneval);
		countDownTask.execute(diffKarneval);


		//new DrawingTheCloud(this.getContext());
		mover = (ImageView) rootView.findViewById(R.id.music_handle1);

		if(started) {
			Matrix matrix = new Matrix();
			mover.setScaleType(ImageView.ScaleType.MATRIX);
			matrix.set(mover.getImageMatrix());
			float part = ((float)(System.currentTimeMillis()-startTime))/217000;
			float move = tot*part;

			matrix.postTranslate(move,0);

			mover.setImageMatrix(matrix);

		}
		lyric1 = (TextView) rootView.findViewById(R.id.lyric1);
		lyric2 = (TextView) rootView.findViewById(R.id.lyric2);
		lyric3 = (TextView) rootView.findViewById(R.id.lyric4);



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
			lyric1.setText("");
			lyric2.setText("");
			lyric3.setText("");
			factory = new MySoundFactory(getActivity());
			factory.createLongMedia(songID, false);

			playing = false;
			started = false;

		} else {
			tvKarnevalTitle.setVisibility(View.INVISIBLE);
			tvKarneval.setVisibility(View.INVISIBLE);
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

	private void setFinish(View tv, ImageView img) {
		// TODO Auto-generated method stub
		tv.setBackgroundResource(R.drawable.bluegray_bg_bottom_shadow);
		
		img.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.countdown_title));
	}

	private class CountDownTask extends AsyncTask<Long, Long, Long> {

		@Override
		protected Long doInBackground(Long... params) {

			final long diffPostKarneval = params[0];
		//	final long diffKarneval = params[1];
		//	final long diffKarneLan = params[2];
		//	final long diffPreKarneval = params[3];
		//	final long diffKarnebeer = params[4];
			publishProgress(diffPostKarneval);
	//		publishProgress(diffKarneval, diffKarneLan, diffPreKarneval,
	//				diffKarnebeer, diffPostKarneval);
			return (long) 0;
		}
		@Override
		protected void onProgressUpdate(Long... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			long diffKarneval = values[0];

			new CountDownTimer(diffKarneval, 1000) {
				@Override
				public void onFinish() {
				
				}

				@Override
				public void onTick(long millisUntilFinished) {
					
					Date today = new Date();
					long timeOfToday = today.getTime();

					for(Countdown c: list) {
						long diff = c.date.getTime() - timeOfToday;
						if(diff < 0) {
							diff = 0;
						}
						c.textView.setText(getCountdownMessage(diff));
					}
					
				}
			}.start();

			/*
			new CountDownTimer(diffKarneval, 1000) {
				@Override
				public void onFinish() {
				}

				@Override
				public void onTick(long millisUntilFinished) {
					Log.d("time:",""+ millisUntilFinished);
					tvKarneval
					.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();

			new CountDownTimer(diffKarneLan, 1000) {
				@Override
				public void onFinish() {
					// Change into checked
				}

				@Override
				public void onTick(long millisUntilFinished) {
					tvKarneLan
					.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();

			new CountDownTimer(diffPreKarneval, 1000) {
				@Override
				public void onFinish() {
					// Change into checked
				}

				@Override
				public void onTick(long millisUntilFinished) {
					tvPreKarneval
					.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();

			new CountDownTimer(diffKarnebeer, 1000) {
				@Override
				public void onFinish() {
					// Change into checked
				}

				@Override
				public void onTick(long millisUntilFinished) {
					tvKarnevalBeer
					.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();

			new CountDownTimer(diffPostKarneval, 1000) {
				@Override
				public void onFinish() {
					// Change into checked
				}

				@Override
				public void onTick(long millisUntilFinished) {
					tvPostKarneval
					.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
			 */
		}
		@Override
		protected void onPostExecute(Long result) {

			super.onPostExecute(result);
		}

	}
	public void startLyrics() {



		text = 0;
		totTime = 0;
		startTime = System.currentTimeMillis();


		startMover();

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
		if(text != delays.length-1) {				

			lyric1.setText(lyrics[text+1]);

		}
		if(text != 0) {				
			lyric3.setText(lyrics[text-1]);			
		}
		lyric2.setText(lyrics[text]);
		
		
		handler.postDelayed(r, delay); 
		moveHandler.post(r2);
	}

	private class PlayButton implements OnClickListener {
		@Override
		public void onClick(View v) {

			if(playing) {

				lyric1.setText("");
				lyric2.setText("");
				lyric3.setText("");
				tvKarnevalTitle.setVisibility(View.VISIBLE);
				tvKarneval.setVisibility(View.VISIBLE);
				handler.removeCallbacks(r);
				moveHandler.removeCallbacks(r2);
				pauseTime = System.currentTimeMillis();
				delay = (totTime + delays[text])-(int)(pauseTime-startTime);
				factory.pause(songID);
				play.setImageResource(R.drawable.playerbutton);
				playing = false;

			} else {
				playing = true;
				if(started) {
					tvKarnevalTitle.setVisibility(View.INVISIBLE);
					tvKarneval.setVisibility(View.INVISIBLE);
					startTime += (System.currentTimeMillis()-pauseTime);
					resumeLyrics(delay);
					factory.resume(songID);
				} else {
					tvKarnevalTitle.setVisibility(View.INVISIBLE);
					tvKarneval.setVisibility(View.INVISIBLE);
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


	private void startMover() {
		// TODO Auto-generated method stub


		//WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		//	Display display = wm.getDefaultDisplay();

		int[] img_coordinates = new int[2];
		mover.getLocationOnScreen(img_coordinates);
		/*
	Display display = getActivity().getWindowManager().getDefaultDisplay();
    DisplayMetrics outMetrics = new DisplayMetrics ();
    display.getMetrics(outMetrics);
	float density  = getResources().getDisplayMetrics().density;
		 */
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		//Display display = getWindowManager().getDefaultDisplay(); 
		@SuppressWarnings("deprecation")
		int width = display.getWidth();  // deprecated


		tot  = width - img_coordinates[0] - Math.round((20.0/65.0)*img_coordinates[0]);

		r2 = new Runnable() {
			float taken = 0;
			public void run() {
				Matrix matrix = new Matrix();
				mover.setScaleType(ImageView.ScaleType.MATRIX);
				matrix.set(mover.getImageMatrix());

				float part = ((float)(System.currentTimeMillis()-startTime))/217000;
				float move = tot*part;
				if(part >= 1) {
				
					matrix.postTranslate(-taken,0);
					mover.setImageMatrix(matrix);
					return;
				}
				matrix.postTranslate(move-taken,0);
				taken = move;

				mover.setImageMatrix(matrix);
				moveHandler.postDelayed(this, 500);
			}
		};

		moveHandler.post(r2); 
		//217 sek
	}



	private String getCountdownMessage(long diffKarneval) {
		long seconds = diffKarneval / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;
		String countDownMessage = String.format("%02d:%02d:%02d:%02d", days,
				hours, minutes, seconds);
		return countDownMessage;
	}
	private class Countdown {
		private TextView textView;
		private Date date;
		public Countdown(TextView textView, Date date) {
			this.textView = textView;
			this.date = date;
		}
	}

}

