package se.lundakarnevalen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class LKButton extends Button {

	public LKButton(Context context) {
		super(context);
	}

	public LKButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LKButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTypeface(Typeface tf, int style) {
		if (style == Typeface.BOLD) {
			super.setTypeface(Typeface.createFromAsset(
					getContext().getAssets(), "fonts/Roboto-Bold.ttf"));
		} else if (style == Typeface.ITALIC) {
			super.setTypeface(Typeface.createFromAsset(
					getContext().getAssets(), "fonts/Roboto-Thin.ttf"));
		} else {
			super.setTypeface(Typeface.createFromAsset(
					getContext().getAssets(), "fonts/Roboto-Light.ttf"));
		}
	}

}