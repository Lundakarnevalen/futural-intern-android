package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKButtonGreen;
import se.lundakarnevalen.widget.LKTextView;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class InfoText2Fragment extends LKFragment {

	public InfoText2Fragment() { 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.info_text_actionbar));
		// TODO
		// Fix both eng and swe.
	} 

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.info_text2, null);

		LKTextView text8 = (LKTextView) root.findViewById(R.id.text_8);

		text8.setText(Html.fromHtml(getString(R.string.text8)));

		LKButtonGreen next = (LKButtonGreen) root.findViewById(R.id.next);
		next.setOnClickListener(new ClickListener());

		return root;
	}

	public class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			android.support.v4.app.FragmentManager fm=getFragmentManager();
			FragmentTransaction ft=fm.beginTransaction();
			ft.addToBackStack(null);
			fm.popBackStack();
			fm.popBackStack();
			
		}
	}

}
