package fragments.futugram;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import json.Picture;
import json.SendPicture;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.gson.Gson;

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

	public void sendBitmapToServer(Bitmap bitmap) {
		// public void sendBitmapToServer(Bitmap bitmap, String imageurl, String
		// name) {

		LKUser user = new LKUser(context);
		user.getUserLocaly();

		Log.d(TAG, "Token:" + user.token);
		
		Picture picture = new Picture();
		picture.image = "something1";
		picture.caption = "No Caption";
		
		SendPicture sp = new SendPicture();
		sp.token = user.token;
		sp.photo = picture;
		
		// JsonObject picture = new JsonObject();
		// picture.addProperty("image", "something1");
		// picture.addProperty("caption", "No caption");

		// String json = gson.toJson(picture);

		// Log.d(TAG, "Sending image to server: " + json);

		Log.d(TAG, "Starting the sending with json:");
		Log.d(TAG, "Token: " + gson.toJson(user.token));
		Log.d(TAG, "Photo:" + gson.toJson(picture));

		 sendRemote.requestServerForText("api/photos", gson.toJson(sp), RequestType.POST, false);
		 
//			Ion.with(context, "https://karnevalist-stage.herokuapp.com/api/photos")
////	 Ion.with(context, "https://koush.clockworkmod.com/test/echo")
//	.setMultipartFile("Something1", createFile(bitmap))
//	.setMultipartParameter("token", user.token)
//	.setMultipartParameter("photo", gson.toJson(picture))
//	.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
//		@Override
//		public void onCompleted(Exception e, JsonObject result) {
//
//			if (e != null) {
//				Log.d(TAG, "ERROR FROM SERVER: " + e.toString());
//			}
//
//			Log.d(TAG, "RETURN FROM SERVER ON UPLOAD: " + result);
//			
//			if (result != null) {
//				String json = result.getAsString();
////				Log.d(TAG, json);
////				SendPicture sp = gson.fromJson(json, SendPicture.class);
////				Log.d(TAG, "Send token:" + sp.token);
////				Log.d(TAG, "Send Pic: " + sp.photo.image);
//			}
//		}
//	});

	}

	private File createFile(Bitmap b) {

		File f = null;
		try {
			// create a file to write bitmap data
			f = new File(context.getCacheDir(), "apa");
			f.createNewFile();

			// Convert bitmap to byte array
			Bitmap bitmap = b;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
			byte[] bitmapdata = bos.toByteArray();

			// write the bytes in file
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.d(TAG, "bad stuff");
		}

		return f;
	}
}
