package fragments;

import json.CredentialEmailWrite;
import json.LoginCredentialsWrite;
import json.ResetResponse;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import util.HelperEmail;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;


public class FrResetPassword extends Fragment{
	
	private static final String log = FrResetPassword.class.getSimpleName();
	
	private EditText mEmailView;
	private LKRemote remote;
	private Gson gson;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fr_layout_reset_password, null); 
		
		remote = new LKRemote(rootView.getContext(), new ResetRemoteListener());
		
		mEmailView = (EditText) rootView.findViewById(R.id.email_field);
		
		Button reset = (Button) rootView.findViewById(R.id.reset_password);
		reset.setOnClickListener(new ButtonReset());

		gson = new Gson();
		
		return rootView;
		
	}
	
	private class ResetRemoteListener implements TextResultListener {

		@Override
		public void onResult(String result) {
			if(result == null) {
				Log.d(log, "Result was null");
				return;
			}
			
			ResetResponse response = gson.fromJson(result, ResetResponse.class);
			
			Log.d(LOG_TAG, response.toString());
			
			if(!response.success) {
				Toast.makeText(getActivity(), response.errors.toString(), Toast.LENGTH_LONG).show();
				return;
			}
			
			Toast.makeText(getActivity(), "Successfully reset password, check your mail", Toast.LENGTH_LONG).show();
			
			FragmentManager manager = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			
			ft.replace(R.id.content_frame, new FrSignIn());
			ft.commit();
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
			CredentialEmailWrite credentials = new CredentialEmailWrite(email);
			
			remote.requestServerForText("api/users/password", gson.toJson(credentials), LKRemote.RequestType.POST, false);	
		}
	}
}
