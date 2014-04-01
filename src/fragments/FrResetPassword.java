package fragments;

import json.CredentialEmailWrite;
import json.ResetResponse;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import util.HelperEmail;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnMenuVisibilityListener;
import android.support.v7.app.ActionBarActivity;
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
	private ActionBar bar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_reset_password,
				null);

		remote = new LKRemote(rootView.getContext(), new ResetRemoteListener());

		mEmailView = (EditText) rootView.findViewById(R.id.email_field);

		Button reset = (Button) rootView.findViewById(R.id.reset_password);
		reset.setOnClickListener(new ButtonReset());

		initializeActionBar();

		gson = new Gson();
		
		setHasOptionsMenu(true);

		return rootView;

	}

	private void initializeActionBar() {
		ActionBarActivity AcBar = (ActionBarActivity) getActivity();
		bar = AcBar.getSupportActionBar();

		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getFragmentManager().popBackStack();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class ResetRemoteListener implements TextResultListener {

		@Override
		public void onResult(String result) {
			if (result == null) {
				Log.d(LOG_TAG, "Result was null");
				return;
			}

			ResetResponse response = gson.fromJson(result, ResetResponse.class);

			Log.d(LOG_TAG, response.toString());

			if (!response.success) {
				Toast.makeText(getActivity(), response.errors.toString(),
						Toast.LENGTH_LONG).show();
				return;
			}

			Toast.makeText(getActivity(),
					"Successfully reset password, check your mail",
					Toast.LENGTH_LONG).show();

			//Return to Sign in
			getFragmentManager().popBackStack();
		}
	}

	private class ButtonReset implements OnClickListener {
		@Override
		public void onClick(View v) {
			mEmailView.setError(null);
			Log.d("Reset", "Pressed Reset");

			String email = mEmailView.getText().toString();

			Log.d("Email", email);

			if (!HelperEmail.validEmail(email)) {
				mEmailView.setError("Incorrectly formated email");
				mEmailView.requestFocus();
				return;
			}

			Gson gson = new Gson();
			CredentialEmailWrite credentials = new CredentialEmailWrite(email);

			remote.requestServerForText("api/users/password",
					gson.toJson(credentials), LKRemote.RequestType.POST, false);
		}
	}
}
