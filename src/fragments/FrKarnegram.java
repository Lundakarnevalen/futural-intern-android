package fragments;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FrKarnegram extends LKFragment {
	
	private static final int CAMERA_PIC_REQUEST = 1001; 
	
	private Bitmap thumbImage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_karnegram, null);
		
		
		GridView gridView = (GridView) rootView.findViewById(R.id.karnegram_gridview);
		
		gridView.setAdapter(new ImageAdapter(getContext()));

		ImageView cameraImage = (ImageView) rootView.findViewById(R.id.karnegram_camera_image);
		
		cameraImage.setOnClickListener(new CameraListener());
	
		return rootView;
	}
	
	private class CameraListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_PIC_REQUEST) {
	        System.out.println("I CAME BACK!!!");
	        
	        thumbImage = (Bitmap) data.getExtras().get("data");
	    }
	}
	
	private class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private LayoutInflater inflater; 

	    public ImageAdapter(Context c) {
	        mContext = c;
	        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public int getCount() {
	        return mThumbIds.length;
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
	    	
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	        
	        	gridView = (View) inflater.inflate(R.layout.fr_karnegram_row_item, null);
	        	
	        	imageView = (ImageView) gridView.findViewById(R.id.karnegram_row_item_image);
	        } else {
	           gridView = (RelativeLayout) convertView;
	           imageView = (ImageView) gridView.findViewById(R.id.karnegram_row_item_image);
	        }

	        if(thumbImage != null) {
	        	imageView.setImageBitmap(thumbImage);
	        } else {
	        	imageView.setImageResource(mThumbIds[position]);
	        }
	        
	        return gridView;
	    }

	    // references to our images
	    private Integer[] mThumbIds = {
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken,
	           R.drawable.fabriken
	    };
	}
}
