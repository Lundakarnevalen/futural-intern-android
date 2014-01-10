package se.lundakarnevalen.android;

import java.util.ArrayList;

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
	private LKEditText name, email, password, mobilnbr, confirmemail, confirmpassword;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton;
	private boolean shirtchosen;
	private char shirtsize;
	private ArrayList<String> sektioner;
	
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
	
	private boolean equalEmails() {
		return email.getText().toString().equals(confirmemail.getText().toString());
	}
	
	private boolean validPassword(){ //borde checka lÔøΩngden
		return !password.getText().toString().equals("");
	}
	private boolean equalPasswords() {
		return password.getText().toString().equals(confirmpassword.getText().toString());
	}
	
	private boolean validMobileNumber(){ //borde checka lÔøΩngden
		return !mobilnbr.getText().toString().equals("");
	}
	private boolean validName(){
		return !name.getText().toString().equals("");
	}
	
	private boolean sectionChosen(){
		return sektioner!=null;
	}
	
	private boolean shirtChosen() {
		return shirtchosen;
	}
	
	private void updateProgressBar() {
		progresslevel = 0;
		if(validEmail()&&equalEmails())
			progresslevel += 10;
		if(validPassword()&&equalPasswords())
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
	    shirtchosen = false;
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_large:
	            if (checked){
	            	shirtchosen = true;
	            	shirtsize = 'L';
	            }
	            break;
	        case R.id.radio_medium:
	            if (checked){
	            	shirtchosen = true;
            		shirtsize = 'M';

	            }
	            break;
	        case R.id.radio_small:
	            if (checked){
	            	shirtchosen = true;
            		shirtsize = 'S';
	            }
	            break;
	    }
	    updateProgressBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.activity_registration_layout, null);
		name = (LKEditText) root.findViewById(R.id.lKEditTextName);
		email = (LKEditText) root.findViewById(R.id.lKEditTextEmail);
		confirmemail = (LKEditText) root.findViewById(R.id.lKEditTextConfirmEmail);
		confirmpassword = (LKEditText) root.findViewById(R.id.lKEditTextConfirmPassword);
		password = (LKEditText) root.findViewById(R.id.lKEditTextPassword);
		mobilnbr = (LKEditText) root.findViewById(R.id.lKEditTextMobilNbr);
		progressbar = (LKProgressBar) root.findViewById(R.id.lKProgressBar1);
		progressvalue = (LKTextView) root.findViewById(R.id.progress_value);
		confirmButton = (LKButton) root.findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirm);
		name.addTextChangedListener(watcher);		
		email.addTextChangedListener(watcher);
		confirmemail.addTextChangedListener(watcher);
		password.addTextChangedListener(watcher);
		confirmpassword.addTextChangedListener(watcher);
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
			updateProgressBar();
			if(progresslevel==70){
				LKUser user = new LKUser(getActivity().getApplicationContext());
				user.setUsername(name.getText().toString());
				user.setEmail(email.getText().toString());
				user.setPassword(password.getText().toString());
				user.setMobileNumber(mobilnbr.getText().toString());
				user.setShirtsize(shirtsize);
				user.setSektioner(sektioner);
				user.setToken("#steeze");
				user.storeUserLocaly();
				Toast.makeText(getContext(), "Saved data to shared prefferances.", Toast.LENGTH_SHORT).show();

				//create new registreringprogressfragment
			} else {
				String wrongs = "Dess fält är inte korrekt inmatade: ";
				
				if(!(validEmail()&&equalEmails()))
					wrongs += "E-post ";
				if(!(validPassword()&&equalPasswords()))
					wrongs += "Lösenord ";
				if(!validMobileNumber())
					wrongs += "Mobilnummer ";
				if(!validName())
					wrongs += "Namn ";
				if(!shirtChosen())
					wrongs += "Tröjstorlek ";
				if(!sectionChosen())
					wrongs += "Sektion ";
			
				Toast.makeText(getContext(), wrongs, Toast.LENGTH_LONG).show();

				
			}
			// Save some dummy data to have a "user" saved in SP. 
			//LKUser user = new LKUser(getActivity().getApplicationContext());
			//user.setUsername("Kalle anka");
			//user.setToken("#steeze");
			//user.storeUserLocaly();
			//Toast.makeText(getContext(), "Saved dummy user in SP. Set username and token in SP only. Clear app in android to remove.", Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	public void onSectionClicked(View view) {
	    boolean checked = ((RadioButton) view).isChecked();
	    sektioner = new ArrayList<String>();
		switch (view.getId()) {
		case R.id.radio_sektion_barnevalen:
			if (checked) {
				sektioner.add("Barnevalen");
			}
			break;
		case R.id.radio_sektion_biljetteriet:
			if (checked) {
				sektioner.add("Biljetteriet");
			}
			break;
		case R.id.radio_sektion_bladderiet:
			if (checked) {
				sektioner.add("Blädderiet");
			}
			break;
		case R.id.radio_sektion_cirkusen:
			if (checked) {
				sektioner.add("Cirkusen");
			}
			break;
		case R.id.radio_sektion_dansen:
			if (checked) {
				sektioner.add("Dansen");
			}
			break;
		case R.id.radio_sektion_ekonomi:
			if (checked) {
				sektioner.add("Ekonomi");
			}
			break;
		case R.id.radio_sektion_fabriken:
			if (checked) {
				sektioner.add("Fabriken");
			}
			break;
		case R.id.radio_sektion_festmasteriet:
			if (checked) {
				sektioner.add("Festmästeriet");
			}
			break;
		case R.id.radio_sektion_filmen:
			if (checked) {
				sektioner.add("Filmen");
			}
			break;
		case R.id.radio_sektion_kabare:
			if (checked) {
				sektioner.add("Kabaré");
			}
			break;
		case R.id.radio_sektion_klipperiet:
			if (checked) {
				sektioner.add("Klipperiet");
			}
			break;
		case R.id.radio_sektion_kommunikation:
			if (checked) {
				sektioner.add("Kommunikation");
			}
			break;
		case R.id.radio_sektion_krogarna:
			if (checked) {
				sektioner.add("Krogarna");
			}
			break;
		case R.id.radio_sektion_omradet:
			if (checked) {
				sektioner.add("Området");
			}
			break;
		case R.id.radio_sektion_musiken:
			if (checked) {
				sektioner.add("Musiken");
			}
			break;
		case R.id.radio_sektion_nojessektionen:
			if (checked) {
				sektioner.add("Nöjessektionen");
			}
			break;
		case R.id.radio_sektion_radion:
			if (checked) {
				sektioner.add("Radion");
			}
			break;
		case R.id.radio_sektion_revyn:
			if (checked) {
				sektioner.add("Revyn");
			}
			break;
		case R.id.radio_sektion_shoppen:
			if (checked) {
				sektioner.add("Shoppen");
			}
			break;
		case R.id.radio_sektion_showen:
			if (checked) {
				sektioner.add("Showen");
			}
			break;
		case R.id.radio_sektion_snaxeriet:
			if (checked) {
				sektioner.add("Snaxeriet");
			}
			break;
		case R.id.radio_sektion_sakerhet:
			if (checked) {
				sektioner.add("Säkerhet");
			}
			break;
		case R.id.radio_sektion_tombola:
			if (checked) {
				sektioner.add("Tombola");
			}
			break;
		case R.id.radio_sektion_taget:
			if (checked) {
				sektioner.add("Tåget");
			}
			break;
		case R.id.radio_sektion_taltnojen:
			if (checked) {
				sektioner.add("Tältnöjen");
			}
			break;
		case R.id.radio_sektion_vieriet:
			if (checked) {
				sektioner.add("Vieriet");
			}
			break;
		}
	}
	
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