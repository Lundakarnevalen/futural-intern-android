package fragments.futugram;

import se.lundakarnevalen.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import fragments.LKFragment;

public class FrKarnegramImage extends LKFragment {
	private ImageView fullSizeImage;
	private View rootView;
	private View spinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fr_karnegram_image, null);
		fullSizeImage = (ImageView) rootView
				.findViewById(R.id.karnegram_full_size);
		View spinner = rootView.findViewById(R.id.karnegram_full_size_spinner);
		GetFullImage fullImage = new GetFullImage(getContext());
		String url = getArguments().getString("URL");
		fullImage.getImage(url, fullSizeImage, spinner);
		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popFragmentStack();
			}
		});
		return rootView;
	}

}
