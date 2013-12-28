package se.lundakarnevalen.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RegistrationOhNoFragment extends LKFragment{
	View loginButton;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.registration_oh_no_layout, null);
		loginButton = root.findViewById(R.id.login_text);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		showActionBarLogo(true);
		if(loginButton != null)
			loginButton.setOnClickListener(loginClick);
	}
	
	View.OnClickListener loginClick = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			LKFragment fragment = new SignInFragment();
			loadFragment(fragment, true);
		}
	};
}
