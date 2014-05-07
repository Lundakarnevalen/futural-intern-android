package fragments.futugram;

import java.util.List;

import com.google.gson.Gson;

import json.Picture;
import json.PictureList;
import json.Token;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKUser;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class GetRemote {

	private static final String TAG = GetRemote.class.getSimpleName();
	
	private BitmapHandler bitmapHandler;
	private LKRemote getRemote;
	private Context context;
	private Gson gson;
		
	protected GetRemote(Context context, BitmapHandler bitmapHandler) {
		this.bitmapHandler = bitmapHandler;
		this.context = context;
		
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

		Token token = new Token(user.token);

		String json = gson.toJson(token);

		Log.d(TAG, "Requesting Server For text with the token: " + json);

		getRemote.requestServerForText("api/photos", json, RequestType.GET,
				false);
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

}
