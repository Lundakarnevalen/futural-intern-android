package se.lundakarnevalen.widget;

import java.util.List;

import fragments.LKFragment;
import fragments.MessageFragment;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKSQLiteDB;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
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
	FragmentManager fragmentManager;
	
	public LKInboxArrayAdapter(Context context, List<LKMenuListItem> items, LKFragment fragment) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fragment = fragment;
		
	}
	
	public void setFragmentManager(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {		
		LKMenuListItem row = getItem(pos);
		return row.layout;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		LKMenuListItem item = getItem(pos);
		item.unread = false;
		Log.d("LKInboxArrayAdapter", "changed item.unread = "+item.unread);
		LKSQLiteDB db = new LKSQLiteDB(context);
		int numOfAffectedMessages = db.update(item);
		
		db.close();
		Log.d("LKInboxArrayAdapter", "DB query completed. "+numOfAffectedMessages+" rows affected");
		
		
		MessageFragment fragment = new MessageFragment();
		fragment.setListItem(item);
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
		//TODO: Mark message as read. I.e set item.unread=false;
	}
	
	public static class LKMenuListItem {
		public String message;
		public String title;
		public String date;
		public int recipients;
		public String sectionName;
		public int id;
		public Bitmap image;
		public boolean unread;
		public RelativeLayout layout;
		public boolean isStatic = false;
		
		public LKMenuListItem(String title, String message, String date, int recipients, int id, boolean unread, Bitmap image) {
			this.message = message;
			this.title = title;
			this.date = date;
			this.image = image;
			this.unread = unread;
			this.id = id;
			this.recipients = recipients;
			this.sectionName = R.id.
		}
		
		public String toString(){
			return "title: "+this.title;
		}
	}

}
