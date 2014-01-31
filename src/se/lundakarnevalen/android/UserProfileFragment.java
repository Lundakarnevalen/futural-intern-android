package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UserProfileFragment extends LKFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.user_profile_layout, null);
		
		final ProgressBar pb = (ProgressBar) root.findViewById(R.id.pb);
		final ImageView picture = (ImageView) root.findViewById(R.id.profile_picture);
		TextView name = (TextView) root.findViewById(R.id.user_info1);
		
		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		
		picture.setImageResource(R.drawable.sections_image);
		name.setText(user.fornamn+" "+user.efternamn); 
		
		LKRemote remote = new LKRemote(getContext());
		remote.setBitmapResultListener(new BitmapResultListener(){
			@Override
			public void onResult(Bitmap result) {
				Log.d(LOG_TAG, "Got bitmap!");
				if(result == null)
					Log.e(LOG_TAG, "bitmap was null"); // Maybe set some standard image?

				pb.setVisibility(View.GONE);
				picture.setVisibility(View.VISIBLE);
				picture.setImageBitmap(result);
			}
		});
		remote.requestServerForBitmap(user.imgUrl);
		//remote.requestServerForBitmap("https://karnevalistse.s3-eu-west-1.amazonaws.com/uploads/karnevalist/foto/34/155658_10151094621063226_25305867_n.jpg?AWSAccessKeyId=AKIAIXKT62LGJLGPB3AA&Signature=weGmOuAHyHoQpibcF0djwP2XZ3U%3D&Expires=1391149924");
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		showActionBarLogo(true);
	}

}
