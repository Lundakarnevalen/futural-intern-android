package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


	public class IdentificationFragment extends LKFragment{
		
			
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View root = inflater.inflate(R.layout.identification_layout, null);
			
			return root;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setTitle(getString(R.string.karta));
		}
	}
