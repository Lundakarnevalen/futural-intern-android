package fragments;

import java.util.Date;
import java.util.GregorianCalendar;

import se.lundakarnevalen.android.R;
import android.R.anim;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountdownFragment extends LKFragment {
	
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
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Countdown");
		
		
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
}
