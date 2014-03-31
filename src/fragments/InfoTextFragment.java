package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import se.lundakarnevalen.widget.LKTextViewFat;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoTextFragment extends LKFragment {

	public InfoTextFragment() {
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

		return root;
	}

}
