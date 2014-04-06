package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MessageFragment extends LKFragment{
	
	LKInboxArrayAdapter.LKMenuListItem listItem;
	
	public void setListItem(LKInboxArrayAdapter.LKMenuListItem listItem){
		this.listItem = listItem;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.message_layout, null);
		LKTextViewBold titleTextView = (LKTextViewBold) root.findViewById(R.id.message_title);
		LKTextViewBold dateTextView = (LKTextViewBold) root.findViewById(R.id.message_date);
		LKTextView messageTextView = (LKTextView) root.findViewById(R.id.message_message_text);
		ImageView thumbnailImageView = (ImageView) root.findViewById(R.id.message_thumbnail);
		
		titleTextView.setText(listItem.title.toUpperCase());
		dateTextView.setText(listItem.date);
		messageTextView.setText(listItem.message);
		thumbnailImageView.setImageBitmap(listItem.image);
		
		//TODO:	Update the inbox counter
		this.setInboxCount();
		
		return root;
	}
}
