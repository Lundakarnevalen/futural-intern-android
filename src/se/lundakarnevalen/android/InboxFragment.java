package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import se.lundakarnevalen.widget.LKTextView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class InboxFragment extends LKFragment{
	
	ListView listView;
	Context context;
	ProgressBar progressCircle;
	LKFragment fragment;
	FragmentManager fragmentManager;
	LKSQLiteDB db;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.inbox_layout, null);
		this.fragment = this;
		listView = (ListView) root.findViewById(R.id.inbox_list_view);
		context = getContext();
		progressCircle = (ProgressBar) root.findViewById(R.id.inbox_progress_circle);

		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Inbox");
		fragmentManager = getActivity().getSupportFragmentManager();
		new RenderingTask().execute(context);
		
		// Code to add dummy data into database.
		//LKSQLiteDB dbDummy = new LKSQLiteDB(context);
		//dbDummy.addItem(new LKMenuListItem("Title", "This is a short message.", "2015-15-34", true, null));
	}
	
	public class RenderingTask extends AsyncTask<Context,Void,Void> {
		ArrayList<LKMenuListItem> items = new ArrayList<LKMenuListItem>();
		
		protected void onPreExecute(Context... context) {
			progressCircle.setVisibility(View.VISIBLE);
			Log.d("RenderingTask", "Completed onPreExecute()");
		}
		
		@Override
		protected Void doInBackground(Context... context) {
			db = new LKSQLiteDB(context[0]);
			List<LKMenuListItem> data = db.getMessages();
			
			//Get inflater
			LayoutInflater inflater = (LayoutInflater) context[0].getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			//Add empty view to beginning of list as top margin
			ListView.LayoutParams lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, context[0].getResources().getDimensionPixelSize(R.dimen.horizontal_margin));
			RelativeLayout r = new RelativeLayout(context[0]);
			r.setLayoutParams(lp);
			LKMenuListItem l = new LKMenuListItem("","","",false,null);
			l.layout = r;
			l.isStatic = true;
			items.add(l);
			
			for(LKMenuListItem message : data){
				items.add(message);
			}
			
			//Populate rowList with data from items
			for(LKInboxArrayAdapter.LKMenuListItem item:items) {
				if(item.isStatic)
					continue;
				RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.inbox_row, null);
				// Find child views of row
				LKTextView titleTextView = (LKTextView) root.findViewById(R.id.inbox_message_title);
				LKTextView messagePreviewTextView = (LKTextView) root.findViewById(R.id.inbox_message_preview);
				LKTextView dateTextView = (LKTextView) root.findViewById(R.id.inbox_message_date);
				ImageView thumbnailImageView = (ImageView) root.findViewById(R.id.inbox_message_thumbnail);
				
				titleTextView.setText(item.title);
				titleTextView.setTextColor(context[0].getResources().getColor((R.color.base_pink)));
				//TODO: Set bold text if item.unread == true
				if(item.unread) {
					Typeface tf = Typeface.createFromAsset(context[0].getAssets(), "fonts/Roboto-Bold.ttf");
					titleTextView.setTypeface(tf);
				}
				
				int screenWidth = context[0].getResources().getDisplayMetrics().widthPixels;
				int widthOfView = (int) (screenWidth * 0.5);
				Paint paint = messagePreviewTextView.getPaint();
				String message = item.message;
				float[] widths = new float[message.length()];
				paint.getTextWidths(message, widths);
				int counter = 0;
				int currentWidth = 0;
				Log.d("RenderingTask","widthOfView = "+widthOfView);
				boolean dots = true;
				while(currentWidth < (widthOfView) && widths.length > 0) {
					currentWidth += widths[counter];
					counter++;
					if(counter >= message.length()){
						dots = false;
						break;
					}
				}
				String previewMessage = message.substring(0, counter)+((dots) ? "..." : "");
				messagePreviewTextView.setText(previewMessage);
				messagePreviewTextView.setTextColor(context[0].getResources().getColor((R.color.peach)));
				
				dateTextView.setText(item.date);
				dateTextView.setTextColor(context[0].getResources().getColor((R.color.peach)));
				thumbnailImageView.setImageBitmap(item.image);
				
				item.layout = root;
			}
			
			//Add empty view to end of list as bottom margin. Only half of top margin since every row has a built in bottom margin of horizontal_margin_half.
			lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, context[0].getResources().getDimensionPixelSize(R.dimen.horizontal_margin_half));
			r = new RelativeLayout(context[0]);
			r.setLayoutParams(lp);
			l = new LKMenuListItem("","","",false,null);
			l.layout = r;
			items.add(l);
			
			Log.d("RenderingTask", "Completed doInBackground()");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			Log.d("RenderingTask", "fragment was "+((fragment==null) ? "null" : "not null"));
			Log.d("RenderingTask", "items was "+((items==null) ? "null" : "not null"));
			Log.d("RenderingTask", "activity was "+((getActivity()==null) ? "null" : "not null"));
			LKInboxArrayAdapter adapt = new LKInboxArrayAdapter(getActivity(), items, fragment);
			try {
				adapt.setFragmentManager(fragmentManager);
			} catch(NullPointerException e) {
				Log.wtf("RenderingTask",e.toString());
			}
			listView.setOnItemClickListener(adapt);
			progressCircle.setVisibility(View.GONE);
			listView.setAdapter(adapt);
		}

	}
}
