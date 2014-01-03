package se.lundakarnevalen.widget;

import java.util.ArrayList;

import se.lundakarnevalen.android.ContentActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.android.SectionsInformationFragment;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class LKSectionsArrayAdapter extends
		ArrayAdapter<LKSectionsArrayAdapter.LKSectionsItem> implements
		OnItemClickListener {
	private Context context;
	private ArrayList<LKSectionsItem> list;

	public LKSectionsArrayAdapter(Context context,
			ArrayList<LKSectionsItem> list) {
		super(context, R.layout.sections_row, list);
		this.context = context;
		this.list = list;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		// if (row == null) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(R.layout.sections_row, parent, false);

		ImageView icon = (ImageView) row.findViewById(R.id.image);
		ImageView forward = (ImageView) row.findViewById(R.id.forward);
		LKTextViewBold title = (LKTextViewBold) row.findViewById(R.id.title);

		// row.setTag(item);
		// } else {
		// item = (LKSectionsItem) row.getTag();
		// }

		LKSectionsItem item = list.get(position);

		icon.setImageResource(item.icon);
		// forward.setImageBitmap(R.drawable.)
		title.setText(item.title);

		return row;
	}

	public static class LKSectionsItem {
		public String title;
		public int icon;
		public String slogan;
		public String information;
		public boolean like;

		public LKSectionsItem(String title, int icon2, String slogan,
				String information, boolean like) {
			this.title = title;
			this.icon = icon2;
			this.slogan = slogan;
			this.information = information;
			this.like = like;
		}

		public String toString() {
			return "The sections name: " + title;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		LKSectionsItem item = getItem(pos);
		SectionsInformationFragment fragment = new SectionsInformationFragment();
		Bundle bundle = new Bundle();

		bundle.putString("title", item.title);
		bundle.putInt("resourceId", item.icon);
		bundle.putString("information", item.information);
		bundle.putString("slogan", item.slogan);

		fragment.setArguments(bundle);

		ContentActivity a = (ContentActivity) context;
		a.getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();

	}

}
