package fragments.futugram;

import se.lundakarnevalen.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import fragments.LKFragment;

public class FrKarnegramImage extends LKFragment {
	private ImageView fullSizeImage;
	private TextView viewName;
	private TextView viewCaption;
	private View rootView;
	private View spinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fr_karnegram_image, null);
		
		fullSizeImage = (ImageView) rootView.findViewById(R.id.karnegram_full_size);
		viewCaption = (TextView) rootView.findViewById(R.id.caption);
		viewName = (TextView) rootView.findViewById(R.id.name);
		
		View spinner = rootView.findViewById(R.id.karnegram_full_size_spinner);
		
		GetFullImage fullImage = new GetFullImage(getContext());
		String url = getArguments().getString("URL");
		String caption = getArguments().getString("caption");
		String name = getArguments().getString("name");
		
		viewCaption.setText(caption);
		viewName.setText(name);
		
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
