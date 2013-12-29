package se.lundakarnevalen.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
}
