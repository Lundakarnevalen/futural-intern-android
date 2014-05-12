package fragments.futugram;

import java.io.File;

import json.ReceivePicture;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SendRemote {

	private static final String TAG = SendRemote.class.getSimpleName();

	private LKRemote sendRemote;
	private Context context;
	private Gson gson;
	private FrPrepareSending fragment;

	public SendRemote(Context context, FrPrepareSending fragment) {
		this.fragment = fragment;
		this.context = context;

		gson = new Gson();

		sendRemote = new LKRemote(context);
		sendRemote.setTextResultListener(new TextSendRecall());

	}

	private class TextSendRecall implements TextResultListener {
		@Override
		public void onResult(String result) {

			if (result == null) {
				Log.d(TAG, "Result was null");
				return;
			}

			Log.d(TAG, "Result: " + result);

		}
	}

	public void sendBitmapToServer(String imageurl, String caption) {

		LKUser user = new LKUser(context);
		user.getUserLocaly();

		Log.d(TAG, "Image url:" + imageurl);
		Log.d(TAG, "Caption: " + caption);

		Toast.makeText(context, "Sending picture", Toast.LENGTH_LONG).show();

		Ion.with(context, LKRemote.remoteAdr + "api/photos")
				.setMultipartParameter("token", user.token)
				.setMultipartParameter("photo[caption]", caption)
				.setMultipartFile("photo[image]", new File(imageurl))
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							Log.d(TAG, e.toString());
							failedToSend();
							return;
						}

						if (result != null) {
							String s = result.toString();

							ReceivePicture pic = gson.fromJson(s,
									ReceivePicture.class);

							if (pic.success.equals("true")) {
								Toast.makeText(
										context,
										context.getString(R.string.picture_sent),
										Toast.LENGTH_LONG).show();
								fragment.popFragmentStack();
							} else {
								failedToSend();
							}
							Log.d(TAG, result.toString());
						}
					}

					private void failedToSend() {
						Toast.makeText(context,
								context.getString(R.string.picture_failure),
								Toast.LENGTH_LONG).show();
						fragment.returnToSendMode();
					}
				});
	}
}
