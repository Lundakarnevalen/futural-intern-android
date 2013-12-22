package se.lundakarnevalen.widget;

import java.util.List;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Class used for the navigation drawers list view.
 * @author Alexander Najafi
 *
 */
public class LKMenuArrayAdapter extends ArrayAdapter<LKMenuArrayAdapter.LKMenuListItem> implements OnItemClickListener{
	
	private final String LOG_TAG = "ArrayAdapter";
	Context context;
	
	public LKMenuArrayAdapter(Context context, List<LKMenuListItem> items){
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		final LKMenuListItem item = getItem(pos);
		
		TextView tv = new TextView(context);
		tv.setText(item.title);
		tv.setTextSize(25);
		
		return tv;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		final LKMenuListItem item = getItem(pos);
		Log.d(LOG_TAG, "clicked: "+pos);
		OnClickListener listener = item.listener;
		if(listener != null)
			listener.onClick(view);
		else
			Log.d(LOG_TAG, "no listener for list item");
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
		
		/**
		 * Creates list item with no click listener.
		 * @param title
		 * @param icon
		 */
		public LKMenuListItem(String title, int icon){
			this.title = title;
			this.icon = icon;
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
		public LKMenuListItem(String title, int icon, final Fragment fragment, final FragmentManager fragmentMgr){
			this.title = title;
			this.icon = icon;
			
			this.listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					fragmentMgr.beginTransaction().replace(R.id.content_frame, fragment).commit();
				}
			};
		}
	}
}


