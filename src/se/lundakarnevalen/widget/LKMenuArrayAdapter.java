package se.lundakarnevalen.widget;

import java.util.List;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class used for the navigation drawers list view.
 * @author Alexander Najafi
 *
 */
public class LKMenuArrayAdapter extends ArrayAdapter<LKMenuArrayAdapter.LKMenuListItem> implements OnItemClickListener {

	private final String LOG_TAG = "ArrayAdapter";
	private LayoutInflater inflater;
	public RelativeLayout inboxCounterWrapper;
	public TextView inboxCounter;

	public LKMenuArrayAdapter(Context context, List<LKMenuListItem> items){
		super(context, android.R.layout.simple_list_item_1, items);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void deleteItem(LKMenuListItem item) {
		remove(item);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		final LKMenuListItem item = getItem(pos);

		if(item.isStatic)
			return item.staticView;	

		RelativeLayout wrapper = (RelativeLayout) inflater.inflate(R.layout.menu_row, null);
		if(item.isActive){
			wrapper.setSelected(true);
			Log.d(LOG_TAG, "was selecete");
		}

		TextView text = (TextView) wrapper.findViewById(R.id.text);
		text.setText(item.title);

		// For inbox row
		if(item.isInboxRow){
			inboxCounterWrapper = (RelativeLayout) wrapper.findViewById(R.id.inbox_ctr_wrapper);
			item.inboxCounterWrapper = inboxCounterWrapper; // Do not move
			inboxCounterWrapper.setVisibility(View.VISIBLE);
			inboxCounter = (TextView) wrapper.findViewById(R.id.inbox_ctr);
			item.inboxCounter = inboxCounter; // Do not move
			inboxCounter.setText("1"); // TODO: Set to some serious value	
		}
		// For inbox row
		if(item.isMapRow){
			inboxCounterWrapper = (RelativeLayout) wrapper.findViewById(R.id.inbox_ctr_wrapper);
			item.inboxCounterWrapper = inboxCounterWrapper; // Do not move
			inboxCounter = (TextView) wrapper.findViewById(R.id.inbox_ctr);
			item.inboxCounter = inboxCounter; // Do not move
			item.rowLayout = (RelativeLayout) wrapper.findViewById(R.id.menu_element);
		}

		if(item.isActive)
			wrapper.setSelected(true);

		return wrapper;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		final LKMenuListItem item = getItem(pos);
		if(item.isMapRow) {
			SharedPreferences prefs = getContext().getSharedPreferences("MAP_FIRST", Context.MODE_PRIVATE);
			prefs.edit().putInt("firstTime", 1).commit();	
		}
		Log.d(LOG_TAG, "clicked: "+pos);
		OnClickListener listener = item.listener;
		if(listener != null){
			listener.onClick(view);
			view.setSelected(true);
			if(item.navDrawer != null && item.closeDrawerOnClick)
				item.navDrawer.closeDrawers();
			Log.d(LOG_TAG, "click");
		}
		else
			Log.d(LOG_TAG, "no listener for list item");
	}

	@Override
	public boolean isEnabled(int pos){
		// Make statics no enabled.
		return !getItem(pos).isStatic;
	}

	/**
	 * Class representing a single row in the menu. Used with the LKMenuArrayAdapter.
	 * @author alexander
	 *
	 */
	public static class LKMenuListItem{
		public int icon;
		public String title;
		OnClickListener listener;
		DrawerLayout navDrawer;
		boolean isStatic = false;
		View staticView;
		boolean closeDrawerOnClick = false;
		boolean isInboxRow = false;
		boolean isMapRow = false;
		boolean isActive = false;
		boolean pressedMap = false;
		
		public RelativeLayout rowLayout;
		
		public RelativeLayout inboxCounterWrapper;
		public TextView inboxCounter;
		
		/**
		 * std. constr.
		 */
		public LKMenuListItem(){

		}


		/**
		 * Creates list item with no click listener.
		 * @param title
		 * @param icon
		 */
		public LKMenuListItem(String title, int icon){
			this.title = title;
			this.icon = icon;
		}

		public LKMenuListItem isActive(boolean isActive){
			this.isActive = isActive;
			return this;
		}
		

		/**
		 * To be used with statics in listview.
		 * @param isStatic
		 * @return
		 */
		public LKMenuListItem isStatic(boolean isStatic){
			this.isStatic = isStatic;
			return this;
		}

		/**
		 * If isStatic is true, this view will be shown.
		 * @param view
		 * @return
		 */
		public LKMenuListItem showView(View view){
			this.staticView = view;
			return this;
		}

		/**
		 * Only to use with inbox fragment
		 * @param isInboxRow sets to show the inbox counter.
		 */
		public LKMenuListItem isInboxRow(boolean isInboxRow){
			this.isInboxRow = isInboxRow;
			return this;
		}

		/**
		 * Only to use with map fragment
		 * @param isMapRow sets to show the map !.
		 */
		public LKMenuListItem isMapRow(boolean isMapRow){
			this.isMapRow = isMapRow;
			return this;
		}

		/**
		 * Creates list item with custom click listener that is called when list item is clicked. 
		 * @param title Text in menu to show
		 * @param icon Icon next to text
		 * @param listener Listener to use to handle click events. 
		 */
		public LKMenuListItem(String title, int icon, OnClickListener listener){
			this.title = title;
			this.icon = icon;
			this.listener = listener;
		}

		/**
		 * Creates list item with click listener that starts a fragment.
		 * @param title Text in menu to show
		 * @param icon Icon next to text
		 * @param fragment Fragment to show
		 */
		public LKMenuListItem(final String title, int icon, final Fragment fragment, final FragmentManager fragmentMgr, final Context context){
			this.title = title;
			this.icon = icon;

			this.listener = new OnClickListener() {

				@Override
				public void onClick(View v) {

					clearBackStack(fragmentMgr);
					fragmentMgr.beginTransaction().replace(R.id.content_frame, fragment).commit();
				}

				private void clearBackStack(FragmentManager fragmentMgr) {

					for(int i = 0; i < fragmentMgr.getBackStackEntryCount(); i++) {
						Log.d("ContentActivity", "Removed from backstack");
						fragmentMgr.popBackStack(); 
					}
				}
			};
		}



		/**
		 * Call this to close the navigationdrawer when item is clicked. 
		 * @param closeDrawerOnClick If true the drawer will close.
		 * @param layout The drawerlayout to be closed. 
		 */
		public LKMenuListItem closeDrawerOnClick(boolean closeDrawerOnClick, DrawerLayout layout){
			this.closeDrawerOnClick = closeDrawerOnClick;
			this.navDrawer = layout;
			return this;
		}
	}
}


