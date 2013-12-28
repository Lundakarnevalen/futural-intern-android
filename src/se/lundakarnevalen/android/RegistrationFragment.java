package se.lundakarnevalen.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
		View root = (View) inflater.inflate(R.layout.activity_registration_layout, null);
		
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
	}
}