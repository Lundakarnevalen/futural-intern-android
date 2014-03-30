package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class LKButtonGreen extends Button{
	Context context;
	public LKButtonGreen(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKButtonGreen(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public LKButtonGreen(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}
	
	private void init(){
		setBackgroundResource(R.drawable.button_selector_green);
		int pxPadding = context.getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		setTypeface(tf);
		setTextColor(Color.WHITE);
	}
}
