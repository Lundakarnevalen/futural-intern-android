package se.lundakarnevalen.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class SplashscreenActivity extends Activity{
	
	RelativeLayout wrapper;
	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen_layout);
		this.context = this;
		
		// init some stuff
		populateSp();
		
		wrapper = (RelativeLayout) findViewById(R.id.splash_wrapper);
		wrapper.setOnClickListener(cont);
	}
	
	View.OnClickListener cont = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, ContentActivity.class);
			context.startActivity(intent);
		}
	};
	
	/** 
	 * Populate SP with some basic data if there is none.
	 */
	private void populateSp(){
		// Init with registration data if none exists. 
		SharedPreferences sp = this.getSharedPreferences(LKFragment.SP_NAME, MODE_PRIVATE);
		int registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, -1);
		
		// Set values
		Editor editor = sp.edit();
		if(registrationStep == -1){
			editor.putInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		}
		
		editor.commit();
	}
}
