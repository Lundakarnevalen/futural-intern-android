package se.lundakarnevalen.android;

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
		TextView name = (TextView) root.findViewById(R.id.detail_info1);
		TextView uName = (TextView) root.findViewById(R.id.user_info1);
		TextView mail = (TextView) root.findViewById(R.id.detail_info2);
		TextView uMail = (TextView) root.findViewById(R.id.user_info2);
		TextView phone = (TextView) root.findViewById(R.id.detail_info3);
		TextView uPhone = (TextView) root.findViewById(R.id.user_info3);
		TextView member = (TextView) root.findViewById(R.id.detail_info4);
		TextView uMember = (TextView) root.findViewById(R.id.user_info4);

		
		
		picture.setImageResource(R.drawable.sections_image);
		name.setText("Namn:");
		uName.setText("Rebecka Alves-Martins"); 
		mail.setText("Mailadress:");
		uMail.setText("rebecka.am@gmail.com");
		phone.setText("Telefonnummer:");
		uPhone.setText("072-2538709");
		member.setText("Sektion/-er:");
		uMember.setText("Kommunikation, Barnevalen, Klipperiet");
		
		
		
		


		return root;

	}

}
