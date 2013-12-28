package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class LKButton extends Button{
	Context context;
	public LKButton(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKButton(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public LKButton(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}
	
	private void init(){
		setBackgroundResource(R.drawable.button_bg);
		int pxPadding = context.getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		setTypeface(tf);
		setTextColor(Color.WHITE);
	}
}
