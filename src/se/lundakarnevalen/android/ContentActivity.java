package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.widget.LKMenuArrayAdapter;
import se.lundakarnevalen.widget.LKMenuArrayAdapter.LKMenuListItem;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ContentActivity extends ActionBarActivity{
	
	private final String LOG_TAG = "ContentActivity";
	
	boolean drawerOpen;
	private ActionBar actionBar;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView menuList;
	private FragmentManager fragmentMgr;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_wrapper);
		actionBar = getSupportActionBar();
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuList = (ListView) findViewById(R.id.menu_list);
		
		fragmentMgr = getSupportFragmentManager();
		
		setupActionBar();
		populateMenu();
	}
	
	/**
	 * Sets up the ListView in the navigationdrawer menu.
	 */
	private void populateMenu(){
		List<LKMenuListItem> listItems = new ArrayList<LKMenuListItem>();
		listItems.add(new LKMenuListItem("Registrering", 0, new RegistrationFragment(), fragmentMgr));
		listItems.add(new LKMenuListItem("Sektioner", 0, new SektionerFragment(), fragmentMgr));
		listItems.add(new LKMenuListItem("Inkorg", 0, new InboxFragment(), fragmentMgr));
		
		LKMenuArrayAdapter adapter = new LKMenuArrayAdapter(this, listItems);
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(adapter);
	}
	
	/**
	 * Called to init actionbar. 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(drawerToggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called to init actionbar.
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	/**
	 * Called to init actionbar. 
	 */
	@Override
	public void onConfigurationChanged(Configuration config){
		super.onConfigurationChanged(config);
		drawerToggle.onConfigurationChanged(config);
	}
	
	/**
	 * Sets up the actionbar with listener and its UI. 
	 */
	private void setupActionBar(){		
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0){
			
			/**
			 * Drawer closed
			 * */
	        public void onDrawerClosed(View view) {
	            // TODO: Fix UI in actionBar
	        	Log.d(LOG_TAG, "closed");
	        }

	        /**
	         * Drawer opened
	         * */
	        public void onDrawerOpened(View drawerView) {
	            // TODO: Fix UI in actionBar
	        	Log.d(LOG_TAG, "open");
	        }
		};
		drawerLayout.setDrawerListener(drawerToggle);
		
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
}
