package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.widget.LKMenuArrayAdapter;
import se.lundakarnevalen.widget.LKMenuArrayAdapter.LKMenuListItem;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentActivity extends ActionBarActivity{
	
	private final String LOG_TAG = "ContentActivity";
	
	boolean drawerOpen;
	private ActionBar actionBar;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView menuList;
	private FragmentManager fragmentMgr;
	private RelativeLayout menuButtonWrapper;
	private TextView title;
	
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
	 * Set title in actionbar
	 * @param title The new title to set. 
	 */
	public void setTitle(String title){
		this.title.setText(title);
	}
	
	/**
	 * Sets up the ListView in the navigationdrawer menu.
	 */
	private void populateMenu(){
		// Create logo and sigill objects. 
		ImageView menuLogo = new ImageView(this);
		ImageView menuSigill = new ImageView(this);
		ListView.LayoutParams params = new ListView.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		menuLogo.setLayoutParams(params);
		menuSigill.setLayoutParams(params);
		
		List<LKMenuListItem> listItems = new ArrayList<LKMenuListItem>();
		listItems.add(new LKMenuListItem().isStatic(true).showView(menuLogo));
		listItems.add(new LKMenuListItem("Start", 0, new RegistrationFragment(), fragmentMgr).closeDrawerOnClick(true, drawerLayout).isActive(true));
		listItems.add(new LKMenuListItem("Sektioner", 0, new SektionerFragment(), fragmentMgr).closeDrawerOnClick(true, drawerLayout));
		listItems.add(new LKMenuListItem("Inkorg", 0, new InboxFragment(), fragmentMgr).closeDrawerOnClick(true, drawerLayout).isInboxRow(true));
		listItems.add(new LKMenuListItem("Om appen", 0, new AboutFragment(), fragmentMgr).closeDrawerOnClick(true, drawerLayout));
		listItems.add(new LKMenuListItem().isStatic(true).showView(menuSigill));
		
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
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.drawer_toggle_icon, 0, 0){
			
			/**
			 * Drawer closed
			 * */
	        public void onDrawerClosed(View view) {
	            // TODO: Fix UI in actionBar
	        	setTitle("closed");
	        	drawerOpen = false;
	        	Log.d(LOG_TAG, "closed");
	        }

	        /**
	         * Drawer opened
	         * */
	        public void onDrawerOpened(View drawerView) {
	            // TODO: Fix UI in actionBar
	        	setTitle("open");
	        	drawerOpen = true;
	        	Log.d(LOG_TAG, "open");
	        }
		};
		
		drawerLayout.setDrawerListener(drawerToggle);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.actionbar_layout, null);
		// Get references for actionbar setup
		menuButtonWrapper = (RelativeLayout) root.findViewById(R.id.menu_drawer_toggle_wrapper);	
		title = (TextView) root.findViewById(R.id.title);
		
		menuButtonWrapper.setOnClickListener(menuToggleListener);
		actionBar.setCustomView(root);
	}
	
	private View.OnClickListener menuToggleListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(drawerOpen)
				drawerLayout.closeDrawers();
			else
				drawerLayout.openDrawer(menuList);
		}
	};
}
