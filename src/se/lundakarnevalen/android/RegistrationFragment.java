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
	private int registrationStep = 0; // 0 = personuppgifter, 1 = kod, 2 = karnevalsuppgifter, 3 = redigera (visa allt förutom koden). 
	private int progresslevel;
	private LKEditText code, name, lastname, email, validemail, mobilnbr, adress, zipcode, city, personnumber, foodpref, engageradKar, engageradNation, engageradStudentikos, engageradEtc, ovrigt, terms;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton, appendButton;
	private LKSpinner nationsSpinner, shirtSpinner, driverLicensSpinner, sexSpinner;
	private ArrayList<String> sektioner;
	
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
	
	private boolean sectionChosen(){
		return sektioner!=null;
	}
	
	private void updateProgressBar() {
		progresslevel = 0;
		
		if(isEditTextFilled(name))
			progresslevel += 5;
		if(isEditTextFilled(lastname))
			progresslevel += 5;
		if(isEditTextOfThisLenght(personnumber,10))
			progresslevel += 5;
		if(isEditTextOfThisLenght(mobilnbr,10))
			progresslevel += 5;
		if(validEmail(email.getText().toString(), validemail.getText().toString()))
			progresslevel += 5;
		if(isEditTextFilled(adress))
			progresslevel += 5;
		if(isEditTextOfThisLenght(zipcode,5))
			progresslevel += 5;
		if(isEditTextFilled(city))
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
		Log.d(LOG_TAG, "validate form");
		updateProgressBar();
		return progresslevel == 40;
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
				// Post data
				break;
			case 3:
				user.getUserLocaly();
				populateUserWithData(user);
				putNewUser(user);
				break;
			}
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
			user.kon = nation;
			user.nation = nation;
			user.storlek = shirtSize;
			user.terminer = Integer.parseInt(terms.getText().toString());
			user.korkort = driverLicens;
			user.snallaIntresse = nation;
			user.snallaSektion = nation;
		}
	}
	
	/**
	 * Validates email address
	 * @param email The address to validate
	 * @return True if it is valid. 
	 */
	private boolean validEmail(String email, String confirmemail){
		boolean equaladress = email.equals(confirmemail);
		String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches() && equaladress;
	}
	
	@Override
	public void onCheckBoxClicked(View view) {
	    boolean checked = ((CheckBox) view).isChecked();
	    if(sektioner == null)
	    	sektioner = new ArrayList<String>();
		/*switch (view.getId()) {
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
				sektioner.add("Bl�dderiet");
			else
				sektioner.remove("Bl�dderiet");
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
				sektioner.add("Festm�steriet");
			else
				sektioner.remove("Festm�steriet");
			break;
		case R.id.checkbox_filmen:
			if (checked) 
				sektioner.add("Filmen");
			else
				sektioner.remove("Filmen");
			break;
		case R.id.checkbox_kabare:
			if (checked) 
				sektioner.add("Kabar�");
			else
				sektioner.remove("Kabar�");
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
				sektioner.add("Omr�det");
			else
				sektioner.remove("Omr�det");
			break;
		case R.id.checkbox_musiken:
			if (checked) 
				sektioner.add("Musiken");
			else
				sektioner.remove("Musiken");
			break;
		case R.id.checkbox_nojessektionen:
			if (checked) 
				sektioner.add("N�jessektionen");
			else
				sektioner.remove("N�jessektionen");
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
				sektioner.add("S�kerhet");
			else
				sektioner.remove("S�kerhet");
			break;
		case R.id.checkbox_tombola:
			if (checked) 
				sektioner.add("Tombola");
			else
				sektioner.remove("Tombola");
			break;
		case R.id.checkbox_taget:
			if (checked) 
				sektioner.add("T�get");
			else
				sektioner.remove("T�get");
			break;
		case R.id.checkbox_taltnojen:
			if (checked) 
				sektioner.add("T�ltn�jen");
			else
				sektioner.remove("T�ltn�jen");
			break;
		case R.id.checkbox_vieriet:
			if (checked) 
				sektioner.add("Vieriet");
			else
				sektioner.remove("Vieriet");
			break;
		} */
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