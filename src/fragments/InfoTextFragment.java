package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import se.lundakarnevalen.widget.LKTextViewFat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class InfoTextFragment extends LKFragment {
	MapFragment mf;
	
	public InfoTextFragment() {
		
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.info_text_actionbar));
		//TODO
		// Fix both eng and swe.
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.info_text, null);

		LKTextViewFat text1 = (LKTextViewFat) root.findViewById(R.id.text_1);
		LKTextViewBold text2 = (LKTextViewBold) root.findViewById(R.id.text_2);
		LKTextView text3 = (LKTextView) root.findViewById(R.id.text_3);
		LKTextViewFat text4 = (LKTextViewFat) root.findViewById(R.id.text_4);
		LKTextView text5 = (LKTextView) root.findViewById(R.id.text_5);
		LKTextViewBold text6 = (LKTextViewBold) root.findViewById(R.id.text_6);
		LKTextView text7 = (LKTextView) root.findViewById(R.id.text_7);
		LKTextView text8 = (LKTextView) root.findViewById(R.id.text_8);

		text1.setText(Html.fromHtml(getString(R.string.text1)));
		text2.setText(Html.fromHtml(getString(R.string.text2)));
		text3.setText(Html.fromHtml(getString(R.string.text3)));
		text4.setText(Html.fromHtml(getString(R.string.text4)));
		text5.setText(Html.fromHtml(getString(R.string.text5)));
		text6.setText(Html.fromHtml(getString(R.string.text6)));
		text7.setText(Html.fromHtml(getString(R.string.text7)));
		text8.setText(Html.fromHtml(getString(R.string.text8)));
		
		System.out.println(getString(R.string.text1)); 

		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		View v = actionBar.getCustomView();
		
		RelativeLayout backToMap = (RelativeLayout) v.findViewById(R.id.back_to_map);	
		backToMap.setOnClickListener(backListener);
		
		return root;
	}
	
	private View.OnClickListener backListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mf == null) {
				loadFragment(new MapFragment(), false);
					
			} else {
				loadFragment(mf, false);
			}
			//TODO start fragment
		}
	};

	public void setMapFragment(MapFragment mapFragment) {
		// TODO Auto-generated method stub
		mf = mapFragment;
	}

	
}
