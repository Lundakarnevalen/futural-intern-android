package fragments;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class InboxFragment extends LKFragment {

	ListView listView;
	Context context;
	ProgressBar progressCircle;
	LKFragment fragment;
	LKSQLiteDB db;
	List<LKMenuListItem> data;
	boolean inboxEmpty = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getContext();
		Log.d("InboxFragment", "Got context");

		db = new LKSQLiteDB(context);
		data = db.getMessages();
		db.close();

		if (data.size() <= 0) {
			Log.d("InboxFragment",
					"No messages. Loading no message fragment...");
			View v = inflater.inflate(R.layout.inbox_empty_layout, null);
			inboxEmpty = true;
			return v;
		} else {
			Log.d("InboxFragment", "Messages. Loading message fragment...");
			RelativeLayout root = (RelativeLayout) inflater.inflate(
					R.layout.inbox_layout, null);
			this.fragment = this;
			listView = (ListView) root.findViewById(R.id.inbox_list_view);
			progressCircle = (ProgressBar) root
					.findViewById(R.id.inbox_progress_circle);
			return root;
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("InboxFragment", "Running on activity craeted");
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.inbox_title));

		Log.d("InboxFragment", "got fragmentManager");
		if (!inboxEmpty) {
			RenderingTask rt = new RenderingTask();
			Log.d("InboxFragment", "Executing RenderingTask");
			rt.execute(context);
		}
		// Code to add dummy data into database.

