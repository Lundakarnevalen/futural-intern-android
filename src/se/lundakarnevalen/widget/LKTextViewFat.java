package se.lundakarnevalen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom textview with custom font. 
 * @author alexander najafi
 *
 */
public class LKTextViewFat extends TextView{
	Context context;
	
	public LKTextViewFat(Context context) {
		super(context);
		this.context = context;
		font();
		
	}
	
	public LKTextViewFat(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		font();
	}
	
	public LKTextViewFat(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		this.context = context;
		font();
	}
	
	private void font(){
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Robot!Head.ttf");
		setTypeface(tf);
	}
}
