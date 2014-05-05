package se.lundakarnevalen.android;

import java.io.ByteArrayOutputStream;

import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class IDActivity extends Activity implements OnClickListener{
	private String name;
	private String pnbr;
	private int sektion;
	private Bitmap picture;
	private boolean front = true;
	ImageView image;

	private String IMAGE_PATH = "karneval.image";
	private static final String LOG_TAG = "IDACTIVITY";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_id);

		
		Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        pnbr = intent.getExtras().getString("pnbr");
        sektion = Integer.parseInt(intent.getExtras().getString("sektion"));        
        String imgURL = intent.getExtras().getString("photo");        
        
        image = ((ImageView) findViewById(R.id.photo));

        SharedPreferences prefs = getSharedPreferences(IMAGE_PATH, Context.MODE_PRIVATE);
		String imageString = prefs.getString(IMAGE_PATH, null);
		if(imageString==null) {
    		     
        
		 LKRemote remote = new LKRemote(this);
    		remote.setBitmapResultListener(new BitmapResultListener() {
    			@Override
    			public void onResult(Bitmap result) {
    				if (result == null) {
    														// standard image?
    				}
    
    				SharedPreferences prefs = getSharedPreferences(IMAGE_PATH, Context.MODE_PRIVATE);
    				
    				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    				result.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
    				byte[] byteArray2 = byteArray.toByteArray();
    				
    				String encoded = Base64.encodeToString(byteArray2, Base64.DEFAULT);
    				prefs.edit().putString(IMAGE_PATH, encoded).commit();
    
    				
    				picture = result;
    				image.setImageBitmap(result);
    			}
    		});
    		remote.requestServerForBitmap(imgURL);

		} else {
			byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
			picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			image.setVisibility(View.VISIBLE);
			image.setImageBitmap(picture);
		
		}
		
    		
    
    		
    		
    		viewFront();
		//wrapper = (RelativeLayout) findViewById(R.id.);
    		
	         
	         
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
    
    private void viewFront() {
    	setContentView(R.layout.activity_id);
    	
    	RelativeLayout card = (RelativeLayout) findViewById(R.id.card);
       card.setOnClickListener(this);
    	((TextView) findViewById(R.id.name)).setText("Namn     "+name);
        ((TextView) findViewById(R.id.personnummer)).setText("Personnummer     "+pnbr);
        ((TextView) findViewById(R.id.sektion)).setText("Sektion     "+getSektion(sektion));
        
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
    	image = ((ImageView) findViewById(R.id.photo));
        if(picture != null) {
        	image.setImageBitmap(picture);
        }
    }


	@Override
	public void onClick(View v) {
		if(front) {
			viewBack();
			front = false;
		} else {
			viewFront();
			front = true;
		}
		
	}

	private void viewBack() {
		setContentView(R.layout.activity_id_back);
		LinearLayout card = (LinearLayout) findViewById(R.id.card);
	       card.setOnClickListener(this);
	    	
	}
	private String getSektion(int id) {
		switch (id) {
		case 0:
			return "Karnevalen";
		case 1:
			return "Barnevalen";
		case 2:
			return "Biljetteriet";
		case 3:
			return "Blädderiet";
		case 4:
			return "Cirkusen";
		case 5:
			return "Dansen";
		case 6:
			return "Ekonomin";
		case 7:
			return "Kabarén";
		case 8:
			return "Fabriken";
		case 9:
			return "Klipperiet";
		case 10:
			return "Kommunikationen";
		case 11:
			 return "Krogarna";
		case 12:
			return "Krogarna";
		case 13:
			return "Krogarna";
		case 14:
			return "Krogarna";
		case 15:
			return "Krogarna";
		case 16:
			return "Området";
		case 17:
			return "Musiken";
		case 18:
			return "Radion";
		case 19:
			return "Revyn";
		case 20:
			return "Shoppen";
		case 21:
			return "Showen";
		case 22:
			return "Snaxeriet";
		case 23:
			return "Spexet";
		case 24:
			return "Säkerhet";
		case 25:
			return "Tombolan";
		case 26:
			return "Vieriet";
		case 100:
			return "Festmästeriet";
		case 101:
			return "Festmästeriet";
		case 102:
			return "Festmästeriet";
		case 199:
			return "Festmästeriet";
		case 202:
			return "Smånöjen";
		case 203:
			return "Smånöjen";
		case 204:
			return "Smånöjen";
		case 300:
			return "Tåget";
		case 399:
			return "Tåget";
		case 400:
			return "Tältnöjen";
		case 499:
			return "Tältnöjen";
		}
		return "Okänd";
	}
}
