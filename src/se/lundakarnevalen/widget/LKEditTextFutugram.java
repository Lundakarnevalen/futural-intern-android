package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class LKEditTextFutugram extends EditText {
	Context context;

	public LKEditTextFutugram(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public LKEditTextFutugram(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LKEditTextFutugram(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}

	private void init() {
		setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.edittext_futugram_background));
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Light.ttf");
		setTypeface(tf);
		setTextColor(getResources().getColor(R.color.dark_blue));
		int pxPadding = context.getResources().getDimensionPixelSize(
				R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		setHintTextColor(getResources().getColor(R.color.dark_blue));
	}
}
