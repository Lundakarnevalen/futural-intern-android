package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class LKButtonBlue extends LKButton {
	private Context context;
	
	public LKButtonBlue(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKButtonBlue(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public LKButtonBlue(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}
	
	private void init(){
		setBackgroundResource(R.drawable.button_selector_blue);
		int pxPadding = context.getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		setTextColor(Color.WHITE);
	}
}
