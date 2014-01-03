package se.lundakarnevalen.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class SectionsFragment extends LKFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = (View) inflater.inflate(R.layout.sections_layout, null);
		RelativeLayout right = (RelativeLayout) root.findViewById(R.id.right_tab);
		right.setOnClickListener(new ClickListener());
		return root;
	}
	private class ClickListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			Fragment fragment = new SectionsListFragment();
			
			ContentActivity a = (ContentActivity) getActivity();
			a.getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();
		}
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Sektioner");
	}
}
