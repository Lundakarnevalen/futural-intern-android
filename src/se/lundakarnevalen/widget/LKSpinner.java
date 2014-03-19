package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Spinner;

public class LKSpinner extends Spinner{
	
	public LKSpinner(Context context){
		super(context);
		init();
	}
	
	public LKSpinner(Context context, AttributeSet attr){
		super(context, attr);
		init();
	}
	
	public LKSpinner(Context context, AttributeSet attr, int defstyle){
		super(context, attr, defstyle);
		init();
	}
	
	private void init(){
		this.setBackgroundResource(R.drawable.spinner_bg_arrow);
		this.setPadding(30, 30, 30, 30);
	}
}