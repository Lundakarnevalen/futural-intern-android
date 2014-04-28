package fragments;

import se.lundakarnevalen.android.ContentActivity;
import se.lundakarnevalen.android.IDActivity;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;


	public class IdentificationFragment extends LKFragment implements OnClickListener{
		Intent i;
		ImageView v;	
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View root = inflater.inflate(R.layout.identification_layout, null);
			LKUser user = new LKUser(getContext());
			user.getUserLocaly();
			i = new Intent(getActivity(), IDActivity.class);
            i.putExtra("name", user.fornamn+" "+user.efternamn);
            i.putExtra("pnbr", user.personnummer);
            i.putExtra("sektion", ""+user.tilldelad_sektion);
            i.putExtra("photo", user.imgUrl);
                        
            v =(ImageView) root.findViewById(R.id.card);
            v.setOnClickListener(this);
			//getActivity().getActionBar().hide();
			
			return root;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setTitle(getString(R.string.id_card));
		}
		
		@Override
		  public void onPause() {
			super.onResume();
		  }
		

		@Override
		  public void onResume() {
			v.clearColorFilter();
			super.onResume();
		  }
		
		
		@Override
		public void onClick(View v) {
			((ImageView) v).setColorFilter(Color.parseColor("#40000000"), Mode.SRC_ATOP);
			new Handler().postDelayed(new Runnable() { 
				
				@Override 
				public void run() {
					// TODO Auto-generated method stub
					startActivity(i);
				}
			},50);
            
		}
		
		
	}
