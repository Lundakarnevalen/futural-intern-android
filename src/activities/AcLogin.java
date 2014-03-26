package activities;

import se.lundakarnevalen.android.R;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import fragments.FrSignIn;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AcLogin extends ActionBarActivity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_layout_login);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		FrSignIn fragment = new FrSignIn();
		fragmentTransaction.add(R.id.content_frame, fragment);
		fragmentTransaction.commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		TODO Do we want a menu here?
//		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
}
