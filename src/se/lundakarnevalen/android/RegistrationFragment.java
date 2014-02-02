package se.lundakarnevalen.android;
import se.lundakarnevalen.widget.LKRadioGroup;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;

public class RegistrationFragment extends LKFragment{
	private int registrationStep = 0; // 0 = personuppgifter, 1 = kod, 2 = karnevalsuppgifter, 3 = redigera (visa allt förutom koden). 
	private int progresslevel, starintresse, starsektion;
	private LKEditText code, name, lastname, email, validemail, mobilnbr, adress, zipcode, city, personnumber, foodpref, engageradKar, engageradNation, engageradStudentikos, engageradEtc, ovrigt, terms;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton, appendButton;
	private LKSpinner nationsSpinner, shirtSpinner, driverLicensSpinner, sexSpinner;
	private boolean[] sektionerchecked = new boolean[500];
	private boolean[] intressenchecked = new boolean[16];
	private LKRadioGroup rgsec;
	private LKRadioGroup rgint;
	private CheckBox pulcheckbox, karnevalist2010cb, work_fulltimecb, work_styrelsecb, work_formancb, work_aktivcb;

	
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
		pulcheckbox = (CheckBox) root.findViewById(R.id.checkbox_PUL);
		work_fulltimecb = (CheckBox) root.findViewById(R.id.checkbox_work_fulltime);
		work_styrelsecb = (CheckBox) root.findViewById(R.id.checkbox_work_styrelse);
		work_formancb = (CheckBox) root.findViewById(R.id.checkbox_work_forman);
		work_aktivcb = (CheckBox) root.findViewById(R.id.checkbox_work_aktiv);
		karnevalist2010cb = (CheckBox) root.findViewById(R.id.checkbox_karnevalist_2010);
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
		rgsec = new LKRadioGroup(getContext(), root, R.id.radio_section_0, R.id.radio_section_1, 
				R.id.radio_section_2, R.id.radio_section_3, R.id.radio_section_4, R.id.radio_section_5, 
				R.id.radio_section_6, R.id.radio_section_7, R.id.radio_section_8, R.id.radio_section_9, 
				R.id.radio_section_10, R.id.radio_section_11, R.id.radio_section_12, R.id.radio_section_13, 
				R.id.radio_section_14, R.id.radio_section_15, R.id.radio_section_16, R.id.radio_section_17, 
				R.id.radio_section_18, R.id.radio_section_19, R.id.radio_section_20, R.id.radio_section_21, 
				R.id.radio_section_22, R.id.radio_section_23, R.id.radio_section_24, R.id.radio_section_25, 
				R.id.radio_section_26, R.id.radio_section_100, R.id.radio_section_101, R.id.radio_section_102, 
				R.id.radio_section_202, R.id.radio_section_203, R.id.radio_section_204, R.id.radio_section_300, 
				R.id.radio_section_399, R.id.radio_section_400, R.id.radio_section_499);
		rgint = new LKRadioGroup(getContext(), root, R.id.radio_dd_0, R.id.radio_dd_1, 
				R.id.radio_dd_2, R.id.radio_dd_3, R.id.radio_dd_4, R.id.radio_dd_5, 
				R.id.radio_dd_6, R.id.radio_dd_7, R.id.radio_dd_8, R.id.radio_dd_9, 
				R.id.radio_dd_10, R.id.radio_dd_11, R.id.radio_dd_12, R.id.radio_dd_13, 
				R.id.radio_dd_14, R.id.radio_dd_15);
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
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Vet ej", 0));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Blekingska", 1));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Göteborgs", 2));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Hallands", 3));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Helsingkrona", 4));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Kalmar", 5));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Krischan", 6));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Lunds", 7));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Malmö", 8));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Sydskånska", 9));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Västgöta", 10));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Wermlands", 11));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Östgöta", 12));
		nationsList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Smålands", 13));
		LKSpinnerArrayAdapter nationsAdapter = new LKSpinnerArrayAdapter(getContext(), nationsList);
		nationsSpinner.setAdapter(nationsAdapter);
		
		List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> shirtSizeList = new ArrayList<LKSpinnerArrayAdapter.LKSpinnerArrayItem>();
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
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Annat", 0));
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Man", 1));
		sexList.add(new LKSpinnerArrayAdapter.LKSpinnerArrayItem("Kvinna", 2));
		LKSpinnerArrayAdapter sexAdapter = new LKSpinnerArrayAdapter(getContext(), sexList);
		sexSpinner.setAdapter(sexAdapter);

	}
	
	
	public void insertAllDataFromSP(){
		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		personnumber.setText(user.personnummer);
		name.setText(user.fornamn);
		lastname.setText(user.efternamn);
		email.setText(user.email);
		validemail.setText(user.email);
		adress.setText(user.gatuadress);
		zipcode.setText(user.postnr);
		city.setText(user.postort);
		mobilnbr.setText(user.telnr);
		foodpref.setText(user.matpref);
		engageradKar.setText(user.engageradKar);
		engageradNation.setText(user.engageradNation);
		engageradStudentikos.setText(user.engageradStudentikos);
		engageradEtc.setText(user.engageradEtc);
		ovrigt.setText(String.valueOf(user.ovrigt));
		//terms.setText(user.terminer);
		work_fulltimecb.setChecked(user.jobbatHeltid);
		work_styrelsecb.setChecked(user.jobbatStyrelse);
		work_formancb.setChecked(user.jobbatForman);
		work_aktivcb.setChecked(user.jobbatAktiv);
		karnevalist2010cb.setChecked(user.karnevalist2010);
		/*	TODO:fixa s� att dropdownlistorna f�r r�tt v�rden.. - reverse this:
			"user.kon = sex;
			user.nation = nation;
			user.storlek = shirtSize;
			user.korkort = driverLicens;"
		*/
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
			wrapperPers.setVisibility(View.GONE); // N�r man klickar p� ny knapp !! FIXA
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
			insertAllDataFromSP();
			
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
		int amount = 0;
		for(boolean b: sektionerchecked){
			if(b)
				amount++;
		}
		return amount>4;
	}
	private boolean intressenChosen(){
		int amount = 0;
		for(boolean b: intressenchecked){
			if(b)
				amount++;
		}
		return amount>2;	
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
				Wrongs.add("F�rnamn");
			if(!isEditTextFilled(lastname))
				Wrongs.add("Efternamn");
			if(!isEditTextOfThisLenght(personnumber,10))
				Wrongs.add("Personnummer");
			if(!isEditTextLongerThanThisLength(mobilnbr,4))
				Wrongs.add("Mobilnummer");
			if(!validEmail(email.getText().toString()))
				Wrongs.add("Mailadress");
			if(!equalEmail(email.getText().toString(), validemail.getText().toString()))
				Wrongs.add("Ej samma mailadress");
			if(!isEditTextFilled(adress))
				Wrongs.add("Adress");
			if(!isEditTextOfThisLenght(zipcode,5))
				Wrongs.add("Postnummer");
			if(!isEditTextFilled(city))
				Wrongs.add("Stad");
			if(!pulcheckbox.isChecked())
				Wrongs.add("Godk�nnande av hanterandet av anv�ndardata");
			break;
		case 2:
			if(!sectionChosen())
				Wrongs.add("Du m�ste v�lja minst 5 sektioner");
			if(!intressenChosen())
				Wrongs.add("Du m�ste v�lja minst 3 intressen");
			break;
		case 3:
			if(!isEditTextFilled(name))
				Wrongs.add("F�rnamn");
			if(!isEditTextFilled(lastname))
				Wrongs.add("Efternamn");
			if(!isEditTextOfThisLenght(personnumber,10))
				Wrongs.add("Personnummer");
			if(!isEditTextLongerThanThisLength(mobilnbr,4))
				Wrongs.add("Mobilnummer");
			if(!validEmail(email.getText().toString()))
				Wrongs.add("Mailadress");
			if(!equalEmail(email.getText().toString(), validemail.getText().toString()))
				Wrongs.add("Ej samma mailadress");
			if(!isEditTextFilled(adress))
				Wrongs.add("Adress");
			if(!isEditTextOfThisLenght(zipcode,5))
				Wrongs.add("Postnummer");
			if(!isEditTextFilled(city))
				Wrongs.add("Stad");
			if(!pulcheckbox.isChecked())
				Wrongs.add("Godk�nnande av hanterandet av anv�ndardata");
			if(!sectionChosen())
				Wrongs.add("Du m�ste v�lja minst 5 sektioner");
			if(!intressenChosen())
				Wrongs.add("Du m�ste v�lja minst 3 intressen");
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
				postNewUser(user);
				break;
			case 3:
				user.getUserLocaly();
				populateUserWithData(user);
				putNewUser(user);
				break;
			}
		} else {
			ArrayList<String> wrongs = validateFormArray();
			String message = "";
			for(String s:wrongs) {
				message += s + "\n";
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(R.string.fail_creedentials);
			builder.setMessage(message);
			builder.setPositiveButton("Ok", null);
			builder.show();
		}
	}
	
	private void putNewUser(final LKUser user){
		Log.d(LOG_TAG, "put new user");
		LKRemote remote = new LKRemote(getContext(), new LKRemote.TextResultListener() {
			@Override
			public void onResult(String result) {
				if(result == null){
					Log.e(LOG_TAG, "error - null response");
					showPopup(getContext().getString(R.string.fail), getContext().getString(R.string.error));
					return;
				}
				Gson gson = new Gson();
				Response.StdResponse resp = gson.fromJson(result, Response.StdResponse.class);
				if(resp.status.equals("success")){
					Log.d(LOG_TAG, "did put on user");
					user.storeUserLocaly();
					loadFragment(new RegistrationProgressFragment(), false);
				}else{
					showPopup(resp.message, getContext().getString(R.string.reg_code_fail_title));
				}
			}
		});
		remote.showProgressDialog(true);
		remote.requestServerForText("karnevalister/"+user.id+".json", user.getJson(), LKRemote.RequestType.PUT);
	}
	
	private void postNewUser(final LKUser user) {
		Log.d(LOG_TAG, "post new user");
		LKRemote remote = new LKRemote(getContext(),new LKRemote.TextResultListener() {
			@Override
			public void onResult(String result) {
				if(result == null){
					Log.e(LOG_TAG, "error - null response");
					showPopup(getContext().getString(R.string.fail), getContext().getString(R.string.error));
					return;
				}
				Log.d(LOG_TAG, result);
				Gson gson = new Gson();
				Response.PostKarnevalist resp = gson.fromJson(result, Response.PostKarnevalist.class);
				if(resp.status.equals("success")){
					Log.d(LOG_TAG, "id: "+user.id);
					user.id = resp.id;
					user.token = resp.token;
					user.step = 2; // TODO: what number to set on post
					user.storeUserLocaly();
					registrationStep++;
					storeRegistrationStep();
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
		remote.showProgressDialog(true);
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
			user.kon = sex;
			user.nation = nation;
			user.storlek = shirtSize;
			if(terms.getText().toString()!=null)
				user.terminer = Integer.parseInt(terms.getText().toString());
			user.korkort = driverLicens;
			user.snallaIntresse = starIntrest();
			user.snallaSektion = starSection();
			user.intresse = intrestsAsIntArr();
			user.sektioner = sectionsAsIntArr();
			user.karnevalist2010 = karnevalist2010cb.isChecked();
			user.jobbatHeltid = work_fulltimecb.isChecked();
			user.jobbatStyrelse = work_styrelsecb.isChecked();
			user.jobbatForman = work_formancb.isChecked();
			user.jobbatAktiv = work_aktivcb.isChecked();
			user.karnevalist2010 = karnevalist2010cb.isChecked();
		}
	}
	
	/**
	 * Validates email address
	 * @param email The address to validate
	 * @return True if it is valid. 
	 */
	private boolean validEmail(String email){
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	private boolean equalEmail(String email1, String email2) {
		return email1.equals(email2);
	}
	
	
	public int[] intrestsAsIntArr() {
		int size = 0;
		for(boolean b: intressenchecked)
			if(b)
				size++;
		int[] ret = new int[size];
		int i = 0;
		for(int j = 0; j<intressenchecked.length;j++)
			if(intressenchecked[j])
				ret[i++] = j;
		return ret;
	}
	public int[] sectionsAsIntArr() {
		int size = 0;
		for(boolean b: sektionerchecked)
			if(b)
				size++;
		int[] ret = new int[size];
		int i = 0;
		for(int j = 0; j<sektionerchecked.length;j++)
			if(sektionerchecked[j])
				ret[i++] = j;
		return ret;
	}
	public int starIntrest() {
		int id = rgint.getActiveRB();
		switch (id) {
			case R.id.radio_dd_0:
				return 0;		
			case R.id.radio_dd_1:
				return 1;
			case R.id.radio_dd_2:
				return 2;
			case R.id.radio_dd_3:
				return 3;		
			case R.id.radio_dd_4:
				return 4;
			case R.id.radio_dd_5:
				return 5;
			case R.id.radio_dd_6:
				return 6;
			case R.id.radio_dd_7:
				return 7;
			case R.id.radio_dd_8:
				return 8;
			case R.id.radio_dd_9:
				return 9;
			case R.id.radio_dd_10:
				return 10;
			case R.id.radio_dd_11:
				return 11;
			case R.id.radio_dd_12:
				return 12;
			case R.id.radio_dd_13:
				return 13;
			case R.id.radio_dd_14:
				return 14;
			case R.id.radio_dd_15:
				return 15;
			default:
				return 0;
		}
	}
	
	@Override
	public void onIntrestsCheckBoxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
			case R.id.dd_0:
				intressenchecked[0] = checked;
				break;
			case R.id.dd_1:
				intressenchecked[1] = checked;
				break;
			case R.id.dd_2:
				intressenchecked[2] = checked;
				break;
			case R.id.dd_3:
				intressenchecked[3] = checked;
				break;
			case R.id.dd_4:
				intressenchecked[4] = checked;
				break;
			case R.id.dd_5:
				intressenchecked[5] = checked;
				break;
			case R.id.dd_6:
				intressenchecked[6] = checked;
				break;
			case R.id.dd_7:
				intressenchecked[7] = checked;
				break;
			case R.id.dd_8:
				intressenchecked[8] = checked;
				break;
			case R.id.dd_9:
				intressenchecked[9] = checked;
				break;
			case R.id.dd_10:
				intressenchecked[10] = checked;
				break;
			case R.id.dd_11:
				intressenchecked[11] = checked;
				break;
			case R.id.dd_12:
				intressenchecked[12] = checked;
				break;
			case R.id.dd_13:
				intressenchecked[13] = checked;
				break;
			case R.id.dd_14:
				intressenchecked[14] = checked;
				break;
			case R.id.dd_15:
				if(checked){
					AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
	    			builder.setTitle("Superviktigt!");
	    			builder.setMessage("F�r dessa funktioner s� m�ste du komma p� audition! Auditionstid bokas hos N�jessektionen.");
	    			builder.setPositiveButton("Ok", null);
	    			builder.show();				
	    		}
				intressenchecked[15] = checked;
				break;	
		}
		updateProgressBar();
	}
	public int starSection() {
		int id = rgsec.getActiveRB();
		switch (id) {
			case R.id.radio_section_0:
				return 0;
			case R.id.radio_section_1:
				return 1;
			case R.id.radio_section_2:
				return 2;
			case R.id.radio_section_3:
				return 3;
			case R.id.radio_section_4:
				return 4;
			case R.id.radio_section_5:
				return 5;
			case R.id.radio_section_6:
				return 6;
			case R.id.radio_section_7:
				return 7;
			case R.id.radio_section_8:
				return 8;
			case R.id.radio_section_9:
				return 9;
			case R.id.radio_section_10:
				return 10;
			case R.id.radio_section_11:
				return 11;
			case R.id.radio_section_12:
				return 12;
			case R.id.radio_section_13:
				return 13;
			case R.id.radio_section_14:
				return 14;
			case R.id.radio_section_15:
				return 15;
			case R.id.radio_section_16:
				return 16;
			case R.id.radio_section_17:
				return 17;
			case R.id.radio_section_18:
				return 18;
			case R.id.radio_section_19:
				return 19;
			case R.id.radio_section_20:
				return 20;
			case R.id.radio_section_21:
				return 21;
			case R.id.radio_section_22:
				return 22;
			case R.id.radio_section_23:
				return 23;
			case R.id.radio_section_24:
				return 24;
			case R.id.radio_section_25:
				return 25;
			case R.id.radio_section_26:
				return 26;
			case R.id.radio_section_100:
				return 100;
			case R.id.radio_section_101:
				return 101;
			case R.id.radio_section_102:
				return 102;
			case R.id.radio_section_202:
				return 202;
			case R.id.radio_section_203:
				return 203;
			case R.id.radio_section_204:
				return 204;
			case R.id.radio_section_300:
				return 300;
			case R.id.radio_section_399:
				return 399;
			case R.id.radio_section_400:
				return 400;
			case R.id.radio_section_499:
				return 499;
			default:
				return 0;
		}
	}
	
	@Override
	public void onSectionCheckBoxClicked(View view) {
	    boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
			case R.id.checkbox_0:
				sektionerchecked[0] = checked;
				break;
			case R.id.checkbox_1:
				sektionerchecked[1] = checked;
				break;
			case R.id.checkbox_2:
				sektionerchecked[2] = checked;
				break;
			case R.id.checkbox_3:
				sektionerchecked[3] = checked;
				break;
			case R.id.checkbox_4:
				sektionerchecked[4] = checked;
				break;
			case R.id.checkbox_5:
				sektionerchecked[5] = checked;
				break;
			case R.id.checkbox_6:
				sektionerchecked[6] = checked;
				break;
			case R.id.checkbox_7:
				sektionerchecked[7] = checked;
				break;
			case R.id.checkbox_8:
				sektionerchecked[8] = checked;
				break;
			case R.id.checkbox_9:
				sektionerchecked[9] = checked;
				break;
			case R.id.checkbox_10:
				sektionerchecked[10] = checked;
				break;
			case R.id.checkbox_11:
				sektionerchecked[11] = checked;
				break;
			case R.id.checkbox_12:
				sektionerchecked[12] = checked;
				break;
			case R.id.checkbox_13:
				sektionerchecked[13] = checked;
				break;
			case R.id.checkbox_14:
				sektionerchecked[14] = checked;
				break;
			case R.id.checkbox_15:
				sektionerchecked[15] = checked;
				break;
			case R.id.checkbox_16:
				sektionerchecked[16] = checked;
				break;
			case R.id.checkbox_17:
				sektionerchecked[17] = checked;
				break;
			case R.id.checkbox_18:
				sektionerchecked[18] = checked;
				break;
			case R.id.checkbox_19:
				sektionerchecked[19] = checked;
				break;
			case R.id.checkbox_20:
				sektionerchecked[20] = checked;
				break;
			case R.id.checkbox_21:
				sektionerchecked[21] = checked;
				break;
			case R.id.checkbox_22:
				sektionerchecked[22] = checked;
				break;
			case R.id.checkbox_23:
				sektionerchecked[23] = checked;
				break;
			case R.id.checkbox_24:
				sektionerchecked[24] = checked;
				break;
			case R.id.checkbox_25:
				sektionerchecked[25] = checked;
				break;
			case R.id.checkbox_26:
				sektionerchecked[26] = checked;
				break;
			case R.id.checkbox_100:
				sektionerchecked[100] = checked;
				break;
			case R.id.checkbox_101:
				sektionerchecked[101] = checked;
				break;
			case R.id.checkbox_102:
				sektionerchecked[102] = checked;
				break;
			case R.id.checkbox_202:
				sektionerchecked[202] = checked;
				break;
			case R.id.checkbox_203:
				sektionerchecked[203] = checked;
				break;
			case R.id.checkbox_204:
				sektionerchecked[204] = checked;
				break;
			case R.id.checkbox_300:
				sektionerchecked[300] = checked;
				break;
			case R.id.checkbox_399:
				sektionerchecked[399] = checked;
				break;
			case R.id.checkbox_400:
				sektionerchecked[400] = checked;
				break;
			case R.id.checkbox_499:
				sektionerchecked[499] = checked;
				break;
		}
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