package se.lundakarnevalen.widget;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class LKRadioButton extends RadioButton{
	Context context;
	
	public LKRadioButton(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public LKRadioButton(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public LKRadioButton(Context context, AttributeSet attrs, int defstyle){
		super(context, attrs, defstyle);
		this.context = context;
		init();
	}
	
	private void init(){
		this.setButtonDrawable(R.drawable.radio_button_style);
	}
}
