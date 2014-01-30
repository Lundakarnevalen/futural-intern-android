package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import android.os.Bundle;

public class AboutFragment extends LKFragment{
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Om appen");
		
	}
}
