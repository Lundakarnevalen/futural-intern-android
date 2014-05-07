package fragments.futugram;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CameraListener implements OnClickListener {
	
	public static final int CAMERA_PIC_REQUEST = 1001;
	
	private static final String TAG = CameraListener.class.getSimpleName();
	private FrKarnegram fragment;
	private Context context;
	
	private ImageView cameraImage;
	
	private ContentValues values;
	private Uri imageUri;
	
	public CameraListener(Context context, FrKarnegram fragment, ImageView cameraImage) {
		this.cameraImage = cameraImage;
		this.fragment = fragment;
		this.context = context;
		
		this.cameraImage.setOnClickListener(this);
	}
	
	public Uri getUri() {
		return imageUri;
	}
	
	@Override
	public void onClick(View v) {
		values = new ContentValues();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.UK).format(new Date());
		values.put(MediaStore.Images.Media.TITLE, "Karnegram " + timeStamp);
		values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

		imageUri = context.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Log.d(TAG, "IMAGE URI: " + imageUri.toString());

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		fragment.startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
}