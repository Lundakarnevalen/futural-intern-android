package se.lundakarnevalen.android;

import java.util.ArrayList;

import se.lundakarnevalen.widget.LKSectionsArrayAdapter;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SectionsListFragment extends LKFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (View) inflater
				.inflate(R.layout.sections_list_layout, null);
		RelativeLayout right = (RelativeLayout) root
				.findViewById(R.id.left_tab);
		right.setOnClickListener(new ClickListener());

		TextView titleTextView = (TextView) root
				.findViewById(R.id.all_sections);
		
		
		ArrayList<LKSectionsArrayAdapter.LKSectionsItem> list = new ArrayList<LKSectionsArrayAdapter.LKSectionsItem>();

		list.add(new LKSectionsItem(
				"Barnevalen",
				R.drawable.sections_image,
				"Sektionen fšr de barnsligaste.",
				"information information information information information information information information information information information information information information informationinformation information information information information information informationinformation information",
				true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));
		list.add(new LKSectionsItem("Title 2", R.drawable.rund, "Slogan 2",
				"information 2", true));

		ListView listView = (ListView) root.findViewById(R.id.list_section);

		LKSectionsArrayAdapter adapter = new LKSectionsArrayAdapter(
				getActivity(), list);

		listView.setOnItemClickListener(adapter);

		listView.setAdapter(adapter);

		return root;
	}

	public class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			ContentActivity a = (ContentActivity) getActivity();
			a.getSupportFragmentManager().popBackStack();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Sektioner");
	}

}
