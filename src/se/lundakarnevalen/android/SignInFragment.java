package se.lundakarnevalen.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class SignInFragment extends LKFragment{
	View[] forgotPasswordButtons;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.sign_in_layout, null);
		forgotPasswordButtons = new View[2];
		forgotPasswordButtons[0] = root.findViewById(R.id.desc_fail_f);
		forgotPasswordButtons[1] = root.findViewById(R.id.desc_fail_l);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		showActionBarLogo(true);
		
		if(forgotPasswordButtons[0] != null && forgotPasswordButtons[1] != null){
			forgotPasswordButtons[0].setOnClickListener(fogotPasswordClickListener);
			forgotPasswordButtons[1].setOnClickListener(fogotPasswordClickListener);
		}
	}
	
	View.OnClickListener fogotPasswordClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LKFragment fragment = new ResetPasswordFragment();
			loadFragment(fragment, false);//HAX
		}
	};
}