//		LKSQLiteDB dbDummy = new LKSQLiteDB(context);
//		int tmp = dbDummy.heighestMessageId();
//		dbDummy.addItem(new LKMenuListItem("Title", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.", "2015-15-34",1, tmp+1, true));
//		dbDummy.addItem(new LKMenuListItem("Fiskar",  "Dansa kan man göra om man vill!", "2015-15-34", 5, tmp+1, true));
//		dbDummy.addItem(new LKMenuListItem("Flapp",  "Kommunicera bör man annars dör man...", "2321-19-74", 10 , tmp+1, true));

		// LKSQLiteDB dbDummy = new LKSQLiteDB(context);
		// int tmp = dbDummy.heighestMessageId();
		// dbDummy.addItem(new LKMenuListItem("Title",
		// "This is a short message.", "2015-15-34", tmp+1, true, null));


		// LKSQLiteDB dbDummy = new LKSQLiteDB(context);
		// dbDummy.addItem(new LKMenuListItem("Title",
		// "Leet (or '1337'), also known as eleet or leetspeak, is an alternative alphabet for the English language that is used primarily on the Internet. It uses various combinations of ASCII characters to replace Latinate letters. For example, leet spellings of the word leet include 1337 and l33t; eleet may be spelled 31337 or 3l33t. The term leet is derived from the word elite. The leet alphabet is a specialized form of symbolic writing. Leet may also be considered a substitution cipher, although many dialects or linguistic varieties exist in different online communities. The term leet is also used as an adjective to describe formidable prowess or accomplishment, especially in the fields of online gaming and in its original usage – computer hacking.",
		// "2015-15-34", true, null));
		Log.d("InboxFragment", "Completed on activity craeted");
	}

	/**
	 * Add message from db.
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param date
	 */

	public static float addMessage(Context context, String title, String message, String date, int recipients, int id){
		LKSQLiteDB db = new LKSQLiteDB(context);
		Log.d("InboxFragment", "InboxFragment.addMessage.id = "+id);
		float data = db.addItem(new LKMenuListItem(title, message, date, recipients, id, true)); 
		db.close();
		return data;
	}

	public class RenderingTask extends AsyncTask<Context, Void, Void> {
		ArrayList<LKMenuListItem> items = new ArrayList<LKMenuListItem>();

		protected void onPreExecute(Context... context) {
			progressCircle.setVisibility(View.VISIBLE);
			Log.d("RenderingTask", "Completed onPreExecute()");
		}

		@Override
		protected Void doInBackground(Context... context) {
			Log.d("Inbox", "Rendering task started");

			// Get inflater
			LayoutInflater inflater = (LayoutInflater) context[0]
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Add empty view to beginning of list as top margin
			ListView.LayoutParams lp = new ListView.LayoutParams(
					ListView.LayoutParams.MATCH_PARENT, context[0]
							.getResources().getDimensionPixelSize(
									R.dimen.horizontal_margin));
			RelativeLayout r = new RelativeLayout(context[0]);
			r.setLayoutParams(lp);

			LKMenuListItem l = new LKMenuListItem("","","",Integer.MIN_VALUE, 0, false);

			l.layout = r;
			l.isStatic = true;
			items.add(l);

			for (LKMenuListItem message : data) {
				items.add(message);
				Log.d("InboxFragment", "fetched item with id = " + message.id);
			}

			// Populate rowList with data from items
			for (LKInboxArrayAdapter.LKMenuListItem item : items) {
				if (item.isStatic)
					continue;
				RelativeLayout root = (RelativeLayout) inflater.inflate(
						R.layout.inbox_row, null);
				// Find child views of row

				LKTextViewBold titleTextView = (LKTextViewBold) root.findViewById(R.id.inbox_message_title);
				LKTextView messagePreviewTextView = (LKTextView) root.findViewById(R.id.inbox_message_preview);
				LKTextView dateTextView = (LKTextView) root.findViewById(R.id.inbox_message_date);
				LKTextViewBold sectionTextView = (LKTextViewBold) root.findViewById(R.id.inbox_message_sektion_title);
				ImageView thumbnailImageView = (ImageView) root.findViewById(R.id.inbox_message_thumbnail);
				
				titleTextView.setText(item.title);
				titleTextView.setTextColor(context[0].getResources().getColor((R.color.base_pink)));
				
				//TODO: Set bold text if item.unread == true
//				if(item.unread) {
//					Typeface tf = Typeface.createFromAsset(context[0].getAssets(), "fonts/Roboto-Bold.ttf");
//					titleTextView.setTypeface(tf);
//				}

				titleTextView.setText(item.title.toUpperCase());

				titleTextView.setTextColor(context[0].getResources().getColor(
						(R.color.red)));

				// TODO: Set bold text if item.unread == false
				if (!item.unread) {
					Log.d("Inbox", item.unread+"");
					Log.d("Inbox", item.title);
					setFinish(root.findViewById(R.id.inbox_background),
							((LKTextViewBold) root
									.findViewById(R.id.inbox_message_title)),
							((LKTextViewBold) root
									.findViewById(R.id.inbox_message_sektion_title)),
							((LKTextView) root
									.findViewById(R.id.inbox_message_preview)),
							((LKTextView) root
									.findViewById(R.id.inbox_message_date)),
							((ImageView) root.findViewById(R.id.red)));
				}

				int screenWidth = context[0].getResources().getDisplayMetrics().widthPixels;
				int widthOfView = (int) (screenWidth * 0.5);
				Paint paint = messagePreviewTextView.getPaint();
				String message = item.message;
				float[] widths = new float[message.length()];
				paint.getTextWidths(message, widths);
				int counter = 0;
				int currentWidth = 0;
				Log.d("RenderingTask", "widthOfView = " + widthOfView);
				boolean dots = true;
				while (currentWidth < (widthOfView) && widths.length > 0) {
					currentWidth += widths[counter];
					counter++;
					if (counter >= message.length()) {
						dots = false;
						break;
					}
				}
				String previewMessage = message.substring(0, counter)
						+ ((dots) ? "..." : "");
				messagePreviewTextView.setText(previewMessage);
			

				dateTextView.setText(item.date);
				dateTextView.setTextColor(context[0].getResources().getColor((R.color.peach)));
				
				// Set image and section text depending on item.recipients
				switch (item.recipients) {
				case 0:
					thumbnailImageView.setImageResource(R.drawable.icon);
					sectionTextView.setText("Karnevalen");
					break;
				case 1:
					thumbnailImageView.setImageResource(R.drawable.barnevalen);
					sectionTextView.setText(R.string.sektion_1);
					break;
				case 2:
					thumbnailImageView.setImageResource(R.drawable.biljetteriet);
					sectionTextView.setText(R.string.sektion_2);
					break;
				case 3:
					thumbnailImageView.setImageResource(R.drawable.bladderiet);
					sectionTextView.setText(R.string.sektion_3);
					break;
				case 4:
					thumbnailImageView.setImageResource(R.drawable.cirkusen);
					sectionTextView.setText(R.string.sektion_4);
					break;
				case 5:
					thumbnailImageView.setImageResource(R.drawable.dansen);
					sectionTextView.setText(R.string.sektion_5);
					break;
				case 6:
					thumbnailImageView.setImageResource(R.drawable.ekonomi);
					sectionTextView.setText(R.string.sektion_6);
					break;
				case 7:
					thumbnailImageView.setImageResource(R.drawable.kabaren);
					sectionTextView.setText(R.string.sektion_7);
					break;
				case 8:
					thumbnailImageView.setImageResource(R.drawable.fabriken);
					sectionTextView.setText(R.string.sektion_8);
					break;
				case 9:
					thumbnailImageView.setImageResource(R.drawable.klipperiet);
					sectionTextView.setText(R.string.sektion_9);
					break;
				case 10:
					thumbnailImageView.setImageResource(R.drawable.kommunikation);
					sectionTextView.setText(R.string.sektion_10);
					break;
				case 11:
					thumbnailImageView.setImageResource(R.drawable.krog);
					sectionTextView.setText(R.string.sektion_11);
					break;
				case 12:
					thumbnailImageView.setImageResource(R.drawable.krog);
					sectionTextView.setText(R.string.sektion_12);
					break;
				case 13:
					thumbnailImageView.setImageResource(R.drawable.krog);
					sectionTextView.setText(R.string.sektion_13);
					break;
				case 14:
					thumbnailImageView.setImageResource(R.drawable.krog);
					sectionTextView.setText(R.string.sektion_14);
					break;
				case 15:
					thumbnailImageView.setImageResource(R.drawable.krog);
					sectionTextView.setText(R.string.sektion_15);
					break;
				case 16:
					thumbnailImageView.setImageResource(R.drawable.omrade);
					sectionTextView.setText(R.string.sektion_16);
					break;
				case 17:
					thumbnailImageView.setImageResource(R.drawable.musiken);
					sectionTextView.setText(R.string.sektion_17);
					break;
				case 18:
					thumbnailImageView.setImageResource(R.drawable.radio);
					sectionTextView.setText(R.string.sektion_18);
					break;
				case 19:
					thumbnailImageView.setImageResource(R.drawable.revyn);
					sectionTextView.setText(R.string.sektion_19);
					break;
				case 20:
					thumbnailImageView.setImageResource(R.drawable.shoppen);
					sectionTextView.setText(R.string.sektion_20);
					break;
				case 21:
					thumbnailImageView.setImageResource(R.drawable.show);
					sectionTextView.setText(R.string.sektion_21);
					break;
				case 22:
					thumbnailImageView.setImageResource(R.drawable.snaxeriet);
					sectionTextView.setText(R.string.sektion_22);
					break;
				case 23:
					thumbnailImageView.setImageResource(R.drawable.spexet);
					sectionTextView.setText(R.string.sektion_23);
					break;
				case 24:
					thumbnailImageView.setImageResource(R.drawable.sakerhet);
					sectionTextView.setText(R.string.sektion_24);
					break;
				case 25:
					thumbnailImageView.setImageResource(R.drawable.tombolan);
					sectionTextView.setText(R.string.sektion_25);
					break;
				case 26:
					thumbnailImageView.setImageResource(R.drawable.vieriet);
					sectionTextView.setText(R.string.sektion_26);
					break;
				case 100:
					thumbnailImageView.setImageResource(R.drawable.festmasteriet);
					sectionTextView.setText(R.string.sektion_100);
					break;
				case 101:
					thumbnailImageView.setImageResource(R.drawable.festmasteriet);
					sectionTextView.setText(R.string.sektion_101);
					break;
				case 102:
					thumbnailImageView.setImageResource(R.drawable.festmasteriet);
					sectionTextView.setText(R.string.sektion_102);
					break;
				case 199:
					thumbnailImageView.setImageResource(R.drawable.festmasteriet);
					sectionTextView.setText(R.string.sektion_199);
					break;
				case 202:
					thumbnailImageView.setImageResource(R.drawable.smanojen);
					sectionTextView.setText(R.string.sektion_202);
					break;
				case 203:
					thumbnailImageView.setImageResource(R.drawable.smanojen);
					sectionTextView.setText(R.string.sektion_203);
					break;
				case 204:
					thumbnailImageView.setImageResource(R.drawable.smanojen);
					sectionTextView.setText(R.string.sektion_204);
					break;
				case 300:
					thumbnailImageView.setImageResource(R.drawable.tag);
					sectionTextView.setText(R.string.sektion_300);
					break;
				case 399:
					thumbnailImageView.setImageResource(R.drawable.tag);
					sectionTextView.setText(R.string.sektion_399);
					break;
				case 400:
					thumbnailImageView.setImageResource(R.drawable.nojen);
					sectionTextView.setText(R.string.sektion_400);
					break;
				case 499:
					thumbnailImageView.setImageResource(R.drawable.nojen);
					sectionTextView.setText(R.string.sektion_499);
					break;
				}

				item.layout = root;
			}

			// Add empty view to end of list as bottom margin. Only half of top
			// margin since every row has a built in bottom margin of
			// horizontal_margin_half.
			lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
					context[0].getResources().getDimensionPixelSize(
							R.dimen.horizontal_margin_half));
			r = new RelativeLayout(context[0]);
			r.setLayoutParams(lp);
			l = new LKMenuListItem("","","",Integer.MIN_VALUE, 0, false);
			l.layout = r;
			items.add(l);

			Log.d("RenderingTask", "Completed doInBackground()");
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			Log.d("RenderingTask", "fragment was "
					+ ((fragment == null) ? "null" : "not null"));
			Log.d("RenderingTask", "items was "
					+ ((items == null) ? "null" : "not null"));
			Log.d("RenderingTask", "activity was "
					+ ((context == null) ? "null" : "not null"));
			LKInboxArrayAdapter adapt = new LKInboxArrayAdapter(context, items,
					fragment);
			try {
				adapt.setFragmentManager(getActivity()
						.getSupportFragmentManager());
			} catch (NullPointerException e) {
				Log.wtf("RenderingTask", e.toString());
			}
			listView.setOnItemClickListener(adapt);
			progressCircle.setVisibility(View.GONE);
			listView.setAdapter(adapt);

			Log.d("InboxFragment", "Completed RenderingTask");
		}

		private void setFinish(View v, LKTextViewBold tv1, LKTextViewBold tv2,
				LKTextView tv3, LKTextView tv4, ImageView red) {
			// TODO Auto-generated method stub
			tv1.setTextColor(getResources().getColor(R.color.shadow_red));
			tv2.setTextColor(getResources().getColor(R.color.shadow_red));
			tv3.setTextColor(getResources().getColor(R.color.shadow_red));
			tv4.setTextColor(getResources().getColor(R.color.shadow_red));

			v.setBackgroundResource(R.drawable.beige_bg_bottom_shadow);
			red.setVisibility(View.VISIBLE);

		}

	}
}
