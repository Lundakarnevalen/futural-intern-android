package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKTextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegistrationProgressFragment extends LKFragment{
	LKTextView name, email, phone, sections;
	LKButton edit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.registrationprogress_layout, null);
		name = (LKTextView) root.findViewById(R.id.name);
		email = (LKTextView) root.findViewById(R.id.email);
		phone = (LKTextView) root.findViewById(R.id.phone);
		sections = (LKTextView) root.findViewById(R.id.sections);
		edit = (LKButton) root.findViewById(R.id.edit_button);
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadFragment(new RegistrationFragment(), false);
			}
		});
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Registrering");
		LKUser user = new LKUser(getActivity().getApplicationContext());
		user.getUserLocaly();
		
		name.setText("f:"+user.fornamn+" "+user.efternamn+"f");
		email.setText("u"+user.email);
		phone.setText("t"+user.telnr);
		StringBuilder sectionsText = new StringBuilder();
		if(user.sektioner != null)
			for(int i : user.sektioner)
				sectionsText.append("id: "+i+"\n");	
		sections.setText("sek"+sectionsText.toString());
				
	}
}
