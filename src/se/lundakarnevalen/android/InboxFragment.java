package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class InboxFragment extends LKFragment{
	
	ListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.inbox_layout, null);
		
		listView = (ListView) root.findViewById(R.id.inbox_list_view);
		
		return root; 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		List<LKMenuListItem> items = new ArrayList<LKMenuListItem>();
		
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));
		items.add(new LKMenuListItem("Bajs","Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy","2013-12-27 13:37", BitmapFactory.decodeResource(getResources(), R.drawable.rund)));

		LKInboxArrayAdapter adapt = new LKInboxArrayAdapter(getActivity(), items);
		listView.setAdapter(adapt);
	}
}
