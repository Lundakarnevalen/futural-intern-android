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

public class LKInboxArrayAdapter extends ArrayAdapter<LKInboxArrayAdapter.LKMenuListItem> implements OnItemClickListener{
	
	Context context;
	LayoutInflater inflater;
	
	public LKInboxArrayAdapter(Context context, List<LKMenuListItem> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		LKMenuListItem item = getItem(pos);
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.inbox_row, null);
		LKTextView titleTextView = (LKTextView) root.findViewById(R.id.inbox_message_title);
		LKTextView messagePreviewTextView = (LKTextView) root.findViewById(R.id.inbox_message_preview);
		LKTextView dateTextView = (LKTextView) root.findViewById(R.id.inbox_message_date);
		ImageView thumbnailView = (ImageView) root.findViewById(R.id.inbox_message_thumbnail);
		
		titleTextView.setText(item.title);
		
		String messagePreview = item.message.substring(0,40)+"...";
		messagePreviewTextView.setText(messagePreview);
		
		dateTextView.setText(item.date);
		
		Bitmap bitmap = item.image;
		thumbnailView.setImageBitmap(bitmap);
		
		return root;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
	}
	
	public static class LKMenuListItem {
		public String message;
		public String title;
		public String date;
		public Bitmap image;
		
		public LKMenuListItem(String title, String message, String date, Bitmap image) {
			this.message = message;
			this.title = title;
			this.date = date;
			this.image = image;
		}
	}

}
