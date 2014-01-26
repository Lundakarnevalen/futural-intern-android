package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKEditText;
import se.lundakarnevalen.widget.LKProgressBar;
import se.lundakarnevalen.widget.LKSpinner;
import se.lundakarnevalen.widget.LKSpinnerArrayAdapter;
import se.lundakarnevalen.widget.LKSpinnerArrayAdapter.LKSpinnerArrayItem;
import se.lundakarnevalen.widget.LKTextView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class RegistrationFragment extends LKFragment{
	private int registrationStep = 0; // 0 = personuppgifter, 1 = kod, 2 = karnevalsuppgifter, 3 = redigera (visa allt förutom koden). 
	private int progresslevel;
	private LKEditText name, email, mobilnbr;
	private LKProgressBar progressbar;
	private LKTextView progressvalue;
	private LKButton confirmButton, appendButton;
	private LKSpinner nationsSpinner, shirtSpinner, driverLicensSpinner;
	private ArrayList<String> sektioner;
	
	// Wrappers
	private LinearLayout wrapperPers, wrapperCode, wrapperLK;
	
	//spinner values
	int nation = 0, shirtSize = 0, driverLicens = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// Get registrationstep
		SharedPreferences sp = getContext().getSharedPreferences(LKFragment.SP_NAME, Context.MODE_PRIVATE);
		registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		
		View root = (View) inflater.inflate(R.layout.registration_layout, null);
		name = (LKEditText) root.findViewById(R.id.name);
		email = (LKEditText) root.findViewById(R.id.email);
		mobilnbr = (LKEditText) root.findViewById(R.id.phone_nr);
		progressbar = (LKProgressBar) root.findViewById(R.id.lKProgressBar1);
		progressvalue = (LKTextView) root.findViewById(R.id.progress_value);
		confirmButton = (LKButton) root.findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirm);
		name.addTextChangedListener(watcher);
		email.addTextChangedListener(watcher);
		mobilnbr.addTextChangedListener(watcher);
		nationsSpinner = (LKSpinner) root.findViewById(R.id.nations);
		nationsSpinner.setOnItemSelectedListener(nationsSpinnerListeners);
		shirtSpinner = (LKSpinner) root.findViewById(R.id.shirt_size);
		shirtSpinner.setOnItemSelectedListener(shirtSpinnerListeners);
		driverLicensSpinner = (LKSpinner) root.findViewById(R.id.driver_licens);
		driverLicensSpinner.setOnItemSelectedListener(driverLicensSpinnerListeners);
		appendButton = (LKButton) root.findViewById(R.id.append_button);
		wrapperPers = (LinearLayout) root.findViewById(R.id.wrapper_pers);
		wrapperCode = (LinearLayout) root.findViewById(R.id.wrapper_code);
		wrapperLK = (LinearLayout) root.findViewById(R.id.wrapper_lk);
		// Set correct views.
		updateLayout();
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
		
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
			wrapperPers.setVisibility(View.GONE);
			confirmButton.setVisibility(View.VISIBLE);
			wrapperCode.setVisibility(View.GONE);
			wrapperLK.setVisibility(View.VISIBLE);
			appendButton.setVisibility(View.VISIBLE);
			break;
		default:
			// Redigera alla uppgifter
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
	
	private boolean validMobileNumber(){
		return !mobilnbr.getText().toString().equals("");
	}
	
	private boolean validName(){
		return !name.getText().toString().equals("");
	}
	
	private boolean sectionChosen(){
		return sektioner!=null;
	}
	
	private void updateProgressBar() {
		progresslevel = 0;
		if(validMobileNumber())
			progresslevel += 10;
		if(validName())
			progresslevel += 10;
		if(sectionChosen())
			progresslevel += 20;
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
		return true;
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
			if(validateForm()){ // TODO: should call the validation method
				LKUser user = new LKUser(getContext());
				populateUserWithData(user);
				user.storeUserLocaly();
				
				registrationStep++;
				storeRegistrationStep();
				updateLayout();
			}
		}
	};
	
	/**
	 * Get data from fields and populate the user with the new data.
	 * @param user The user to populate. 
	 */
	private void populateUserWithData(LKUser user){
		
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