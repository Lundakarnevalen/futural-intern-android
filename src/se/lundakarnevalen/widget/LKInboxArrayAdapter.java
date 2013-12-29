package se.lundakarnevalen.widget;

import java.util.List;

import se.lundakarnevalen.android.LKFragment;
import se.lundakarnevalen.android.MessageFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

public class LKInboxArrayAdapter extends ArrayAdapter<LKInboxArrayAdapter.LKMenuListItem> implements OnItemClickListener{
	private final String LOG_TAG = "InboxArrayAdapter";
	Context context;
	LayoutInflater inflater;
	LKFragment fragment;
	
	public LKInboxArrayAdapter(Context context, List<LKMenuListItem> items, LKFragment fragment) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fragment = fragment;
		
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {		
		LKMenuListItem row = getItem(pos);
		return row.layout;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		LKMenuListItem item = getItem(pos);
		MessageFragment fragment = new MessageFragment();
		fragment.setListItem(item);
		fragment.loadFragment(fragment, true);
	}
	
	public static class LKMenuListItem {
		public String message;
		public String title;
		public String date;
		public Bitmap image;
		public boolean unread;
		public RelativeLayout layout;
		public boolean isStatic = false;
		
		public LKMenuListItem(String title, String message, String date, boolean unread, Bitmap image) {
			this.message = message;
			this.title = title;
			this.date = date;
			this.image = image;
			this.unread = unread;
		}
	}

}
