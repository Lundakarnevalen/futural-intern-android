package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKTextView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class InfoText2Fragment extends LKFragment {

	public InfoText2Fragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.info_text_actionbar));
		//TODO
		// Fix both eng and swe.
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.info_text2, null);

		
		LKTextView text8 = (LKTextView) root.findViewById(R.id.text_8);

		
		text8.setText(Html.fromHtml(getString(R.string.text8)));
		

		
		return root;
	}
	
	private View.OnClickListener backListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			loadFragment(new MapFragment(), false);
			//TODO start fragment
		}
	};

	
}
