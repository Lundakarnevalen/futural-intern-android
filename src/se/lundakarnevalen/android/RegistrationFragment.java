package se.lundakarnevalen.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RegistrationFragment extends LKFragment{
	private int progesslevel;
	private SharedPreferences shpref;
	
	
	public boolean isRegistationCorrect(){
		return false;
	}
	
	public boolean validEmail(){
		return false;
	}
	
	public boolean validPassword(){
		return false;
	}
	
	public boolean validMobileNumber(){
		return false;
	}
	
	public boolean sectionChosen(){
		return false;
	}
	
	public boolean shirtChosen() {
		return false;
	}
	public void saveRegistration() {	
	}
	
	public void updateProgessBar() {
		
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