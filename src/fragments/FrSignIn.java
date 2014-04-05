package fragments;

import json.LoginCredentialsWrite;
import se.lundakarnevalen.android.ContentActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import util.HelperEmail;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class FrSignIn extends Fragment {

	private Context context;

	private static final String log = FrSignIn.class.getSimpleName();
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

//	/**
//	 * Keep track of the login task to ensure we can cancel it if requested.
//	 */
//	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private LKRemote remote;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_layout_sign_in, null);

		context = getActivity();
		
		// Set up the login form.
		mEmail = getActivity().getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) rootView.findViewById(R.id.email_field);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) rootView.findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = rootView.findViewById(R.id.login_form);
		mLoginStatusView = rootView.findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) rootView.findViewById(R.id.login_status_message);

		rootView.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		remote = new LKRemote(context, new ButtonLoginResponse());
		
		Button buttonReset = (Button) rootView.findViewById(R.id.password_reset);
		buttonReset.setOnClickListener(new ButtonResetPassword());
		
		hideVirtualKeyboard();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		return rootView;
	}


	private void hideVirtualKeyboard() {
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
	}
	
	private class ButtonResetPassword implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentManager manager = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			
			ft.replace(R.id.content_frame, new FrResetPassword());
			ft.addToBackStack(null);
			ft.commit();
		}
	}
	
	private class ButtonLoginResponse implements LKRemote.TextResultListener {

		@Override
		public void onResult(String result) {
			Log.d(log, "Yay, some result!");
			
			showProgress(false);
			
			if(result == null) {
				Toast.makeText(getActivity(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
				mPasswordView.setText(null);
				return;
			}
			
			if(LKUser.localUserStored(getActivity())) {
				Log.d(log, "user stored");
			}
			
			LKUser user = new LKUser(context);
			
			user.parseJsonLogin(result);
			
			user.storeUserLocaly();

			Intent intent = new Intent(context, ContentActivity.class);		
		
			context.startActivity(intent);
			getActivity().finish();
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!HelperEmail.validEmail(mEmail)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			
			if(!LKRemote.hasInternetConnection(getActivity())) {
				Toast.makeText(getActivity(), R.string.no_internet_msg, Toast.LENGTH_LONG).show();
				return;
			}
			
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			
			hideVirtualKeyboard();
			
//			Show the loading screen
			showProgress(true);
			
//			Create the JSON object to send to the server
			LoginCredentialsWrite credentials = new LoginCredentialsWrite(mEmail, mPassword);
			
			Gson g = new Gson();
			
			String json = g.toJson(credentials);
			
			remote.requestServerForText("api/users/sign_in", json, LKRemote.RequestType.POST, true);
		}
	}

	
	/** 
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
