package se.lundakarnevalen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom textview with custom font. 
 *
 */
public class LKRobotTextView extends TextView {

    public LKRobotTextView(Context context) {
        super(context);
    }

    public LKRobotTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LKRobotTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if(isInEditMode()) return;
        super.setTypeface(Typeface.createFromAsset(super.getContext().getAssets(), "fonts/Robot!Head.ttf"));


    }
}