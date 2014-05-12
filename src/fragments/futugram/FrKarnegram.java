package fragments.futugram;

import java.io.File;

import json.ListPicture;
import se.lundakarnevalen.android.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import fragments.LKFragment;

public class FrKarnegram extends LKFragment {

	private static final String TAG = FrKarnegram.class.getSimpleName();

	private CameraListener cameraListener;
	private BitmapHandler bitmapHandler;
	private GetRemote getRemote;

	private GridView gridView;

	private ImageView cameraImage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_karnegram, container,
				false);

		gridView = (GridView) rootView.findViewById(R.id.karnegram_gridview);

		bitmapHandler = new BitmapHandler(gridView);

		getRemote = new GetRemote(getContext(), bitmapHandler);

		gridView.setAdapter(new ImageAdapter(getContext(), bitmapHandler));

		cameraImage = (ImageView) rootView
				.findViewById(R.id.karnegram_camera_image);

		cameraListener = new CameraListener(getContext(), this, cameraImage);

		// fullSizeImage = (ImageView) rootView
		// .findViewById(R.id.karnegram_full_size);
		// fullSizeImage.setVisibility(View.INVISIBLE);

		gridView.setOnItemClickListener(new ImageListener());
		Ion.getDefault(getContext()).configure()
				.setLogging("MyLogs", Log.DEBUG);

		getAlbumStorageDir("Karnegram");

		getRemote.getPictures();

		return rootView;
	}

	private class ImageListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			ListPicture pictures = getRemote.getListPictures();

			String url = pictures.getUrl(position);
			String caption = pictures.getCation(position);
			String name = pictures.getName(position);

			Bundle bundle = new Bundle();
			bundle.putString("URL", url);
			bundle.putString("caption", caption);
			bundle.putString("name", name);
			FrKarnegramImage frkarnegramimage = new FrKarnegramImage();
			frkarnegramimage.setArguments(bundle);
			loadFragment(frkarnegramimage, true);

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		
		if (requestCode != CameraListener.CAMERA_PIC_REQUEST) {
			return;
		}
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		Log.d(TAG, "OK");

		String imageurl = getRealPathFromURI(cameraListener.getUri());

		Log.d(TAG, "Image url:" + imageurl);

		FrPrepareSending fragment = new FrPrepareSending();

		Bundle bundle = new Bundle();
		bundle.putString("url", imageurl);

		fragment.setArguments(bundle);

		loadFragment(fragment, true);
	}

	/**
	 * Not sure, copied from the Internet, seems to work even though it is
	 * deprecated
	 * 
	 * @param contentUri
	 * @return
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(contentUri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				albumName);

		Log.d(TAG, "PATH: " + file.getAbsolutePath());

		if (!file.mkdirs()) {
			Log.e(LOG_TAG, "Directory not created");
		}
		return file;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.futugram));
	}
}
