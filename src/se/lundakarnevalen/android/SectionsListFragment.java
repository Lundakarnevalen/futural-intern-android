package se.lundakarnevalen.android;

import java.util.ArrayList;

import se.lundakarnevalen.widget.LKSectionsArrayAdapter;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SectionsListFragment extends LKFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (View) inflater
				.inflate(R.layout.sections_list_layout, null);

		ArrayList<LKSectionsArrayAdapter.LKSectionsItem> list = new ArrayList<LKSectionsArrayAdapter.LKSectionsItem>();

		list.add(new LKSectionsItem("Title", R.drawable.rund, "Slogan",
				"information information information information information information information information", true));
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Sektioner");
	}

}
