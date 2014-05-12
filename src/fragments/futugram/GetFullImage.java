package fragments.futugram;

import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GetFullImage {

	private static final String TAG = GetRemote.class.getSimpleName();
	
	private LKRemote getRemote;
	private ImageView imageView;
	private RelativeLayout shadow;
	private Context context;
	private View spinner;
	
	public GetFullImage(Context context) {
		this.context = context;
		
		getRemote = new LKRemote(context);
		getRemote.setBitmapResultListener(new BitmapGetRecall());
	}
	
	public void getImage(String url, ImageView imageView, RelativeLayout shadow,  View spinner) {
		Log.d(TAG, "Getting image");
		this.spinner = spinner;
		this.imageView = imageView;
		this.shadow = shadow;
		shadow.setVisibility(View.GONE);
		imageView.setVisibility(View.GONE);
		
		LKUser user = new LKUser(context);
		user.getUserLocaly();
		
		getRemote.requestServerForBitmap(url);
	}
	
	private class BitmapGetRecall implements BitmapResultListener {
		@Override
		public void onResult(Bitmap result) {
			Log.d(TAG, "Result");
			if (result == null) {
				// TODO Worth bothering that some photo isn't shown?
				return;
			}
			spinner.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);
			shadow.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(result);
		}
	}
}
