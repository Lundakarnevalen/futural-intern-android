package fragments.futugram;

import json.Picture;
import json.PictureList;
import json.Token;
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

		Token object = new Token(user.token);
		
		String token = user.token;

		Log.d(TAG, "**************************");
		
//		String json = gson.toJson(object);

//		Log.d(TAG, "Requesting Server For text with the token: " + json);

		String newJson = "token=\"" + object.token + "\"";
		
		Log.d(TAG, "Requesting Server For text with the token: " + newJson);
		
//		JsonObject json = new JsonObject();
//		json.addProperty("token", user.token);
		
		getRemote.requestServerForText("api/photos?token=" + token, "", LKRemote.RequestType.GET, false);

//		Ion.with(context) 
////		.load("http://koush.clockworkmod.com/test/echo")
//		.load("https://karnevalist-stage.herokuapp.com/api/photos")
//		.setLogging(TAG, Log.DEBUG)
//		.setJsonObjectBody(json)
//		.asJsonObject()
//		.setCallback(new FutureCallback<JsonObject>() {
//		   @Override
//		    public void onCompleted(Exception e, JsonObject result) {
//		        if(e != null) {
//		        	Log.d(TAG, "Error: " + e.toString());
//		        	
//		        }
//		        
//		        if(result != null) {
//		        	Log.d(TAG, "Result: " + result.toString());
////		        	
//		        }
//		    }
//		}); 
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

			for (Picture picture : jsonPicture.photos) {
				Log.d(TAG, "Requesting bitmaps from server");
				getRemote.requestServerForBitmap(picture.url);
			}
		}
	}

}
