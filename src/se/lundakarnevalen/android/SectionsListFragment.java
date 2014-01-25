package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.Random;

import se.lundakarnevalen.remote.SectionSQLiteDB;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SectionsListFragment extends LKFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (View) inflater
				.inflate(R.layout.sections_list_layout, null);
		RelativeLayout right = (RelativeLayout) root
				.findViewById(R.id.left_tab);
		right.setOnClickListener(new ClickListener());

		ArrayList<LKSectionsArrayAdapter.LKSectionsItem> list = new ArrayList<LKSectionsArrayAdapter.LKSectionsItem>();

		SectionSQLiteDB db = new SectionSQLiteDB(getActivity());
		db.dropEntiresInDatabase();

		db.addItem(new LKSectionsItem(
				"Barnevalen",
				R.drawable.sections_image,
				"Sektionen fšr de barnsligaste.",
				"information information information information information information information information information information information information information information informationinformation information information information information information informationinformation information",
				true));

		db.addItem(new LKSectionsItem(
				"Klipperiet",
				R.drawable.sections_image,
				"Sektionen fšr de barnsligaste.",
				"information information information information information information information information information information information information information information informationinformation information information information information information informationinformation information",
				true));

		db.addItem(new LKSectionsItem(
				"Musik",
				R.drawable.sections_image,
				"Sektionen fšr de barnsligaste.",
				"information information information information information information information information information information information information information information informationinformation information information information information information informationinformation information",
				true));

		db.addItem(new LKSectionsItem(
				"Cirkusen",
				R.drawable.sections_image,
				"Sektionen fšr de barnsligaste.",
				"information information information information information information information information information information information information information information informationinformation information information information information information informationinformation information",
				true));

		list.addAll(db.getSections());

		ListView listView = (ListView) root.findViewById(R.id.list_section);

		LKSectionsArrayAdapter adapter = new LKSectionsArrayAdapter(
				getActivity(), list);

		listView.setOnItemClickListener(adapter);

		listView.setAdapter(adapter);


		// Bundle for slot machine
		Bundle bundle = getArguments();
		if (bundle.getBoolean("random")) {
			Random rand = new Random();
			int position = rand.nextInt(adapter.getCount()); // How many sections there are in the list
		} // End Bundle
		
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
