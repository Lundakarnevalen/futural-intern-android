
package se.lundakarnevalen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LKGantFont extends TextView{
	Context context; 
	public LKGantFont(Context context) {
	  super(context);
	  this.context = context;    
		  }

	    public LKGantFont(Context context, AttributeSet attrs) {
	    	super(context, attrs);
	    	this.context = context;
		    }

	    @Override
	    public void setTypeface(Typeface tf, int style) {
	        if(isInEditMode()) return;
	        super.setTypeface(Typeface.createFromAsset(super.getContext().getAssets(), "fonts/Eurostile-Demi.ttf"));


	    }

	
	
}
