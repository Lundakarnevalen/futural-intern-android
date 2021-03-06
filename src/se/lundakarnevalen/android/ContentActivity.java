package se.lundakarnevalen.android;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import json.Notification;
import json.Response;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKMenuArrayAdapter;
import se.lundakarnevalen.widget.LKMenuArrayAdapter.LKMenuListItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import fragments.CountdownFragment;
import fragments.IdentificationFragment;
import fragments.InboxFragment;
import fragments.LKFragment;
import fragments.LKFragment.MessangerMessage;
import fragments.SongGroupsFragment;
import fragments.futugram.FrKarnegram;

public class ContentActivity extends ActionBarActivity implements
		LKFragment.Messanger {

	private CountdownFragment countDown;
	private final String LOG_TAG = ContentActivity.class.getSimpleName();

	final Calendar startTimeMap = Calendar.getInstance();
	final Calendar endTimeMap = Calendar.getInstance();

	private LKMenuArrayAdapter adapter;

	private LKMenuListItem mapItem = null;

	private String IMAGE_PATH = "karneval.image";
	
	boolean drawerOpen;
	private ActionBar actionBar;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView menuList;
	private FragmentManager fragmentMgr;
	LKMenuListItem inboxListItem;

	LKUser user;
	
	private RelativeLayout menuButtonWrapper;
	private RelativeLayout inboxIndicatorWrapper;
	private TextView actionbarInboxCounter;

	private ImageView actionBarLogo;
	private TextView title;
	private Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		trackingStart();

		setContentView(R.layout.content_wrapper);

		actionBar = getSupportActionBar();

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menuList = (ListView) findViewById(R.id.menu_list);

		fragmentMgr = getSupportFragmentManager();

		setupActionBar();

		loadFragment(new CountdownFragment(), false);

	}

	@Override
	public void onStop() {
		super.onStop();
		trackingStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume");

		updateUserFromServer();
		
		populateMenu();

		// Check if need to register for new gcm.
		// SharedPreferences sp = getSharedPreferences(LKFragment.SP_GCM_NAME,
		// MODE_PRIVATE);
		// String gcmId = sp.getString(LKFragment.SP_GCM_REGID, null);
		String gcmId = SplashscreenActivity.getRegId(this);
		Log.d(LOG_TAG, "gcmId: " + gcmId);
		if (gcmId == null || gcmId.equals("")) {

			Log.d(LOG_TAG, "gcmId was either null or empty, register again");
			// Try to get new gcm.
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
			SplashscreenActivity.regInBackground(this, gcm);
		} else {

		}

		Log.d("SplashScreen", "Starting getMessages()");
		LKRemote remote = new LKRemote(context,
				new LKRemote.TextResultListener() {

					@Override
					public void onResult(String result) {
						Log.d("SplashScreen", "onResult(): " + result);
						if (result == null) {
							Log.d("SplashScreen", "Result from server was null");
							return;
						}
						Gson gson = new Gson();
						Response.Notifications notifications = gson.fromJson(
								result, Response.Notifications.class);
						notifications.parseMessages();
						Notification[] messages = notifications.messages;
						LKSQLiteDB db = new LKSQLiteDB(context);
						Log.d("ContentAct",
								"Created db object. Starting loop. messages.length = "
										+ messages.length);
						for (int i = 0; i < messages.length; i++) {
//							Log.d("ContentAct", "loop counter i = " + i);
							if (!db.messageExistsInDb(messages[i].id)) {
								Log.d("SplashScreen", "Message not in db");
								db.addItem(new LKInboxArrayAdapter.LKMenuListItem(
										messages[i].title, messages[i].message,
										messages[i].created_at,
										messages[i].recipient_id,
										messages[i].id, true));
							}
							Log.d(LOG_TAG, "done");
						}
						Log.d(LOG_TAG, "loop done");
						db.close();
						Log.d("ContentAct", "Completed getMessages");
					}
				});

		
		remote.showProgressDialog(false);
		Log.d("SplashScreen", "Starting server request");
		
		LKUser tmpUser = new LKUser(this);
		tmpUser.getUserLocaly();

		remote.requestServerForText("api/notifications.json?token="
				+ tmpUser.token, "", RequestType.GET, false);

		this.setInboxCount();

	}
	
	private void updateUserFromServer() {
		// update JSon form server
		LKUser user = new LKUser(this);
		user.getUserLocaly();
		user.updateFromRemote();
	}

	/**
	 * Called when a fragment messages the activity.
	 */
	@Override
	public void message(MessangerMessage message, Bundle data) {
		switch (message) {
		case SET_TITLE:
			setTitle(data.getString("title"));
			break;
		case SET_INBOX_COUNT:
			setInboxCount();
			break;
		case SHOW_ACTION_BAR_LOGO:
			showActionBarLogo(data.getBoolean("show", false));
			break;
		}
	}

	/**
	 * Handles radiobuttons in the fragment
	 * 
	 * @param view
	 *            radio button view
	 */
	public void onIntrestsRadioButtonClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr
					.findFragmentById(R.id.content_frame);
			fragment.onIntrestsRadioButtonClicked(view);
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "could not get fragment." + e.toString());
		}
	}

	/**
	 * Handles checkboxes for sections in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onSectionCheckBoxClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr
					.findFragmentById(R.id.content_frame);
			fragment.onSectionCheckBoxClicked(view);
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "could not get fragment.");
		}
	}

	/**
	 * Handles checkboxes for intrests in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onIntrestsCheckBoxClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr
					.findFragmentById(R.id.content_frame);
			fragment.onIntrestsCheckBoxClicked(view);
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "could not get fragment.");
		}
	}

	/**
	 * Handles checkboxes in the fragment
	 * 
	 * @param view
	 *            checkbox view
	 */
	public void onCheckBoxClicked(View view) {
		try {
			LKFragment fragment = (LKFragment) fragmentMgr
					.findFragmentById(R.id.content_frame);
			fragment.onCheckBoxClicked(view);
		} catch (ClassCastException e) {
			Log.e(LOG_TAG, "could not get fragment.");
		}
	}

	/**
	 * Show actionbar logo or text
	 * 
	 * @param show
	 *            If true, the logo will be visible if false, the text will be
	 *            shown.
	 */
	private void showActionBarLogo(boolean show) {
		if (!show) {
			actionBarLogo.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
		} else {
			title.setVisibility(View.GONE);
			actionBarLogo.setVisibility(View.VISIBLE);
		}
	}

	// -1 = nothing
	// 0 = press
	// 1 = !
	private void setMapMark(int nbr) {
		if (mapItem != null) {
			if (nbr == 1) {
				mapItem.inboxCounter.setText("!");
				mapItem.text.setTextColor(getResources().getColor(
						R.color.menu_item_enabled_text));
				mapItem.buttonLayout
						.setBackgroundResource(R.drawable.menu_row_selector);

				mapItem.inboxCounterWrapper.setVisibility(View.VISIBLE);
			} else if (nbr == 0) {
				mapItem.text.setTextColor(getResources().getColor(
						R.color.menu_item_enabled_text));
				mapItem.buttonLayout
						.setBackgroundResource(R.drawable.menu_row_selector);

				mapItem.inboxCounterWrapper.setVisibility(View.GONE);
			} else if (nbr == -1) {
				// TODO DO DARKER!!
				mapItem.text.setTextColor(getResources().getColor(
						R.color.menu_item_disabled_text));
				mapItem.buttonLayout
						.setBackgroundResource(R.color.menu_item_disabled);
			} else if (nbr == 2) {
				// TODO
				// Nu blir det tomt. �ndra s� blir m�rk igen..
				adapter.remove(mapItem);
			}
		}
	}

	public void setInboxCount() {
		final Context context = this;
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {

				Log.d("AsyncTask", "Started background process.");
				LKSQLiteDB db = new LKSQLiteDB(context);
				int n = db.numberOfUnreadMessages();
				db.close();
				Log.d("AsyncTask", "Finnished background process.");
				Log.d("FUUUCKCKCKC!", n + "");
				return n;
			}

			@Override
			protected void onPostExecute(Integer result) {
				Log.d("AsyncTask", "Started postexecute process.");
				if (result <= 0) {
					// Hide UI elements
					try {
						inboxIndicatorWrapper.setVisibility(View.GONE);
						Log.d("ContentActivity", inboxListItem == null ? "null"
								: "not null");
						Log.d("ContentActivity",
								inboxListItem.inboxCounterWrapper == null ? "null"
										: "not null");
						inboxListItem.inboxCounterWrapper
								.setVisibility(View.GONE);
						Log.d("ContentActivity", "did some shitzz!");
					} catch (NullPointerException e) {
						Log.e(LOG_TAG, "Was activity dead?");
					}
				} else {
					// set values and show UI elements
					try {
						inboxIndicatorWrapper.setVisibility(View.VISIBLE);
						inboxListItem.inboxCounterWrapper
								.setVisibility(View.VISIBLE);
						actionbarInboxCounter.setText(String.valueOf(result));
						inboxListItem.inboxCounter.setText(String
								.valueOf(result));
						Log.d("ContentActivity", "did other shitz!");
					} catch (NullPointerException e) {
						Log.e(LOG_TAG, "Was activity dead?");
					}
				}
			}
		}.execute();

	}

	/**
	 * Loads new fragment into the frame.
	 */
	@Override
	public void loadFragment(Fragment fragment, boolean addToBackstack) {
		Log.d("ContentActivity", "loadFragment()");
		FragmentTransaction transaction = fragmentMgr.beginTransaction()
		// .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
		// android.R.anim.fade_in, android.R.anim.fade_out) does this really
		// work?
				.replace(R.id.content_frame, fragment);

		if (addToBackstack)
			transaction.addToBackStack(null);

		transaction.commit();
	}

	@Override
	public void popFragmentStack() {
		Log.i("ContentActivity", "fragmentMgr.popBackStack()");
		fragmentMgr.popBackStack();
	}

	/**
	 * Set title in actionbar
	 * 
	 * @param title
	 *            The new title to set.
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * Sets up the ListView in the navigationdrawer menu.
	 */
	public void populateMenu() {
		// Create logo and sigill objects.
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menuSigill = inflater.inflate(R.layout.menu_static_sigill, null);

		buildMenuProfile(menuSigill);

		List<LKMenuListItem> listItems = new ArrayList<LKMenuListItem>();

		listItems.add(new LKMenuListItem().isStatic(true).showView(menuSigill));
		inboxListItem = new LKMenuListItem(getString(R.string.Inkorg), 0,
				new InboxFragment(), fragmentMgr, this, true)
				.closeDrawerOnClick(true, drawerLayout).isInboxRow(true);

		// listItems.add(new LKMenuListItem("Music", 0, new MusicFragment(),
		// fragmentMgr, this).closeDrawerOnClick(true, drawerLayout));
		// TODO fix block
		// listItems.add(new LKMenuListItem("Start", 0, null, fragmentMgr,
		// this).closeDrawerOnClick(true, drawerLayout).isActive(true));
		// listItems.add(new LKMenuListItem("Map", 0, new MapFragment(),
		// fragmentMgr, this).closeDrawerOnClick(true, drawerLayout));
		// listItems.add(new LKMenuListItem("Sektioner", 0, new
		// SectionsFragment(), fragmentMgr, this).closeDrawerOnClick(true,
		// drawerLayout));

		// TODO Map only available on tidningsdagen
		countDown = new CountdownFragment();
		listItems.add(new LKMenuListItem(getString(R.string.countdown_title),
				0, countDown, fragmentMgr, this, true).closeDrawerOnClick(true,
				drawerLayout));

		// TODO fix block

		startTimeMap.set(2014, Calendar.APRIL, 11, 22, 59, 00);
		endTimeMap.set(2014, Calendar.APRIL, 13, 05, 30, 00);

		Calendar c = Calendar.getInstance();

		// only add map if before..
//		if (c.after(endTimeMap)) {
//			// Nothing happen
//		} else if (c.after(startTimeMap)) {
//			mapItem = new LKMenuListItem(getString(R.string.karta), 0,
//					new MapFragment(), fragmentMgr, this, true)
//					.closeDrawerOnClick(true, drawerLayout).isMapRow(true);
//			listItems.add(mapItem);
//		} else {
//
//			mapItem = new LKMenuListItem(getString(R.string.karta), 0,
//					new MapFragment(), fragmentMgr, this, false)
//					.closeDrawerOnClick(true, drawerLayout).isMapRow(true);
//			listItems.add(mapItem);
//		}

		listItems.add(inboxListItem);

		// listItems.add(new LKMenuListItem("Om appen", 0, new AboutFragment(),
		// fragmentMgr, this).closeDrawerOnClick(true, drawerLayout));

		LKMenuListItem sangbok = new LKMenuListItem(getString(R.string.sangbok_title), 0, new SongGroupsFragment(), fragmentMgr, this, true).closeDrawerOnClick(true, drawerLayout);
		
		listItems.add(new LKMenuListItem(getString(R.string.karnegram), 0, new FrKarnegram(), fragmentMgr, this, true).closeDrawerOnClick(true, drawerLayout));
		
		listItems.add(sangbok);

		if(user.aktiv) {
			
		LKMenuListItem identification = new LKMenuListItem(
				getString(R.string.identification_title), 0, new IdentificationFragment(),
				fragmentMgr, this, true).closeDrawerOnClick(true, drawerLayout);
		listItems.add(identification);
		}
		
		adapter = new LKMenuArrayAdapter(this, listItems);
		menuList.setAdapter(adapter);

		menuList.setOnItemClickListener(adapter);
	}

	private void buildMenuProfile(View menuSigill) {
		final ImageView image = (ImageView) menuSigill
				.findViewById(R.id.user_picture);
		TextView name = (TextView) menuSigill.findViewById(R.id.user_name);

		user = new LKUser(this);
		user.getUserLocaly();

		if (user.fornamn == null || user.efternamn == null) {
			name.setText("John Doe");
		} else {
			name.setText((user.fornamn + " " + user.efternamn).toUpperCase());
			name.setLineSpacing(19, 1);

		}


		
		SharedPreferences prefs = getSharedPreferences(IMAGE_PATH, Context.MODE_PRIVATE);
		String imageString = prefs.getString(IMAGE_PATH, null);
		if(imageString==null) {
		LKRemote remote = new LKRemote(this);
		remote.setBitmapResultListener(new BitmapResultListener() {
			@Override
			public void onResult(Bitmap result) {
				if (result == null) {
														// standard image?
				}
				try {
				SharedPreferences prefs = getSharedPreferences(IMAGE_PATH, Context.MODE_PRIVATE);
				
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				result.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
				byte[] byteArray2 = byteArray.toByteArray();
				
				String encoded = Base64.encodeToString(byteArray2, Base64.DEFAULT);
				prefs.edit().putString(IMAGE_PATH, encoded).commit();
				
				image.setVisibility(View.VISIBLE);
				image.setImageBitmap(result);
				} catch(NullPointerException e) {
					
				}
			}
		});
		remote.requestServerForBitmap(user.imgUrl);
		} else {
			byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap result = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			image.setVisibility(View.VISIBLE);
			image.setImageBitmap(result);
		}
	}

	/**
	 * Called to init actionbar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Called to init actionbar.
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	/**
	 * Called to init actionbar.
	 */
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		drawerToggle.onConfigurationChanged(config);
	}

	/**
	 * Sets up the actionbar with listener and its UI.
	 */
	private void setupActionBar() {
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.drawer_toggle_icon, 0, 0) {

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
				if (mapItem != null) {
					Calendar c = Calendar.getInstance();
					if (c.compareTo(startTimeMap) == 1) {
						if (!(c.compareTo(endTimeMap) == 1)) {
							SharedPreferences prefs = getSharedPreferences(
									"MAP_FIRST", Context.MODE_PRIVATE);
							int first = prefs.getInt("firstTime", -1);
							if (first == 1) {
								mapItem.enable = true;
								setMapMark(0);
							} else {
								mapItem.enable = true;
								setMapMark(1);
							}
						} else {
							setMapMark(2);
							mapItem = null;
						}
					} else {

						mapItem.enable = false;
						setMapMark(-1);
					}
				}
				drawerOpen = true;
				Log.d(LOG_TAG, "open");
			}
		};
		Log.d(LOG_TAG, "setup actionbar!");
		drawerLayout.setDrawerListener(drawerToggle);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.actionbar_layout, null);
		// Get references for actionbar setup
		menuButtonWrapper = (RelativeLayout) root
				.findViewById(R.id.menu_drawer_toggle_wrapper);
		inboxIndicatorWrapper = (RelativeLayout) root
				.findViewById(R.id.inbox_indicator_wrapper);
		actionbarInboxCounter = (TextView) root
				.findViewById(R.id.inbox_indicator);
		actionBarLogo = (ImageView) root.findViewById(R.id.action_bar_logo);

		title = (TextView) root.findViewById(R.id.title);

		menuButtonWrapper.setOnClickListener(menuToggleListener);
		actionBar.setCustomView(root);
	}

	private View.OnClickListener menuToggleListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (drawerOpen)
				drawerLayout.closeDrawers();
			else
				drawerLayout.openDrawer(menuList);
		}
	};

	private void trackingStart() {
		EasyTracker.getInstance().activityStart(this);
	}

	private void trackingStop() {
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int backCount = getSupportFragmentManager()
					.getBackStackEntryCount();

			if (backCount == 0) {
				if (countDown != null) {
					countDown.stopMusic();
				}
			}
			// and so on...
		}
		return super.onKeyDown(keyCode, event);
	}

}
