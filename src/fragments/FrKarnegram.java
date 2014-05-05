package fragments;

import java.util.ArrayList;
import java.util.List;

import json.Picture;
import json.PictureList;
import json.Token;
import se.lundakarnevalen.android.R;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.BitmapResultListener;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKUser;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

public class FrKarnegram extends LKFragment {
	
	private static final int CAMERA_PIC_REQUEST = 1001; 
	
	private static final String TAG = FrKarnegram.class.getSimpleName();
	
	private Bitmap thumbImage;
	
	private LKRemote remote;
	private Gson gson;
	
	private GridView gridView;
	
	private List<Bitmap> listBitmaps;
	private ImageView fullSizeImage;
	private ImageView cameraImage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_karnegram, null);
		
		listBitmaps = new ArrayList<Bitmap>();
		
		gridView = (GridView) rootView.findViewById(R.id.karnegram_gridview);
		
		gridView.setAdapter(new ImageAdapter(getContext()));

		cameraImage = (ImageView) rootView.findViewById(R.id.karnegram_camera_image);
		fullSizeImage = (ImageView) rootView.findViewById(R.id.karnegram_full_size);
		fullSizeImage.setVisibility(View.INVISIBLE);
		
		cameraImage.setOnClickListener(new CameraListener());
		gridView.setOnItemClickListener(new ImageListener());

	
		
		remote = new LKRemote(getContext());
		remote.setTextResultListener(new TextRecall());
		remote.setBitmapResultListener(new BitmapRecall());
		
		gson = new Gson();
		
		
		gridView.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				Log.d(TAG,"------------------------HÄÄÄÄÄÄÄÄÄÄÄÄÄRRRRRRRRRRRRR_______----------------------");
				
				if(keyCode == KeyEvent.KEYCODE_BACK && fullSizeImage.getVisibility() == View.VISIBLE)
				{
					Log.d(TAG,"------------------------HÄÄÄÄÄÄÄÄÄÄÄÄÄRRRRRRRRRRRRR_______----------------------");
					fullSizeImage.setVisibility(View.INVISIBLE);
					return true;
				}
				return false;
			}
			
		});
		
//		getPictures();
		
		return rootView;
	}
		
		
	private class BitmapRecall implements BitmapResultListener {
		@Override
		public void onResult(Bitmap result) {
			
			if(result == null) {
//				TODO Worth bothering that some photo isn't shown?
				return;
			}
			
			addBitmap(result);			
		}
	}
	
	private class TextRecall implements TextResultListener {
		@Override
		public void onResult(String result) {
			
			if(result == null) {
				Log.d(TAG, "Result was null");
//				TODO Handle the error
				return;
			}
			Log.d(TAG, "Parsing result, result was: " + result);
			
			PictureList jsonPicture = gson.fromJson(result, PictureList.class);
						
			if(!jsonPicture.success.equals("true")) {
				//TODO Handle the error?
				return;
			}
			
//			Here we know that there was a result and it was successful
			Log.d(TAG, "Starting to fetch bitmaps from urls");
			
			for(Picture picture : jsonPicture.listPictures) {
				Log.d(TAG, "Requesting bitmaps from server");
				remote.requestServerForBitmap(picture.url);
			}
		}
	}

	private void getPictures() {
		
		if (!LKUser.localUserStored(getContext())) {
			Log.d(TAG, "No user stored locally, cannot fetch pictures");
			return;
		}
		
		LKUser user = new LKUser(getContext());
		user.getUserLocaly();
		
		Token token = new Token(user.token);
		
		String json = gson.toJson(token);
		
		Log.d(TAG, "Requesting Server For text with the token: " + json);
		
		remote.requestServerForText("api/photos", json, RequestType.GET, false);
	}
	
	private class CameraListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
		}
	}
	private class ImageListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			fullSizeImage.setImageBitmap(listBitmaps.get(position));
			fullSizeImage.setVisibility(View.VISIBLE);
		
		}
	}
	

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_PIC_REQUEST) {
	        System.out.println("I CAME BACK!!!");
	        
	        try {
	        	Bitmap bitmap = (Bitmap) data.getExtras().get("data"); 
	        	
	        	sendBitmapToServer(bitmap);
	        	addBitmap(bitmap);
	        } catch(NullPointerException e) {
//	        	Don't do anything
	        }
	    }
	}
	
	private void sendBitmapToServer(Bitmap bitmap) {
		
	}

	private void addBitmap(Bitmap bitmap) {
		listBitmaps.add(bitmap);
		gridView.invalidateViews();
	}
	
	private class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private LayoutInflater inflater; 

	    public ImageAdapter(Context c) {
	        mContext = c;
	        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	    	
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	        
	        	gridView = (View) inflater.inflate(R.layout.fr_karnegram_row_item, null);
	        	
	        	imageView = (ImageView) gridView.findViewById(R.id.karnegram_row_item_image);
	        } else {
	           gridView = (RelativeLayout) convertView;
	           imageView = (ImageView) gridView.findViewById(R.id.karnegram_row_item_image);
	        }

	        if(!listBitmaps.isEmpty()) {
	        	imageView.setImageBitmap(listBitmaps.get(position));	        	
	        }
	        
	        return gridView;
	    }
	}
}
