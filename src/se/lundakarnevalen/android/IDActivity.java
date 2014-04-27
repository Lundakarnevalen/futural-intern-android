package se.lundakarnevalen.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class IDActivity extends Activity{


	private static final String LOG_TAG = "IDACTIVITY";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_id);

		//wrapper = (RelativeLayout) findViewById(R.id.);
		
		 Display display = getWindowManager().getDefaultDisplay();
	        int width = display.getWidth();  // deprecated
	        int height = display.getHeight();
	        ImageView cloud = (ImageView) findViewById(R.id.clouds_start);
	        Animation a = new TranslateAnimation(0,width,0 ,0);
	        ImageView movingCloud = (ImageView) findViewById(R.id.clouds_moving);
	        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        lp.setMargins(-width, 0, 0, 0);
	        lp.width = width;
	        lp.height = height;
	        movingCloud.setLayoutParams(lp);
	        
	        Animation a2 = new TranslateAnimation(0,width*2,0 ,0);
	        a2.setRepeatCount(Animation.INFINITE);
	        a.setDuration(15000);
	        a2.setDuration(30000);  
	        a2.setInterpolator(new AccelerateDecelerateInterpolator());
	        a2.setInterpolator(new LinearInterpolator());
            
	        
	        
	        ImageView movingCloud2 = (ImageView) findViewById(R.id.clouds_moving_2);
	        Animation a3 = new TranslateAnimation(0,width*2,0 ,0);
	        a3.setRepeatCount(Animation.INFINITE);
	        movingCloud2.setLayoutParams(lp);
	        a3.setDuration(30000);
	        a.setAnimationListener(new CloudStartListner(cloud, movingCloud2, a3));
	        a.setInterpolator(new AccelerateDecelerateInterpolator());
	        a.setInterpolator(new LinearInterpolator());
	        a3.setInterpolator(new AccelerateDecelerateInterpolator());
	        a3.setInterpolator(new LinearInterpolator());
           
	        cloud.startAnimation(a);
	       
	        movingCloud.startAnimation(a2);
	
	}

	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onStop(){
		super.onStop();
	}

	@Override
	public void onPause(){
		super.onPause();
	}


    private class CloudStartListner implements Animation.AnimationListener {

        ImageView view;
        ImageView movingCloud;
        Animation a2;

        public CloudStartListner(ImageView view, ImageView movingCloud, Animation a2) {
            this.view = view;
            this.movingCloud = movingCloud;
            this.a2 = a2;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            view.setVisibility(View.INVISIBLE);
            if(a2!=null)  {
                movingCloud.startAnimation(a2);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
