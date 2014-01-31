package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKTextView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegistrationProgressFragment extends LKFragment{
	LKTextView name, email, phone;
	LKButton edit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.registrationprogress_layout, null);
		name = (LKTextView) root.findViewById(R.id.name);
		email = (LKTextView) root.findViewById(R.id.email);
		phone = (LKTextView) root.findViewById(R.id.phone);
		edit = (LKButton) root.findViewById(R.id.edit_button);
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadFragment(new RegistrationFragment(), true);
			}
		});
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
		Log.d(LOG_TAG, "onActivityCreated");
		LKUser user = new LKUser(getActivity().getApplicationContext());
		user.getUserLocaly();
		
		if(appIsLocked(user)){
			// TODO: Open fragment to show user details. 
		}
		
		name.setText(user.fornamn+" "+user.efternamn);
		email.setText(user.email);
		phone.setText(user.telnr);		
	}
}
