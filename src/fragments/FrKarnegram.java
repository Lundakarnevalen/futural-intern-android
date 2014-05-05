package fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import json.Picture;
import json.PictureList;
import json.Token;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class FrKarnegram extends LKFragment {

	private static final int CAMERA_PIC_REQUEST = 1001;

	private static final String TAG = FrKarnegram.class.getSimpleName();

	private LKRemote sendRemote;
	private LKRemote getRemote;
	private Gson gson;

	private GridView gridView;

	private List<Bitmap> listBitmaps;

	private ContentValues values;
	private Uri imageUri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_karnegram, null);

		listBitmaps = new ArrayList<Bitmap>();

		gridView = (GridView) rootView.findViewById(R.id.karnegram_gridview);

		gridView.setAdapter(new ImageAdapter(getContext()));

		ImageView cameraImage = (ImageView) rootView
				.findViewById(R.id.karnegram_camera_image);

		cameraImage.setOnClickListener(new CameraListener());

		sendRemote = new LKRemote(getContext());
		sendRemote.setTextResultListener(new TextSendRecall());

		getRemote = new LKRemote(getContext());
		getRemote.setTextResultListener(new TextGetRecall());
		getRemote.setBitmapResultListener(new BitmapGetRecall());

		gson = new Gson();

		getPictures();

		getAlbumStorageDir("Karnegram");

		return rootView;
	}

	private class TextSendRecall implements TextResultListener {
		@Override
		public void onResult(String result) {

		}
	}

	private class BitmapGetRecall implements BitmapResultListener {
		@Override
		public void onResult(Bitmap result) {

			if (result == null) {
				// TODO Worth bothering that some photo isn't shown?
				return;
			}

			addBitmap(result);
		}
	}

	private class TextGetRecall implements TextResultListener {
		@Override
		public void onResult(String result) {

			if (result == null) {
				Log.d(TAG, "Result was null");
				// TODO Handle the error
				return;
			}
			Log.d(TAG, "Parsing result, result was: " + result);

			PictureList jsonPicture = gson.fromJson(result, PictureList.class);

			if (!jsonPicture.success.equals("true")) {
				// TODO Handle the error?
				return;
			}

			// Here we know that there was a result and it was successful
			Log.d(TAG, "Starting to fetch bitmaps from urls");

			for (Picture picture : jsonPicture.listPictures) {
				Log.d(TAG, "Requesting bitmaps from server");
				getRemote.requestServerForBitmap(picture.url);
			}
		}
	}

	private void getPictures() {

		if (!LKUser.localUserStored(getContext())) {
			Log.d(TAG, "No user stored locally, cannot fetch pictures");
			return;
		}

		LKUser user = new LKUser(getContext());
		user.getUserLocaly();

		Token token = new Token(user.token);

		String json = gson.toJson(token);

		Log.d(TAG, "Requesting Server For text with the token: " + json);

		getRemote.requestServerForText("api/photos", json, RequestType.GET,
				false);
	}

	private class CameraListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			values = new ContentValues();

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.UK).format(new Date());
			values.put(MediaStore.Images.Media.TITLE, "Karnegram " + timeStamp);
			values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

			imageUri = getContext().getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			Log.d(TAG, "IMAGE URI: " + imageUri.toString());

			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST)
			try {
				Bitmap picture = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
				addBitmap(picture);
				String imageurl = getRealPathFromURI(imageUri);

				sendBitmapToServer(picture, imageurl);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * Not sure, copied from the Internet, seems to work even though it is
	 * depricated
	 * 
	 * @param contentUri
	 * @return
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/*
	 * if (requestCode == CAMERA_PIC_REQUEST) {
	 * System.out.println("I CAME BACK!!!");
	 * 
	 * try { Bitmap bitmap = (Bitmap) data.getExtras().get("data");
	 * 
	 * Log.d(TAG, "Storing image!");
	 * 
	 * File mediaStorageDir = new
	 * File(Environment.getExternalStoragePublicDirectory
	 * (Environment.DIRECTORY_PICTURES), "Karnegram");
	 * 
	 * // Create the storage directory if it does not exist if (!
	 * mediaStorageDir.exists()){ if (! mediaStorageDir.mkdirs()){
	 * Log.d("MyCameraApp", "failed to create directory"); return; } }
	 * 
	 * String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
	 * Locale.UK).format(new Date()); File mediaFile = new
	 * File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp +
	 * ".jpg");
	 * 
	 * FileOutputStream out = null; try { out = new FileOutputStream(mediaFile);
	 * bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); } catch (Exception
	 * e) { e.printStackTrace(); } finally { try{ out.close(); } catch(Throwable
	 * ignore) {} }
	 * 
	 * // sendBitmapToServer(bitmap); // addBitmap(bitmap); }
	 * catch(NullPointerException e) { // Don't do anything } }}
	 */

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

	private void sendBitmapToServer(Bitmap bitmap, String imageurl) {

		Picture picture = new Picture();
		picture.image = imageurl;
		picture.caption = "No Caption";

		String json = gson.toJson(picture);

		Log.d(TAG, "Sending image to server: " + json);

		sendRemote.requestServerForText("api/photos", json, RequestType.POST,
				false);
	}

	private void addBitmap(Bitmap bitmap) {
		listBitmaps.add(bitmap);
		gridView.invalidateViews();
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater inflater;

		public ImageAdapter(Context c) {
			mContext = c;
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return listBitmaps.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			View gridView;
			ImageView imageView;

			if (convertView == null) { // if it's not recycled, initialize some
										// attributes

				gridView = (View) inflater.inflate(
						R.layout.fr_karnegram_row_item, null);

				imageView = (ImageView) gridView
						.findViewById(R.id.karnegram_row_item_image);
			} else {
				gridView = (RelativeLayout) convertView;
				imageView = (ImageView) gridView
						.findViewById(R.id.karnegram_row_item_image);
			}

			if (!listBitmaps.isEmpty()) {
				imageView.setImageBitmap(listBitmaps.get(position));
			}

			return gridView;
		}
	}
}
