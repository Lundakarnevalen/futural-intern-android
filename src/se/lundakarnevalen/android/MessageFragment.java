package se.lundakarnevalen.android;

import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends LKFragment{
	
	LKInboxArrayAdapter.LKMenuListItem listItem;
	
	public void setListItem(LKInboxArrayAdapter.LKMenuListItem listItem){
		this.listItem = listItem;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView tv = new TextView(getContext());
		tv.setText(listItem.title);
		return tv;
	}
}
