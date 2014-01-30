
package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SectionsInformationFragment extends LKFragment {

	private String title;
	private int pictureResourceId;
	private String information;

	public SectionsInformationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.sections_information_layout, null);
		Button registerButton = (Button) root
				.findViewById(R.id.section_button);
		registerButton.setOnClickListener(new ClickListener());

		Bundle bundle = getArguments();
		title = bundle.getString("title");
		pictureResourceId = bundle.getInt("resourceId");
		information = bundle.getString("information");

		TextView titleTextView = (TextView) root
				.findViewById(R.id.Section_name);
		ImageView icon = (ImageView) root.findViewById(R.id.Section_icon);
		LKTextView informationTextView = (LKTextView) root
				.findViewById(R.id.section_information);

		titleTextView.setText(title.toUpperCase());
		icon.setImageResource(pictureResourceId);
		informationTextView.setText(Html.fromHtml(information));
		
		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		if(appIsLocked(user)){
			registerButton.setOnClickListener(null);
			registerButton.setVisibility(View.GONE);
		}
		
		return root;
	}
	
	public class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			LKFragment fragment = new RegistrationFragment();
			loadFragment(fragment, true);
		}
	}
}
