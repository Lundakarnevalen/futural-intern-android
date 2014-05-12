package fragments.futugram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import se.lundakarnevalen.android.R;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import fragments.LKFragment;

public class FrPrepareSending extends LKFragment {
	
	private static final String TAG = FrPrepareSending.class.getSimpleName();
	
	private View rootView;
	private SendRemote sendRemote;
	
	private Button buttonCancel;
	private Button buttonSend;
	
	private ImageView image;
	
	private String imageUrl;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fr_karnegram_image_send, null);
	
		imageUrl = getArguments().getString("url");
		
		sendRemote = new SendRemote(getContext());
		
		buttonCancel = (Button) rootView.findViewById(R.id.cancel);
		buttonCancel.setOnClickListener(new CancelButton());
		
		buttonSend = (Button) rootView.findViewById(R.id.send);
		buttonSend.setOnClickListener(new SendButton());
		
		RelativeLayout layoutImage = (RelativeLayout) rootView.findViewById(R.id.karnegram_full_size);
		
		image = (ImageView) layoutImage.findViewById(R.id.karnegram_row_item_image);
		
		loadImage(image, imageUrl);
		
		return rootView;
		
	}
	
	private void loadImage(ImageView image, String imageUrl) {
		Bitmap bm = loadBitmap(imageUrl);
		
		if(bm == null) {
			Log.d(TAG, "Result was null");
			return;
		}
		
		image.setImageBitmap(bm);
	}
	
	private Bitmap loadBitmap(String url) {
		Uri uri = Uri.parse(url);
		
		File f = new File(uri.toString());
		
		return BitmapFactory.decodeFile(f.getAbsolutePath());
	}

	private class CancelButton implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			popFragmentStack();
		}
	}
	
	private class SendButton implements OnClickListener {

		@Override
		public void onClick(View v) {
			sendRemote.sendBitmapToServer(imageUrl);
		}
	}
}
