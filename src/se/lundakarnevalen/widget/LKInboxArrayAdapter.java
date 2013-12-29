package se.lundakarnevalen.widget;

import java.util.List;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LKInboxArrayAdapter extends ArrayAdapter<RelativeLayout> implements OnItemClickListener{
	
	Context context;
	LayoutInflater inflater;
	
	public LKInboxArrayAdapter(Context context, List<RelativeLayout> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {		
		RelativeLayout row = getItem(pos);
		return row;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
	}
	
	public static class LKMenuListItem {
		public String message;
		public String title;
		public String date;
		public Bitmap image;
		public boolean unread;
		
		public LKMenuListItem(String title, String message, String date, boolean unread, Bitmap image) {
			this.message = message;
			this.title = title;
			this.date = date;
			this.image = image;
			this.unread = unread;
		}
	}

}
