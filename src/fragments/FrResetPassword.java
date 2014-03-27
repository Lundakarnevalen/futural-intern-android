package fragments;

import com.google.gson.Gson;

import json.LoginCredentialsWrite;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import util.HelperEmail;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class FrResetPassword extends Fragment{
	
	private static final String log = FrResetPassword.class.getSimpleName();
	
	private EditText mEmailView;
	private LKRemote remote;
	private Gson gson;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fr_layout_reset_password, null); 
		
		remote = new LKRemote(rootView.getContext(), new TextListener());
		
		mEmailView = (EditText) rootView.findViewById(R.id.email_field);
		
		Button reset = (Button) rootView.findViewById(R.id.reset_password);
		reset.setOnClickListener(new ButtonReset());
		
		gson = new Gson();
		
		Log.d("ASDASD", "asdasd");
		
		return rootView;
		
	}
	
	private class TextListener implements TextResultListener {

		@Override
		public void onResult(String result) {

			if(result == null) {
				Log.d(log, "Result was null");
				return;
			}
			
//			gson.fromJson(result, classOfT)
		
			
		}
	}
	
	private class ButtonReset implements OnClickListener {
		@Override
		public void onClick(View v) {
			mEmailView.setError(null);
			Log.d("Reset", "Pressed Reset");
			
			String email = mEmailView.getText().toString();
			
			Log.d("Email", email);
			
			if(!HelperEmail.validEmail(email)) {
				mEmailView.setError("Incorrectly formated email");
				mEmailView.requestFocus();
				return;
			}
			 
			Gson gson = new Gson();
			LoginCredentialsWrite credentials = new LoginCredentialsWrite(email, "");
			
			remote.requestServerForText("hostname/api/users/password", gson.toJson(credentials) , LKRemote.RequestType.POST, false);	
		}
	}
}
