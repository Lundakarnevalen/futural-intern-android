package se.lundakarnevalen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom textview with custom font. 
 * @author alexander najafi
 *
 */
public class LKTextView extends TextView{

	public LKTextView(Context context) {
		super(context);
	}
	
	public LKTextView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public LKTextView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

}
