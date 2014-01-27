package se.lundakarnevalen.android;

import java.util.ArrayList;
import java.util.Random;

import se.lundakarnevalen.remote.SectionSQLiteDB;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SectionsListFragment extends LKFragment {

	private SectionSQLiteDB db;
	private LKSectionsArrayAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (View) inflater
				.inflate(R.layout.sections_list_layout, null);
		RelativeLayout right = (RelativeLayout) root
				.findViewById(R.id.left_tab);
		right.setOnClickListener(new ClickListener());

		ListView listView = (ListView) root.findViewById(R.id.list_section);

		db = new SectionSQLiteDB(getActivity());
		
		ArrayList<LKSectionsArrayAdapter.LKSectionsItem> list = new ArrayList<LKSectionsArrayAdapter.LKSectionsItem>();

		list.addAll(db.getSections());

		adapter = new LKSectionsArrayAdapter(getActivity(), list);

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