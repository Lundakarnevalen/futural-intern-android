package fragments.futugram;

import java.io.File;

import json.Picture;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SendRemote {

	private static final String TAG = SendRemote.class.getSimpleName();
	
	private LKRemote sendRemote;
	private Context context;
	private Gson gson;
	
	public SendRemote(Context context) {
		this.context = context;
		
		gson = new Gson();

		sendRemote = new LKRemote(context);
		sendRemote.setTextResultListener(new TextSendRecall());
	}

	private class TextSendRecall implements TextResultListener {
		@Override
		public void onResult(String result) {

		}
	}
	
	public void sendBitmapToServer(Bitmap bitmap, String imageurl) {

		Picture picture = new Picture();
		picture.image = imageurl;
		picture.caption = "No Caption";

		String json = gson.toJson(picture);

		Log.d(TAG, "Sending image to server: " + json);

		
		Ion.with(context, "https://koush.clockworkmod.com/test/echo")
		.setMultipartParameter("goop", "noop")
		.setMultipartFile("filename.zip", new File("/sdcard/filename.zip"))
		.asJsonObject()
		.setCallback(new FutureCallback<JsonObject>() {
			   @Override
			    public void onCompleted(Exception e, JsonObject result) {
				   
				   Log.d(TAG, "ERROR FROM SERVER: " + e.toString());
				   
				   	String res = result.getAsString();
				   	
				   	Log.d(TAG, "RETURN FROM SERVER ON UPLOAD: " + res);
			    }
			});
		
		
//		sendRemote.requestServerForText("api/photos", json, RequestType.POST,
//				false);
	}
}

