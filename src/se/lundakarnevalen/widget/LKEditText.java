package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class LKEditText extends EditText{
	Context context;
	public LKEditText(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKEditText(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public LKEditText(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}
	
	private void init(){
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.edittext_background));
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		setTypeface(tf);
		setTextColor(Color.WHITE);
		int pxPadding = context.getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
		setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
		setHintTextColor(context.getResources().getColor(R.color.hint_peach));
	}
}
