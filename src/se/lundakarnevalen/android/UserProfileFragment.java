package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends LKFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.user_profile_layout, null);
		
		
		ImageView picture = (ImageView) root.findViewById(R.id.profile_picture);
		TextView name = (TextView) root.findViewById(R.id.user_info1);


		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		
		picture.setImageResource(R.drawable.sections_image);
		name.setText(user.fornamn+" "+user.efternamn); 
		
		return root;

	}

}
