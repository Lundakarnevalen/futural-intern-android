package fragments;

import se.lundakarnevalen.android.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FrKarnegramImage extends LKFragment {
	private ImageView fullSizeImage;
	private View rootView;
	private Bitmap bitmap;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fr_karnegram_image, null);
		fullSizeImage = (ImageView) rootView.findViewById(R.id.karnegram_full_size);
		fullSizeImage.setImageBitmap(bitmap);
		rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popFragmentStack();
			}
		});
		return rootView;
	}

	public void setArguments(Bitmap bitmap) {
		// TODO Auto-generated method stub
		this.bitmap = bitmap;
		
	}





}
