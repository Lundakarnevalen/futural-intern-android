package se.lundakarnevalen.widget;

import fragments.LKFragment;
import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class LKProgressBar extends ProgressBar{
	Context context;
	public LKProgressBar(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKProgressBar(Context context, AttributeSet attrs){
		super(context, attrs, android.R.attr.progressBarStyleHorizontal);
		this.context = context;
		init();
	}
	
	public LKProgressBar(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, android.R.attr.progressBarStyleHorizontal);
		this.context = context;
		init();
	}
	
	private void init(){
		setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_bar_style));
		this.setBackgroundResource(R.drawable.progress_bar_bg);
		int padding = (int) LKFragment.dpToPx(5, context);
		setPadding(padding, padding, padding, padding);
	}
}
