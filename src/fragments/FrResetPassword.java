package fragments;

import json.CredentialEmailWrite;
import json.ResetResponse;
import se.lundakarnevalen.android.AcLogin;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import util.HelperEmail;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class FrResetPassword extends Fragment {

	private static final String LOG_TAG = FrResetPassword.class.getSimpleName();
 
	private EditText mEmailView;
	private LKRemote remote;
	private Gson gson;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_reset_password, null);

		Context context = getActivity();
		AcLogin activity = (AcLogin) context;
		
		activity.setActionBarTitle(getString(R.string.action_bar_reset_password));
		
		remote = new LKRemote(rootView.getContext(), new ResetRemoteListener());

		mEmailView = (EditText) rootView.findViewById(R.id.email_field);

		Button buttonReset = (Button) rootView.findViewById(R.id.reset_password);
		buttonReset.setOnClickListener(new ButtonReset());

		Button buttonReturn = (Button) rootView.findViewById(R.id.back);
		buttonReturn.setOnClickListener(new ReturnButtonListener());
		
		gson = new Gson();
		
		setHasOptionsMenu(true);

		return rootView;

	}
	
	private class ReturnButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {	
			//Return to Sign in
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(LOG_TAG, "Entered onOptionsItemSelected");
		switch (item.getItemId()) {
		case android.R.id.home:
			
//			Return to FrSignIn
			getFragmentManager().popBackStack();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class ResetRemoteListener implements TextResultListener {

		@Override
		public void onResult(String result) {
			Log.d(LOG_TAG, "Got result from server");
			
			if (result == null) {
				Toast.makeText(getActivity(), "Could not find that email", Toast.LENGTH_LONG).show();
				return;
			}

			ResetResponse response = gson.fromJson(result, ResetResponse.class);

			Log.d(LOG_TAG, response.toString());

			if (!response.success) {
				Toast.makeText(getActivity(), response.errors.toString(), Toast.LENGTH_LONG).show();
				return;
			}

			Toast.makeText(getActivity(), R.string.reset_password_successful, Toast.LENGTH_LONG).show();

			//Return to Sign in
			getFragmentManager().popBackStack();
		}
	}

	private class ButtonReset implements OnClickListener {
		@Override
		public void onClick(View v) {
			mEmailView.setError(null);
			Log.d(LOG_TAG, "Pressed Reset");

			String email = mEmailView.getText().toString();

			Log.d("Email", email);

			if (!HelperEmail.validEmail(email)) {
				mEmailView.setError(getString(R.string.error_invalid_email));
				mEmailView.requestFocus();
				return;
			}

			Gson gson = new Gson();
			CredentialEmailWrite credentials = new CredentialEmailWrite(email);

			remote.requestServerForText("api/users/password", gson.toJson(credentials), LKRemote.RequestType.POST, false);
		}
	}
}
