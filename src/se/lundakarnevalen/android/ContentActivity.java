package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.android.LKFragment.MessangerMessage;
import se.lundakarnevalen.widget.LKMenuArrayAdapter;
import se.lundakarnevalen.widget.LKMenuArrayAdapter.LKMenuListItem;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class ContentActivity extends ActionBarActivity implements LKFragment.Messanger{
	
	private final String LOG_TAG = "ContentActivity";
	
	boolean drawerOpen;
	private ActionBar actionBar;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView menuList;
	private FragmentManager fragmentMgr;
	LKMenuListItem inboxListItem;
	
	private RelativeLayout menuButtonWrapper;
	private RelativeLayout inboxIndicatorWrapper;
	private TextView actionbarInboxCounter;
	
	private ImageView actionBarLogo;
	private TextView title;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle inData = getIntent().getExtras();
		
		setContentView(R.layout.content_wrapper);
		actionBar = getSupportActionBar();
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuList = (ListView) findViewById(R.id.menu_list);
		
		fragmentMgr = getSupportFragmentManager();
		
		setupActionBar();
		populateMenu();
		Intent intent = getIntent();
		int fragment = intent.getIntExtra("fragment", Integer.MIN_VALUE);
		LKFragment fragmentToLoad = null;
		switch(fragment){
		case LKFragment.INBOX_FRAGMENT:
			fragmentToLoad = new InboxFragment();
			break;
		default:
			fragmentToLoad = LKFragment.getStartFragment(this);
		}
		loadFragment(fragmentToLoad, false);
	}
	
	
	/**
	 * Called when a fragment messages the activity.
	 */
	@Override
	public void message(MessangerMessage message, Bundle data) {
			switch(message){
			case SET_TITLE:
				setTitle(data.getString("title"));
				break;
			case SET_INBOX_COUNT:
				setInboxCount(data.getInt("count", 0));
				break;
			case SHOW_ACTION_BAR_LOGO:
				showActionBarLogo(data.getBoolean("show", false));
				break;
			}
	}
	
	/**
	 * Handles radiobuttons in the fragment
	 * @param view radio button view
	 */
	public void onRadioButtonClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr.findFragmentById(R.id.content_frame);
			fragment.onRadioButtonClicked(view);
		} catch(ClassCastException e) {
			Log.e(LOG_TAG,"could not get fragment.");
		}
	}
	/**
	 * Handles checkboxes in the fragment
	 * @param view checkbox view
	 */
	public void onCheckBoxClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr.findFragmentById(R.id.content_frame);
			fragment.onCheckBoxClicked(view);
		} catch(ClassCastException e) {
			Log.e(LOG_TAG,"could not get fragment.");
		}
	}
	
	/**
	 * Show actionbar logo or text
	 * @param show If true, the logo will be visible if false, the text will be shown. 
	 */
	private void showActionBarLogo(boolean show){
		if(!show){
			actionBarLogo.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
		}else{
			title.setVisibility(View.GONE);
			actionBarLogo.setVisibility(View.VISIBLE);
		}
	}
	
	private void setInboxCount(int count){
		if(count <= 0){
			// Hide UI elements
			inboxIndicatorWrapper.setVisibility(View.GONE);
			inboxListItem.inboxCounterWrapper.setVisibility(View.GONE);	
		}else{
			// set values and show UI elements
			inboxIndicatorWrapper.setVisibility(View.VISIBLE);
			inboxListItem.inboxCounterWrapper.setVisibility(View.VISIBLE);
			
			actionbarInboxCounter.setText(String.valueOf(count));
			inboxListItem.inboxCounter.setText(String.valueOf(count));
		}
	}
	
	/**
	 * Loads new fragment into the frame.
	 */
	@Override
	public void loadFragment(Fragment fragment, boolean addToBackstack){
		FragmentTransaction transaction = fragmentMgr.beginTransaction().replace(R.id.content_frame, fragment);
		if(addToBackstack)
			transaction.addToBackStack(null);
		transaction.commit();
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
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menuSigill = inflater.inflate(R.layout.menu_static_sigill, null);
		
		inboxListItem = new LKMenuListItem("Inkorg", 0, new InboxFragment(), fragmentMgr, this).closeDrawerOnClick(true, drawerLayout).isInboxRow(true);
		List<LKMenuListItem> listItems = new ArrayList<LKMenuListItem>();
		listItems.add(new LKMenuListItem("Start", 0, null, fragmentMgr, this).closeDrawerOnClick(true, drawerLayout).isActive(true));
		listItems.add(new LKMenuListItem("Sektioner", 0, new SectionsFragment(), fragmentMgr, this).closeDrawerOnClick(true, drawerLayout));

		listItems.add(inboxListItem);
		//listItems.add(new LKMenuListItem("Om appen", 0, new RegistrationFragment(), fragmentMgr, this).closeDrawerOnClick(true, drawerLayout));
		
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
	        	drawerOpen = false;
	        	Log.d(LOG_TAG, "closed");
	        }

	        /**
	         * Drawer opened
	         * */
	        public void onDrawerOpened(View drawerView) {
	            // TODO: Fix UI in actionBar
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
		inboxIndicatorWrapper = (RelativeLayout) root.findViewById(R.id.inbox_indicator_wrapper);
		actionbarInboxCounter = (TextView) root.findViewById(R.id.inbox_indicator);
		actionBarLogo = (ImageView) root.findViewById(R.id.action_bar_logo);
		
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
