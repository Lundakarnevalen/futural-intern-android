package se.lundakarnevalen.android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegistrationOhNoFragment extends LKFragment{
	View loginButton;
	View registerButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.registration_oh_no_layout, null);
		loginButton = root.findViewById(R.id.login_text);
		registerButton = root.findViewById(R.id.register);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		showActionBarLogo(true);
		if(loginButton != null)
			loginButton.setOnClickListener(loginClick);
		if(registerButton != null)
			registerButton.setOnClickListener(registerClick);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(LOG_TAG, "onResume");
		// check if user
		LKFragment fragment = getStartFragment(getContext());
		if(!(fragment instanceof RegistrationOhNoFragment)){
			Log.d(LOG_TAG, "change fragment!");
			loadFragment(fragment, false);
		}
	}
	
	View.OnClickListener registerClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LKFragment fragment = new RegistrationFragment();
			loadFragment(fragment, false);
		}
	};
	
	View.OnClickListener loginClick = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			LKFragment fragment = new SignInFragment();
			loadFragment(fragment, false);//HAX
		}
	};
}
