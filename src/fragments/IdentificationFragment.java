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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
			LKUser user = new LKUser(getContext());
			user.getUserLocaly();
			Intent i = new Intent(getActivity(), IDActivity.class);
            i.putExtra("name", user.fornamn+" "+user.efternamn);
            i.putExtra("pnbr", user.personnummer);
            i.putExtra("sektion", ""+user.tilldelad_sektion);
            i.putExtra("photo", user.imgUrl);
            Log.d("sektion",""+user.tilldelad_sektion);
            /*
            LKRemote remote = new LKRemote(getActivity());
    		remote.setBitmapResultListener(new BitmapResultListener() {
    			@Override
    			public void onResult(Bitmap result) {
    				Log.d(LOG_TAG, "Got bitmap!");
    				if (result == null) {
    					Log.e(LOG_TAG, "bitmap was null"); // Maybe set some
    														// standard image?
    				}

    				image.setVisibility(View.VISIBLE);
    				image.setImageBitmap(result);
    			}
    		});
    		remote.requestServerForBitmap(user.imgUrl);

            */
            
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
