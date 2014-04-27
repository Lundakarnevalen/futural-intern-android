package fragments;

import se.lundakarnevalen.android.ContentActivity;
import se.lundakarnevalen.android.IDActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;


	public class IdentificationFragment extends LKFragment{
		
			
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View root = inflater.inflate(R.layout.identification_layout, null);
			//getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			//getActivity().getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
			Intent i = new Intent(getActivity(), IDActivity.class);
            startActivity(i);
            
			//getActivity().getActionBar().hide();
			
			return root;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setTitle(getString(R.string.karta));
		}
		
		
	}
