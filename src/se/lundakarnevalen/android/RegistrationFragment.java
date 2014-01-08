package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKEditText;
import se.lundakarnevalen.widget.LKProgressBar;
import se.lundakarnevalen.widget.LKTextView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegistrationFragment extends LKFragment{
	private int progresslevel;
	private SharedPreferences shpref;
	private LKEditText name, email, password, mobilnbr;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton;
	
	private boolean isRegistationCorrect(){
		return false;
	}
	
	private boolean validEmail(){
		if(email.getText().toString().equals(""))
			return false;
		char[] emailarr = email.getText().toString().toCharArray();
		boolean contatt = false;
		int i = 0;
		for(char c:emailarr) {
			i++;
			if(c == '@'){
				if(i==1) {
					return false;
				}
				contatt = true;
				break;
			}	
		}
		int savedi = i;
		int lastdot = 0;
		if(contatt) {
			boolean containsdot = false;
			for(; i<emailarr.length; i++) {
				if(emailarr[i] == '@')
					return false;
				if(emailarr[i] == '.'){
					if(i == savedi)
						return false;
					containsdot = true;
					lastdot = i;
				}
			}
			return (lastdot<emailarr.length - 1) && containsdot;
		}
		return false;
	}
	
	private boolean validPassword(){ //borde checka l�ngden
		return !password.getText().toString().equals("");
	}
	
	private boolean validMobileNumber(){ //borde checka l�ngden
		return !mobilnbr.getText().toString().equals("");
	}
	private boolean validName(){
		return !name.getText().toString().equals("");
	}
	
	private boolean sectionChosen(){
		return false;
	}
	
	private boolean shirtChosen() {
		return false;
	}
	
	private void saveRegistration() {	
	}
	
	private void updateProgressBar() {
		progresslevel = 0;
		if(validEmail())
			progresslevel += 10;
		if(validPassword())
			progresslevel += 10;
		if(validMobileNumber())
			progresslevel += 10;
		if(validName())
			progresslevel += 10;
		if(shirtChosen())
			progresslevel += 10;
		if(sectionChosen())
			progresslevel += 20;
		if(progresslevel == 0) {
			progresslevel++;
		}
		progressbar.setProgress(progresslevel);
		progressvalue.setText("" + progresslevel + " %");
	}
	
	@Override
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_large:
	            if (checked)
	                //save L
	            break;
	        case R.id.radio_medium:
	            if (checked)
	                //save M
	            break;
	        case R.id.radio_small:
	            if (checked)
	               //save S
	            break;
	    }
	    updateProgressBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.activity_registration_layout, null);
		name = (LKEditText) root.findViewById(R.id.lKEditTextName);
		email = (LKEditText) root.findViewById(R.id.lKEditTextEmail);
		password = (LKEditText) root.findViewById(R.id.lKEditTextPassword);
		mobilnbr = (LKEditText) root.findViewById(R.id.lKEditTextMobilNbr);
		progressbar = (LKProgressBar) root.findViewById(R.id.lKProgressBar1);
		progressvalue = (LKTextView) root.findViewById(R.id.progress_value);
		confirmButton = (LKButton) root.findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirm);
		name.addTextChangedListener(watcher);
		email.addTextChangedListener(watcher);
		password.addTextChangedListener(watcher);
		mobilnbr.addTextChangedListener(watcher);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
	}
	
	View.OnClickListener confirm = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Save some dummy data to have a "user" saved in SP. 
			LKUser user = new LKUser(getActivity().getApplicationContext());
			user.setUsername("Kalle anka");
			user.setToken("#steeze");
			user.storeUserLocaly();
			Toast.makeText(getContext(), "Saved dummy user in SP. Set username and token in SP only. Clear app in android to remove.", Toast.LENGTH_SHORT).show();
		}
	};
	
	TextWatcher watcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			updateProgressBar();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {	
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {			
		}
		
	};

}