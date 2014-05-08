package fragments.futugram;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.GridView;

public class BitmapHandler {
	
	private static final String TAG = BitmapHandler.class.getSimpleName();
	
	private List<Bitmap> listBitmaps;
	private GridView gridView;
	
	
	public BitmapHandler(GridView gridView) {
		this.gridView = gridView;
		
		listBitmaps = new ArrayList<Bitmap>();
	}
	
	public int size() {
		return listBitmaps.size();
	}
	
	public boolean isEmpty() {
		return listBitmaps.isEmpty();
	}

	public Bitmap get(int position) {
		return listBitmaps.get(position);
	}

	public void add(Bitmap result) {
		Log.d(TAG, "Adding photo with index: " + listBitmaps.size());
		listBitmaps.add(result);
		gridView.invalidateViews();
	}
}
