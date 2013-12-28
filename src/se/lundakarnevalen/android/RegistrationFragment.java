package se.lundakarnevalen.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;

public class RegistrationFragment extends LKFragment{
	private int progesslevel;
	private SharedPreferences shpref;
	
	public boolean isRegistationCorrect(){
		return false;
	}
	
	public void saveRegistration() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView tv = new TextView(getActivity());
		tv.setText("Registrering");
		
		return tv;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
	}
}