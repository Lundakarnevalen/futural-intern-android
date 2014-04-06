package se.lundakarnevalen.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import fragments.FrSignIn;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AcLogin extends ActionBarActivity {
	
	private ActionBar actionBar;
	TextView titleView;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_layout_login);
		
		actionBar = getSupportActionBar();
		
		buildActionBar();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		FrSignIn fragment = new FrSignIn();
		fragmentTransaction.add(R.id.content_frame, fragment);
		fragmentTransaction.commit();
		
	}
	
	private void buildActionBar() {
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.ac_login_action_bar, null);
		// Get references for actionbar setup
		
		titleView = (TextView) root.findViewById(R.id.action_bar_text);
		
		actionBar.setCustomView(root);
	}

	public void setActionBarTitle(String title) {
		titleView.setText(title);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		TODO Do we want a menu here?
//		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
