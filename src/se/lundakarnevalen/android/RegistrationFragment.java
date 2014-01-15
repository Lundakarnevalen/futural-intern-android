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
import android.widget.CheckBox;
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
				LKFragment fragment = new RegistrationProgressFragment();
				loadFragment(fragment, true);
				Toast.makeText(getContext(), "Saved data to shared prefferances.", Toast.LENGTH_SHORT).show();
			} else {
				String wrongs = "Dessa fält är inte korrekt inmatade: \n";
				
				if(!(validEmail()&&equalEmails()))
					wrongs += "E-post \t";
				if(!(validPassword()&&equalPasswords()))
					wrongs += "Lösenord \t";
				if(!validMobileNumber())
					wrongs += "Mobilnummer \t";
				if(!validName())
					wrongs += "Namn \t";
				if(!shirtChosen())
					wrongs += "Tröjstorlek \t";
				if(!sectionChosen())
					wrongs += "Sektion \t";
			
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
	public void onCheckBoxClicked(View view) {
	    boolean checked = ((CheckBox) view).isChecked();
	    if(sektioner == null)
	    	sektioner = new ArrayList<String>();
		switch (view.getId()) {
		case R.id.checkbox_barnevalen:
			if (checked)
				sektioner.add("Barnevalen");
			else 
				sektioner.remove("Barnevalen");
			break;
		case R.id.checkbox_biljetteriet:
			if (checked) 
				sektioner.add("Biljetteriet");
			else
				sektioner.remove("Biljetteriet");
			break;
		case R.id.checkbox_bladderiet:
			if (checked) 
				sektioner.add("Blädderiet");
			else
				sektioner.remove("Blädderiet");
			break;
		case R.id.checkbox_cirkusen:
			if (checked) 
				sektioner.add("Cirkusen");
			else				
				sektioner.remove("Cirkusen");
			break;
		case R.id.checkbox_dansen:
			if (checked) 
				sektioner.add("Dansen");
			else
				sektioner.remove("Dansen");
			break;
		case R.id.checkbox_ekonomi:
			if (checked) 
				sektioner.add("Ekonomi");
			else
				sektioner.remove("Ekonomi");
			break;
		case R.id.checkbox_fabriken:
			if (checked) 
				sektioner.add("Fabriken");
			else
				sektioner.remove("Fabriken");
			break;
		case R.id.checkbox_festmasteriet:
			if (checked) 
				sektioner.add("Festmästeriet");
			else
				sektioner.remove("Festmästeriet");
			break;
		case R.id.checkbox_filmen:
			if (checked) 
				sektioner.add("Filmen");
			else
				sektioner.remove("Filmen");
			break;
		case R.id.checkbox_kabare:
			if (checked) 
				sektioner.add("Kabaré");
			else
				sektioner.remove("Kabaré");
			break;
		case R.id.checkbox_klipperiet:
			if (checked) 
				sektioner.add("Klipperiet");
			else
				sektioner.remove("Klipperiet");
			break;
		case R.id.checkbox_kommunikation:
			if (checked) 
				sektioner.add("Kommunikation");
			else
				sektioner.remove("Kommunikation");
			break;
		case R.id.checkbox_krogarna:
			if (checked) 
				sektioner.add("Krogarna");
			else
				sektioner.remove("Krogarna");
			break;
		case R.id.checkbox_omradet:
			if (checked) 
				sektioner.add("Området");
			else
				sektioner.remove("Området");
			break;
		case R.id.checkbox_musiken:
			if (checked) 
				sektioner.add("Musiken");
			else
				sektioner.remove("Musiken");
			break;
		case R.id.checkbox_nojessektionen:
			if (checked) 
				sektioner.add("Nöjessektionen");
			else
				sektioner.remove("Nöjessektionen");
			break;
		case R.id.checkbox_radion:
			if (checked) 
				sektioner.add("Radion");
			else
				sektioner.remove("Radion");
			break;
		case R.id.checkbox_revyn:
			if (checked) 
				sektioner.add("Revyn");
			else
				sektioner.remove("Revyn");
			break;
		case R.id.checkbox_shoppen:
			if (checked) 
				sektioner.add("Shoppen");
			else
				sektioner.remove("Shoppen");
			break;
		case R.id.checkbox_showen:
			if (checked) 
				sektioner.add("Showen");
			else
				sektioner.remove("Showen");
			break;
		case R.id.checkbox_snaxeriet:
			if (checked) 
				sektioner.add("Snaxeriet");
			else
				sektioner.remove("Snaxeriet");
			break;
		case R.id.checkbox_sakerhet:
			if (checked) 
				sektioner.add("Säkerhet");
			else
				sektioner.remove("Säkerhet");
			break;
		case R.id.checkbox_tombola:
			if (checked) 
				sektioner.add("Tombola");
			else
				sektioner.remove("Tombola");
			break;
		case R.id.checkbox_taget:
			if (checked) 
				sektioner.add("Tåget");
			else
				sektioner.remove("Tåget");
			break;
		case R.id.checkbox_taltnojen:
			if (checked) 
				sektioner.add("Tältnöjen");
			else
				sektioner.remove("Tältnöjen");
			break;
		case R.id.checkbox_vieriet:
			if (checked) 
				sektioner.add("Vieriet");
			else
				sektioner.remove("Vieriet");
			break;
		}
		if(sektioner.size() == 0) 
			sektioner = null;
		updateProgressBar();
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