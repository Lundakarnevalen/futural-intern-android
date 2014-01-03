package se.lundakarnevalen.android;

//import se.lundakarnevalen.android.SectionsListFragment.ClickListener;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SectionsInformationFragment extends LKFragment {

	private String title;
	private int pictureResourceId;
	private String slogan;
	private String information;

	public SectionsInformationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.sections_information_layout, null);
//		RelativeLayout registerButton = (RelativeLayout) root
//				.findViewById(R.id.section_button);
//		registerButton.setOnClickListener(new ClickListener());

		Bundle bundle = getArguments();
		title = bundle.getString("title");
		pictureResourceId = bundle.getInt("resourceId");
		slogan = bundle.getString("slogan");
		information = bundle.getString("information");

		LKTextViewBold titleTextView = (LKTextViewBold) root
				.findViewById(R.id.Section_name);
		ImageView icon = (ImageView) root.findViewById(R.id.Section_icon);
		LKTextViewBold sloganTextView = (LKTextViewBold) root
				.findViewById(R.id.section_slogan);
		LKTextView informationTextView = (LKTextView) root
				.findViewById(R.id.section_information);

		titleTextView.setText(title);
		icon.setImageResource(pictureResourceId);
		sloganTextView.setText(slogan);
		informationTextView.setText(information);
		


		return root;
	}

//	public class ClickListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			ContentActivity a = (ContentActivity) getActivity();
//			a.getSupportFragmentManager().popBackStack();
//		}
//	}
}