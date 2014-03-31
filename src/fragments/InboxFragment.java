package fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.android.R.color;
import se.lundakarnevalen.android.R.dimen;
import se.lundakarnevalen.android.R.id;
import se.lundakarnevalen.android.R.layout;
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
	LKSQLiteDB db;
	List<LKMenuListItem> data;
	boolean inboxEmpty = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		context = getContext();
		Log.d("InboxFragment", "Got context");
		
		db = new LKSQLiteDB(context);
		data = db.getMessages();
		db.close();
		
		if(data.size() <= 0) {
			Log.d("InboxFragment", "No messages. Loading no message fragment...");
			View v = inflater.inflate(R.layout.inbox_empty_layout, null);
			inboxEmpty = true;
			return v;
		} else {
			Log.d("InboxFragment", "Messages. Loading message fragment...");
			RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.inbox_layout, null);
			this.fragment = this;
			listView = (ListView) root.findViewById(R.id.inbox_list_view);
			progressCircle = (ProgressBar) root.findViewById(R.id.inbox_progress_circle);
			return root;
		}
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("InboxFragment", "Running on activity craeted");
		super.onActivityCreated(savedInstanceState);
		setTitle("Inbox");
		
		Log.d("InboxFragment", "got fragmentManager");
		if(!inboxEmpty) {
			RenderingTask rt = new RenderingTask();
			Log.d("InboxFragment", "Executing RenderingTask");
			rt.execute(context);
		}
		// Code to add dummy data into database.
		//LKSQLiteDB dbDummy = new LKSQLiteDB(context);
		//dbDummy.addItem(new LKMenuListItem("Title", "This is a short message.", "2015-15-34", true, null));

		//LKSQLiteDB dbDummy = new LKSQLiteDB(context);
		//dbDummy.addItem(new LKMenuListItem("Title", "Leet (or '1337'), also known as eleet or leetspeak, is an alternative alphabet for the English language that is used primarily on the Internet. It uses various combinations of ASCII characters to replace Latinate letters. For example, leet spellings of the word leet include 1337 and l33t; eleet may be spelled 31337 or 3l33t. The term leet is derived from the word elite. The leet alphabet is a specialized form of symbolic writing. Leet may also be considered a substitution cipher, although many dialects or linguistic varieties exist in different online communities. The term leet is also used as an adjective to describe formidable prowess or accomplishment, especially in the fields of online gaming and in its original usage – computer hacking.", "2015-15-34", true, null));
		Log.d("InboxFragment", "Completed on activity craeted");
	}		
	
	/**
	 * Add message from db.
	 * @param context
	 * @param title
	 * @param message
	 * @param date
	 */
	public static float addMessage(Context context, String title, String message, String date, int id){
		LKSQLiteDB db = new LKSQLiteDB(context);
		Log.d("InboxFragment", "InboxFragment.addMessage.id = "+id);
		float data = db.addItem(new LKMenuListItem(title, message, date, id, true, null)); // Null är bitmappen.
		db.close();
		return data;
	}
	
	public class RenderingTask extends AsyncTask<Context,Void,Void> {
		ArrayList<LKMenuListItem> items = new ArrayList<LKMenuListItem>();
		
		protected void onPreExecute(Context... context) {
			progressCircle.setVisibility(View.VISIBLE);
			Log.d("RenderingTask", "Completed onPreExecute()");
		}
		
		@Override
		protected Void doInBackground(Context... context) {

			//Get inflater
			LayoutInflater inflater = (LayoutInflater) context[0].getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			//Add empty view to beginning of list as top margin
			ListView.LayoutParams lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, context[0].getResources().getDimensionPixelSize(R.dimen.horizontal_margin));
			RelativeLayout r = new RelativeLayout(context[0]);
			r.setLayoutParams(lp);
			LKMenuListItem l = new LKMenuListItem("","","", 0, false,null);
			l.layout = r;
			l.isStatic = true;
			items.add(l);
			
			for(LKMenuListItem message : data){
				items.add(message);
				Log.d("InboxFragment", "fetched item with id = "+message.id);
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
			l = new LKMenuListItem("","","", 0, false,null);
			l.layout = r;
			items.add(l);
			
			Log.d("RenderingTask", "Completed doInBackground()");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			Log.d("RenderingTask", "fragment was "+((fragment==null) ? "null" : "not null"));
			Log.d("RenderingTask", "items was "+((items==null) ? "null" : "not null"));
			Log.d("RenderingTask", "activity was "+((context==null) ? "null" : "not null"));
			LKInboxArrayAdapter adapt = new LKInboxArrayAdapter(context, items, fragment);
			try {
				adapt.setFragmentManager(getActivity().getSupportFragmentManager());
			} catch(NullPointerException e) {
				Log.wtf("RenderingTask",e.toString());
			}
			listView.setOnItemClickListener(adapt);
			progressCircle.setVisibility(View.GONE);
			listView.setAdapter(adapt);
			
			Log.d("InboxFragment", "Completed RenderingTask");
		}
		
		

	}
}
