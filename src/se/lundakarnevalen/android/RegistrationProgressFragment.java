package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegistrationProgressFragment extends LKFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.registrationprogress_layout, null);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
		LKUser user = new LKUser(getActivity().getApplicationContext());
	}
}
