package fragments.futugram;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private BitmapHandler listBitmaps;

	protected ImageAdapter(Context c, BitmapHandler listBitmaps) {
		this.listBitmaps = listBitmaps;

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