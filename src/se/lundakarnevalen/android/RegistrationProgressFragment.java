package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKProgressBar;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegistrationProgressFragment extends LKFragment{
	LKTextView name, email, persnnbr, progressValue, tr1, tr2a;
	LKTextViewBold tr2b;
	LKButton edit;
	LKProgressBar pr;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.registrationprogress_layout, null);
		name = (LKTextView) root.findViewById(R.id.name);
		email = (LKTextView) root.findViewById(R.id.email);
		persnnbr = (LKTextView) root.findViewById(R.id.persnnbr);
		edit = (LKButton) root.findViewById(R.id.edit_button);
		pr = (LKProgressBar) root.findViewById(R.id.progressBar1);
		progressValue = (LKTextView) root.findViewById(R.id.progress_value);
		tr1 = (LKTextView) root.findViewById(R.id.lKTextView2);
		tr2a = (LKTextView) root.findViewById(R.id.lKTextView3);
		tr2b = (LKTextViewBold) root.findViewById(R.id.lKTextViewBold2);
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadFragment(new RegistrationFragment(), true);
			}
		});
		return root;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(LOG_TAG, "check if registred");
		// check if user
		LKFragment fragment = getStartFragment(getContext());
		if(!(fragment instanceof RegistrationProgressFragment)){
			Log.d(LOG_TAG, "change fragment!");
			loadFragment(fragment, false);
		}
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
			Log.d(LOG_TAG, "App is locked");
			loadFragment(LKFragment.getStartFragment(getContext()), false);
		}
		
		name.setText(user.fornamn+" "+user.efternamn);
		email.setText(user.email);
		persnnbr.setText(user.personnummer);
		
		SharedPreferences sp = getContext().getSharedPreferences(LKFragment.SP_NAME, Context.MODE_PRIVATE);
		int registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		switch(registrationStep){
		case 0:
			pr.setProgress(1);
			progressValue.setText("1%");
			break;
		case 1:
			pr.setProgress(40);
			progressValue.setText("40%");
			break;
		case 2:
			pr.setProgress(64);
			progressValue.setText("64%");
			break;
		case 3:
			pr.setProgress(86);
			progressValue.setText("86%");
			break;
		}
		
		// Set text:
		switch(registrationStep){
		case 0:
		case 1:
		case 2:
			tr1.setVisibility(View.GONE);
			tr2a.setText(getContext().getString(R.string.progress_notdone_1));
			tr2b.setText(getContext().getString(R.string.progress_notdone_2));
			break;
		case 3:
		default:
			tr1.setVisibility(View.VISIBLE);
			tr1.setText(getContext().getString(R.string.registration_informationafterorigressbar2));
			tr2a.setText(getContext().getString(R.string.registration_informationbild1));
			tr2b.setText(getContext().getString(R.string.registration_informationbild2));
			break;
		}
	}
}
