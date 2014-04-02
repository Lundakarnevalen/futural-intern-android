package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class LKButtonYellow extends LKButton {
	private Context context;

	public LKButtonYellow(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public LKButtonYellow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LKButtonYellow(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}

	private void init() {
		final int pxPadding = context.getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		setBackgroundResource(R.drawable.button_selector_yellow);
		setTextColor(Color.WHITE);
	}
}
