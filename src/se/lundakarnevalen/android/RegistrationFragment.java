package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import json.Response;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKEditText;
import se.lundakarnevalen.widget.LKProgressBar;
import se.lundakarnevalen.widget.LKSpinner;
import se.lundakarnevalen.widget.LKSpinnerArrayAdapter;
import se.lundakarnevalen.widget.LKSpinnerArrayAdapter.LKSpinnerArrayItem;
import se.lundakarnevalen.widget.LKTextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;

public class RegistrationFragment extends LKFragment{
	private int registrationStep = 0; // 0 = personuppgifter, 1 = kod, 2 = karnevalsuppgifter, 3 = redigera (visa allt f√∂rutom koden). 
	private int progresslevel;
	private LKEditText code, name, lastname, email, validemail, mobilnbr, adress, zipcode, city, personnumber, foodpref, engageradKar, engageradNation, engageradStudentikos, engageradEtc, ovrigt, terms;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton, appendButton;
	private LKSpinner nationsSpinner, shirtSpinner, driverLicensSpinner, sexSpinner;
	private ArrayList<Integer> sektioner;
	private ArrayList<Integer> intressen;

	
	// Wrappers
	private LinearLayout wrapperPers, wrapperCode, wrapperLK;
	
	//spinner values
	int nation = 0, shirtSize = 0, driverLicens = 0, sex = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// Get registrationstep
		SharedPreferences sp = getContext().getSharedPreferences(LKFragment.SP_NAME, Context.MODE_PRIVATE);
		registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		
		View root = (View) inflater.inflate(R.layout.registration_layout, null);
		personnumber = (LKEditText) root.findViewById(R.id.pers_nr);
		name = (LKEditText) root.findViewById(R.id.name);
		lastname = (LKEditText) root.findViewById(R.id.lastname);
		email = (LKEditText) root.findViewById(R.id.email);
		validemail = (LKEditText) root.findViewById(R.id.email_valid);
		mobilnbr = (LKEditText) root.findViewById(R.id.phone_nr);
		adress = (LKEditText) root.findViewById(R.id.postal_address);
		zipcode = (LKEditText) root.findViewById(R.id.zip_code);
		city = (LKEditText) root.findViewById(R.id.zip_town);
		foodpref = (LKEditText) root.findViewById(R.id.food_prefs);
		engageradKar = (LKEditText) root.findViewById(R.id.teknologkaren);
		engageradNation = (LKEditText) root.findViewById(R.id.nationDed);
		engageradStudentikos = (LKEditText) root.findViewById(R.id.otherStud);
		engageradEtc = (LKEditText) root.findViewById(R.id.otherDed);
		ovrigt = (LKEditText) root.findViewById(R.id.other);
		terms = (LKEditText) root.findViewById(R.id.terms);
		progressbar = (LKProgressBar) root.findViewById(R.id.lKProgressBar1);
		progressvalue = (LKTextView) root.findViewById(R.id.progress_value);
		confirmButton = (LKButton) root.findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirm);
		personnumber.addTextChangedListener(watcher);
		name.addTextChangedListener(watcher);
		lastname.addTextChangedListener(watcher);
		email.addTextChangedListener(watcher);
		validemail.addTextChangedListener(watcher);
		mobilnbr.addTextChangedListener(watcher);
		adress.addTextChangedListener(watcher);
		zipcode.addTextChangedListener(watcher);
		city.addTextChangedListener(watcher);
		foodpref.addTextChangedListener(watcher);
		engageradKar.addTextChangedListener(watcher);
		engageradNation.addTextChangedListener(watcher);
		engageradStudentikos.addTextChangedListener(watcher);
		engageradEtc.addTextChangedListener(watcher);
		ovrigt.addTextChangedListener(watcher);
		terms.addTextChangedListener(watcher);
		nationsSpinner = (LKSpinner) root.findViewById(R.id.nations);
		nationsSpinner.setOnItemSelectedListener(nationsSpinnerListeners);
		sexSpinner = (LKSpinner) root.findViewById(R.id.sex);
		sexSpinner.setOnItemSelectedListener(sexSpinnerListeners);
		shirtSpinner = (LKSpinner) root.findViewById(R.id.shirt_size);
		shirtSpinner.setOnItemSelectedListener(shirtSpinnerListeners);
		driverLicensSpinner = (LKSpinner) root.findViewById(R.id.driver_licens);
		driverLicensSpinner.setOnItemSelectedListener(driverLicensSpinnerListeners);
		appendButton = (LKButton) root.findViewById(R.id.append_button);
		wrapperPers = (LinearLayout) root.findViewById(R.id.wrapper_pers);
		wrapperCode = (LinearLayout) root.findViewById(R.id.wrapper_code);
		wrapperLK = (LinearLayout) root.findViewById(R.id.wrapper_lk);
		code = (LKEditText) root.findViewById(R.id.continue_code);
		code.setOnEditorActionListener(sendEditorChangeListener);
		// Set correct views.
		updateLayout();
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
		
		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		if(appIsLocked(user)){
			loadFragment(new RegistrationProgressFragment(), false);
		}
		
		// Populate spinners
		List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> nationsList = new ArrayList<LKSpinnerArrayAdapter.LKSpinnerArrayItem>();
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Inget/Oklar", 0));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Blekingska", 1));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("G√∂teborgs", 2));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Hallands", 3));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Helsingkrona", 4));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Kalmar", 5));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Krischan", 6));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Lunds", 7));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Malm√∂", 8));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Sydsk√•nska", 9));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("V√§stg√∂ta", 10));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Wermlands", 11));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("√ñstg√∂ta", 12));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Sm√•lands", 13));
		LKSpinnerArrayAdapter nationsAdapter = new LKSpinnerArrayAdapter(getContext(), nationsList);
		nationsSpinner.setAdapter(nationsAdapter);
		
		List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> shirtSizeList = new ArrayList<LKSpinnerArrayAdapter.LKSpinnerArrayItem>();
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Oklar", 0));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("XS", 1));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("S", 2));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("M", 3));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("L", 4));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("XL", 5));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("XXL", 6));
		shirtSizeList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("XXXL", 7));
		LKSpinnerArrayAdapter shirtSizeAdapter = new LKSpinnerArrayAdapter(getContext(), shirtSizeList);
		shirtSpinner.setAdapter(shirtSizeAdapter);
		
		List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> driverLicensList = new ArrayList<LKSpinnerArrayAdapter.LKSpinnerArrayItem>();
		driverLicensList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Inget", 0));
		driverLicensList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("B/BE", 1));
		driverLicensList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("B/BE + C/CE", 2));
		driverLicensList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("B/BE + D/DE", 3));
		driverLicensList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("B/BE + C/CE + D/DE", 4));
		LKSpinnerArrayAdapter driverLicensSizeAdapter = new LKSpinnerArrayAdapter(getContext(), driverLicensList);
		driverLicensSpinner.setAdapter(driverLicensSizeAdapter);
		
		List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> sexList = new ArrayList<LKSpinnerArrayAdapter.LKSpinnerArrayItem>();
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Annat/Oklart", 0));
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Man", 1));
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Kvinna", 2));
		LKSpinnerArrayAdapter sexAdapter = new LKSpinnerArrayAdapter(getContext(), sexList);
		sexSpinner.setAdapter(sexAdapter);

	}
	
	/**
	 * Sets correct layout based on registrationstep
	 */
	private void updateLayout(){
		switch(registrationStep){
		case 0:
			// Personuppgifter
			wrapperPers.setVisibility(View.VISIBLE);
			confirmButton.setVisibility(View.VISIBLE);
			wrapperCode.setVisibility(View.GONE);
			wrapperLK.setVisibility(View.GONE);
			appendButton.setVisibility(View.GONE);
			break;
		case 1:
			// Koden
			wrapperPers.setVisibility(View.GONE);
			confirmButton.setVisibility(View.VISIBLE);
			wrapperCode.setVisibility(View.VISIBLE);
			wrapperLK.setVisibility(View.GONE);
			appendButton.setVisibility(View.GONE);
			break;
		case 2:
			// Karnevalsuppgifter
			wrapperPers.setVisibility(View.GONE); // NÔøΩr man klickar pÔøΩ ny knapp !! FIXA
			confirmButton.setVisibility(View.VISIBLE);
			wrapperCode.setVisibility(View.GONE);
			wrapperLK.setVisibility(View.VISIBLE);
			appendButton.setVisibility(View.VISIBLE);
			break;
		default:
			// Redigera alla uppgifterss
			wrapperPers.setVisibility(View.VISIBLE);
			confirmButton.setVisibility(View.VISIBLE);
			wrapperCode.setVisibility(View.GONE);
			wrapperLK.setVisibility(View.VISIBLE);
			appendButton.setVisibility(View.GONE);
			break;
		}
	}
	
	/**
	 * Stores registrationStep in SP. 
	 */
	private void storeRegistrationStep(){
		SharedPreferences sp = getContext().getSharedPreferences(LKFragment.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(LKFragment.SP_KEY_REGISTRATION_STEP, registrationStep);
		editor.commit();
	}
	
	private boolean isEditTextFilled(LKEditText et){
		return !et.getText().toString().equals("");
	}
	private boolean isEditTextOfThisLenght(LKEditText et, int size){
		return et.getText().toString().length() == size;
	}
	private boolean isEditTextLongerThanThisLength(LKEditText et, int size){
		return et.getText().toString().length() > size;
	}
	
	private boolean sectionChosen(){
		return sektioner!=null && sektioner.size()>=5;
	}
	private boolean intressenChosen(){
		return intressen!=null && intressen.size()>=3;
	}
	
	private void updateProgressBar() {
		progresslevel = 0;
		
		if(isEditTextFilled(name))
			progresslevel += 5;
		if(isEditTextFilled(lastname))
			progresslevel += 5;
		if(isEditTextOfThisLenght(personnumber,10))
			progresslevel += 5;
		if(isEditTextLongerThanThisLength(mobilnbr,4))
			progresslevel += 5;
		if(validEmail(email.getText().toString()) && equalEmail(email.getText().toString(), validemail.getText().toString()))
			progresslevel += 5;
		if(isEditTextFilled(adress))
			progresslevel += 5;
		if(isEditTextOfThisLenght(zipcode,5))
			progresslevel += 5;
		if(isEditTextFilled(city))
			progresslevel += 5;
		if(sectionChosen())
			progresslevel += 5;
		if(intressenChosen())
			progresslevel += 5;
		if(progresslevel == 0) {
			progresslevel++;
		}
		
		progressbar.setProgress(progresslevel);
		progressvalue.setText("" + progresslevel + " %");
	}
	
	/**
	 * Validates form based on registrationStep. 
	 * @return if the form is filled out correctly.
	 */
	private boolean validateForm(){
		return validateFormArray().size() == 0;
	}
	
	private ArrayList<String> validateFormArray(){
		Log.d(LOG_TAG, "validate form");
		ArrayList<String> Wrongs = new ArrayList<String>();
		switch(registrationStep){
		case 0:
			if(!isEditTextFilled(name))
				Wrongs.add("Inget förnamn ifyllt");
			if(!isEditTextFilled(lastname))
				Wrongs.add("Inget efternamn ifyllt");
			if(!isEditTextOfThisLenght(personnumber,10))
				Wrongs.add("Personnummer är ej korrekt ifyllt");
			if(!isEditTextLongerThanThisLength(mobilnbr,4))
				Wrongs.add("Inget giltigt mobilnummer är ifyllt");
			if(!validEmail(email.getText().toString()))
				Wrongs.add("MailAdressen har ett ogiltigt format");
			if(!equalEmail(email.getText().toString(), validemail.getText().toString()))
				Wrongs.add("Mailadresserna är inte lika.");
			if(!isEditTextFilled(adress))
				Wrongs.add("Ingen adress är ifylld");
			if(!isEditTextOfThisLenght(zipcode,5))
				Wrongs.add("Inget postnummer är ifyllt");
			if(!isEditTextFilled(city))
				Wrongs.add("Ingen stad är ifylld");
			break;
		case 2:
			if(!sectionChosen())
				Wrongs.add("Du måste välja minst 5 sektioner");
			if(!intressenChosen())
				Wrongs.add("Du måste välja minst 3 intressen");
			break;
		case 3:
			if(!isEditTextFilled(name))
				Wrongs.add("Inget förnamn ifyllt");
			if(!isEditTextFilled(lastname))
				Wrongs.add("Inget efternamn ifyllt");
			if(!isEditTextOfThisLenght(personnumber,10))
				Wrongs.add("Personnummer är ej korrekt ifyllt");
			if(!isEditTextLongerThanThisLength(mobilnbr,4))
				Wrongs.add("Inget giltigt mobilnummer är ifyllt");
			if(!validEmail(email.getText().toString()))
				Wrongs.add("MailAdressen har ett ogiltigt format");
			if(!equalEmail(email.getText().toString(), validemail.getText().toString()))
				Wrongs.add("Mailadresserna är inte lika.");
			if(!isEditTextFilled(adress))
				Wrongs.add("Ingen adress är ifylld");
			if(!isEditTextOfThisLenght(zipcode,5))
				Wrongs.add("Inget postnummer är ifyllt");
			if(!isEditTextFilled(city))
				Wrongs.add("Ingen stad är ifylld");
			if(!sectionChosen())
				Wrongs.add("Du måste välja minst 5 sektioner");
			if(!intressenChosen())
				Wrongs.add("Du måste välja minst 3 intressen");
		}
		return Wrongs;
	}

	
	AdapterView.OnItemSelectedListener nationsSpinnerListeners = new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			LKSpinnerArrayAdapter.LKSpinnerArrayItem item = (LKSpinnerArrayItem) parent.getItemAtPosition(pos);
			nation = item.value;
			Log.d(LOG_TAG, "nations new value: "+nation);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing
		}
		
	};
	AdapterView.OnItemSelectedListener sexSpinnerListeners = new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			LKSpinnerArrayAdapter.LKSpinnerArrayItem item = (LKSpinnerArrayItem) parent.getItemAtPosition(pos);
			sex = item.value;
			Log.d(LOG_TAG, "sex new value: "+sex);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing
		}
		
	};
	
	AdapterView.OnItemSelectedListener driverLicensSpinnerListeners = new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			LKSpinnerArrayAdapter.LKSpinnerArrayItem item = (LKSpinnerArrayItem) parent.getItemAtPosition(pos);
			driverLicens = item.value;
			Log.d(LOG_TAG, "driverLicens new value: "+driverLicens);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing
		}
		
	};
	
	AdapterView.OnItemSelectedListener shirtSpinnerListeners = new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			LKSpinnerArrayAdapter.LKSpinnerArrayItem item = (LKSpinnerArrayItem) parent.getItemAtPosition(pos);
			shirtSize = item.value;
			Log.d(LOG_TAG, "shirtsize new value: "+shirtSize);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing
		}
		
	};
	
	View.OnClickListener confirm = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			confirm();
		}
	};
	
	OnEditorActionListener sendEditorChangeListener = new OnEditorActionListener(){
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE))
				confirm();
			return false;
		}		
	};
	
	private void confirm(){
		Log.d(LOG_TAG, "confirm");
		if(validateForm()){
			Log.d(LOG_TAG, ""+registrationStep);
			LKUser user = new LKUser(getContext());
			switch(registrationStep){
			case 0:
				populateUserWithData(user);
				user.storeUserLocaly();
				registrationStep++;
				storeRegistrationStep();
				updateLayout();
				break;
			case 1:
				// Check code and update
				if(checkCode()){
					registrationStep++;
					storeRegistrationStep();
					updateLayout();
				}
				break;
			case 2:
				populateUserWithData(user);
				user.storeUserLocaly();
				registrationStep++;
				storeRegistrationStep();
				postNewUser(user);
				break;
			case 3:
				populateUserWithData(user);
				user.storeUserLocaly();
				putNewUser(user);
				break;
			}
		}
	}
	
	private void putNewUser(LKUser user){
		Log.d(LOG_TAG, "put new user");
		LKRemote remote = new LKRemote(getContext(), new LKRemote.TextResultListener() {
			@Override
			public void onResult(String result) {
				Gson gson = new Gson();
				Response.StdResponse resp = gson.fromJson(result, Response.StdResponse.class);
				if(resp.status.equals("success")){
					Log.d(LOG_TAG, "did put on user");
				}else{
					showPopup(resp.message, getContext().getString(R.string.reg_code_fail_title));
				}
			}
		});
		remote.requestServerForText("karnevalister/"+user.id+".json", user.getJson(), LKRemote.RequestType.PUT);
	}
	
	private void postNewUser(final LKUser user) {
		Log.d(LOG_TAG, "post new user");
		LKRemote remote = new LKRemote(getContext(),new LKRemote.TextResultListener() {
			@Override
			public void onResult(String result) {
				Log.d(LOG_TAG, result);
				Gson gson = new Gson();
				Response.PostKarnevalist resp = gson.fromJson(result, Response.PostKarnevalist.class);
				if(resp.status.equals("success")){
					Log.d(LOG_TAG, "id: "+user.id);
					user.id = resp.id;
					user.token = resp.token;
					user.step = 2; // TODO: what number to set on post?
					user.storeUserLocaly();
					loadFragment(new RegistrationProgressFragment(), false);
				}else{
					showPopup(resp.message, getContext().getString(R.string.reg_code_fail_title));
				}
			}
		});
		
		user.gcmRegId = getGcmRegId();
		if(user.gcmRegId == null)
			user.gcmRegId = "null";
		remote.requestServerForText("karnevalister.json", user.getJson(), LKRemote.RequestType.POST);
	}
	
	private boolean checkCode(){
		if(code.getText().toString().toUpperCase().equals(getContext().getString(R.string.registrationpassword).toUpperCase())){
			return true;
		}
		// TODO: Show popup.
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getContext().getString(R.string.reg_code_fail_title));
		builder.setMessage(getContext().getString(R.string.reg_code_fail_msg));
		builder.setPositiveButton("Ok", null);
		builder.show();
		return false;
	}
	
	/**
	 * Get data from fields and populate the user with the new data.
	 * @param user The user to populate. 
	 */
	private void populateUserWithData(LKUser user){
		if(registrationStep >= 2) {
			user.personnummer = personnumber.getText().toString();
			user.fornamn = name.getText().toString();
			user.efternamn = lastname.getText().toString();
			user.email = email.getText().toString();
			user.gatuadress = adress.getText().toString();
			user.postnr = zipcode.getText().toString();
			user.postort = city.getText().toString();
			user.telnr = mobilnbr.getText().toString();
			user.matpref = foodpref.getText().toString();
			user.engageradKar = engageradKar.getText().toString();
			user.engageradNation = engageradNation.getText().toString();
			user.engageradStudentikos = engageradStudentikos.getText().toString();
			user.engageradEtc = engageradEtc.getText().toString();
			user.ovrigt = ovrigt.getText().toString();
			user.kon = nation;
			user.nation = nation;
			user.storlek = shirtSize;
			user.terminer = Integer.parseInt(terms.getText().toString());
			user.korkort = driverLicens;
			//user.snallaIntresse = nation;
			//user.snallaSektion = nation;
		}
	}
	
	/**
	 * Validates email address
	 * @param email The address to validate
	 * @return True if it is valid. 
	 */
	private boolean equalEmail(String email1, String email2) {
		return email1.equals(email2);
	}
	private boolean validEmail(String email){
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	@Override
	public void onIntrestsCheckBoxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();
	    if(intressen == null)
	    	intressen = new ArrayList<Integer>();
		switch (view.getId()) {
		case R.id.dd_0:
			if (checked)
				intressen.add(0);
			else 
				intressen.remove(0);
			break;
		case R.id.dd_1:
			if (checked)
				intressen.add(1);
			else 
				intressen.remove(1);
			break;
		case R.id.dd_2:
			if (checked)
				intressen.add(2);
			else 
				intressen.remove(2);
			break;
		case R.id.dd_3:
			if (checked)
				intressen.add(3);
			else 
				intressen.remove(3);
			break;
		case R.id.dd_4:
			if (checked)
				intressen.add(4);
			else 
				intressen.remove(4);
			break;
		case R.id.dd_5:
			if (checked)
				intressen.add(5);
			else 
				intressen.remove(5);
			break;
		case R.id.dd_6:
			if (checked)
				intressen.add(6);
			else 
				intressen.remove(6);
			break;
		case R.id.dd_7:
			if (checked)
				intressen.add(7);
			else 
				intressen.remove(7);
			break;
		case R.id.dd_8:
			if (checked)
				intressen.add(8);
			else 
				intressen.remove(8);
			break;
		case R.id.dd_9:
			if (checked)
				intressen.add(9);
			else 
				intressen.remove(9);
			break;
		case R.id.dd_10:
			if (checked)
				intressen.add(10);
			else 
				intressen.remove(10);
			break;
		case R.id.dd_11:
			if (checked)
				intressen.add(11);
			else 
				intressen.remove(11);
			break;
		case R.id.dd_12:
			if (checked)
				intressen.add(12);
			else 
				intressen.remove(12);
			break;
		case R.id.dd_13:
			if (checked)
				intressen.add(13);
			else 
				intressen.remove(13);
			break;
		case R.id.dd_14:
			if (checked)
				intressen.add(14);
			else 
				intressen.remove(14);
			break;
		case R.id.dd_15:
			if (checked)
				intressen.add(15);
			else 
				intressen.remove(15);
			break;
			
		}
		if(intressen.size() == 0) 
			intressen = null;
		updateProgressBar();
	}
	
	@Override
	public void onSectionCheckBoxClicked(View view) {
	    boolean checked = ((CheckBox) view).isChecked();
	    if(sektioner == null)
	    	sektioner = new ArrayList<Integer>();
		switch (view.getId()) {
		case R.id.checkbox_0:
			if (checked)
				sektioner.add(0);
			else 
				sektioner.remove(0);
			break;
		case R.id.checkbox_1:
			if (checked)
				sektioner.add(1);
			else 
				sektioner.remove(1);
			break;
		case R.id.checkbox_2:
			if (checked) 
				sektioner.add(2);
			else
				sektioner.remove(2);
			break;
		case R.id.checkbox_3:
			if (checked) 
				sektioner.add(3);
			else
				sektioner.remove(3);
			break;
		case R.id.checkbox_4:
			if (checked) 
				sektioner.add(4);
			else				
				sektioner.remove(4);
			break;
		case R.id.checkbox_5:
			if (checked) 
				sektioner.add(5);
			else
				sektioner.remove(5);
			break;
		case R.id.checkbox_6:
			if (checked) 
				sektioner.add(6);
			else
				sektioner.remove(6);
			break;
		case R.id.checkbox_7:
			if (checked) 
				sektioner.add(7);
			else
				sektioner.remove(7);
			break;
		case R.id.checkbox_8:
			if (checked) 
				sektioner.add(8);
			else
				sektioner.remove(8);
			break;
		case R.id.checkbox_9:
			if (checked) 
				sektioner.add(9);
			else
				sektioner.remove(9);
			break;
		case R.id.checkbox_10:
			if (checked) 
				sektioner.add(10);
			else
				sektioner.remove(10);
			break;
		case R.id.checkbox_11:
			if (checked) 
				sektioner.add(11);
			else
				sektioner.remove(11);
			break;
		case R.id.checkbox_12:
			if (checked) 
				sektioner.add(12);
			else
				sektioner.remove(12);
			break;
		case R.id.checkbox_13:
			if (checked) 
				sektioner.add(13);
			else
				sektioner.remove(13);
			break;
		case R.id.checkbox_14:
			if (checked) 
				sektioner.add(14);
			else
				sektioner.remove(14);
			break;
		case R.id.checkbox_15:
			if (checked) 
				sektioner.add(15);
			else
				sektioner.remove(15);
			break;
		case R.id.checkbox_16:
			if (checked) 
				sektioner.add(16);
			else
				sektioner.remove(16);
			break;
		case R.id.checkbox_17:
			if (checked) 
				sektioner.add(17);
			else
				sektioner.remove(17);
			break;
		case R.id.checkbox_18:
			if (checked) 
				sektioner.add(18);
			else
				sektioner.remove(18);
			break;
		case R.id.checkbox_19:
			if (checked) 
				sektioner.add(19);
			else
				sektioner.remove(19);
			break;
		case R.id.checkbox_20:
			if (checked) 
				sektioner.add(20);
			else
				sektioner.remove(20);
			break;
		case R.id.checkbox_21:
			if (checked) 
				sektioner.add(21);
			else
				sektioner.remove(21);
			break;
		case R.id.checkbox_22:
			if (checked) 
				sektioner.add(22);
			else
				sektioner.remove(22);
			break;
		case R.id.checkbox_23:
			if (checked) 
				sektioner.add(23);
			else
				sektioner.remove(23);
			break;
		case R.id.checkbox_24:
			if (checked) 
				sektioner.add(24);
			else
				sektioner.remove(24);
			break;
		case R.id.checkbox_25:
			if (checked) 
				sektioner.add(25);
			else
				sektioner.remove(25);
			break;
		case R.id.checkbox_26:
			if (checked) 
				sektioner.add(26);
			else
				sektioner.remove(26);
			break;
		case R.id.checkbox_100:
			if (checked) 
				sektioner.add(100);
			else
				sektioner.remove(100);
			break;
		case R.id.checkbox_101:
			if (checked) 
				sektioner.add(101);
			else
				sektioner.remove(101);
			break;
		case R.id.checkbox_102:
			if (checked) 
				sektioner.add(102);
			else
				sektioner.remove(102);
			break;
		case R.id.checkbox_202:
			if (checked) 
				sektioner.add(202);
			else
				sektioner.remove(202);
			break;
		case R.id.checkbox_203:
			if (checked) 
				sektioner.add(203);
			else
				sektioner.remove(203);
			break;
		case R.id.checkbox_204:
			if (checked) 
				sektioner.add(204);
			else
				sektioner.remove(204);
			break;
		case R.id.checkbox_300:
			if (checked) 
				sektioner.add(300);
			else
				sektioner.remove(300);
			break;
		case R.id.checkbox_399:
			if (checked) 
				sektioner.add(399);
			else
				sektioner.remove(399);
			break;
		case R.id.checkbox_400:
			if (checked) 
				sektioner.add(400);
			else
				sektioner.remove(400);
			break;
		case R.id.checkbox_499:
			if (checked) 
				sektioner.add(499);
			else
				sektioner.remove(499);
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