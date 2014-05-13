package se.lundakarnevalen.widget;

import java.util.List;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LKSpinnerArrayAdapter extends ArrayAdapter<LKSpinnerArrayAdapter.LKSpinnerArrayItem>{
	Context context;
	
	
	
	public LKSpinnerArrayAdapter(Context context, List<LKSpinnerArrayAdapter.LKSpinnerArrayItem> data) {
		super(context, R.layout.sections_row, data);
		this.context = context;
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	
	public View getView(int pos, View convertView, ViewGroup paren){
		TextView tv = new TextView(context);
		LKSpinnerArrayItem item = getItem(pos);
		tv.setText(item.text);
		tv.setTextColor(Color.WHITE);
		return tv;
	}
	
	public static class LKSpinnerArrayItem{
		public String text;
		public int value;
		
		public LKSpinnerArrayItem(String text, int value){
			this.text = text;
			this.value = value;
		}
		
		public String toString(){
			return text;
		}
	}
}
