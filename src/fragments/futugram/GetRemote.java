package fragments.futugram;

import json.PictureList;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;

public class GetRemote {

	private static final String TAG = GetRemote.class.getSimpleName();
	
	private BitmapHandler bitmapHandler;
	private LKRemote getRemote;
	private Context context;
	private Gson gson;
	private int index;
	private PictureList listPicture;
		
	protected GetRemote(Context context, BitmapHandler bitmapHandler) {
		this.bitmapHandler = bitmapHandler;
		this.context = context;
		index = 0;
		
		gson = new Gson();
		
		getRemote = new LKRemote(context);
		getRemote.setTextResultListener(new TextGetRecall());
		getRemote.setBitmapResultListener(new BitmapGetRecall());
	}
	
	private class BitmapGetRecall implements BitmapResultListener {
		@Override
		public void onResult(Bitmap result) {
			if (result == null) {
				// TODO Worth bothering that some photo isn't shown?
				return;
			}
			
			if(index < listPicture.photos.size()) {
				getPicture(listPicture.photos.get(index).thumb);
			}
			
			bitmapHandler.add(result);
		}
	}
	
	public void getPictures() {

		if (!LKUser.localUserStored(context)) {
			Log.d(TAG, "No user stored locally, cannot fetch pictures");
			return;
		}

		LKUser user = new LKUser(context);
		user.getUserLocaly();
		
		getRemote.requestServerForText("api/photos?token=" + user.token, "", LKRemote.RequestType.GET, false);
	}

	private class TextGetRecall implements TextResultListener {
		@Override
		public void onResult(String result) {
			
			if (result == null) {
				Log.d(TAG, "Result was null on textRecall");
				// TODO Handle the error
				return;
			}
			Log.d(TAG, "Parsing result, result was: " + result);

			listPicture = gson.fromJson(result, PictureList.class);

			if (!listPicture.success.equals("true")) {
				// TODO Handle the error?
				return;
			}

			// Here we know that there was a result and it was successful
			Log.d(TAG, "Starting to fetch bitmaps from urls");

			if(listPicture.photos.isEmpty()) {
				return;
			}
			getPicture(listPicture.photos.get(index).thumb);
		}
	}
	
	private void getPicture(String url) {
		getRemote.requestServerForBitmap(url);
		index++;
	}
	
	public String getUrl(int index){
		return listPicture.photos.get(index).url;
	}
}
