package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
public class MapFragment extends LKFragment implements OnTouchListener{
	HashMap<Integer, Position> positions;

	private boolean firstTime; 
	
	private double myLat;
	private double myLng;

	private final String TAG = "Touch";

	private ImageView img;

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	// States onTouchEvent
	private final int NONE = 0;
	private final int DRAG = 1;
	private final int ZOOM = 2;
	private int mode = NONE;

	// Variables for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float newDist = 1f;
	// Control scale 1 = full size
	private float scale = 1f;


	// Every time you switch to this fragment.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//TODO Change the layout
		//View rootView = inflater.inflate(R.layout.fr_layout_map, null);

		sendPosition();

		// Background image
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radio);

		//Bitmap mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.row_arrow);
		Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
		Canvas canvas = new Canvas();
		canvas.setBitmap(bmOverlay);
		canvas.drawBitmap(mBitmap, new Matrix(), null);
		//canvas.drawBitmap(mBitmap2, new Matrix(), null);
		//canvas.drawBitmap(mBitmap2, mBitmap.getWidth()/3, mBitmap.getHeight()/3, null);

		Paint p = new Paint();
		p.setColor(Color.GREEN);
		int i = 0;
		int counter[][] = new int[50][50];
		Random r = new Random();
		while(i <2000) {
			++counter[r.nextInt(50)][r.nextInt(50)];
			i++;
		} 
		for(int j = 0;j<50;j++) {
			for(int jj = 0;jj<50;jj++) {
				if(counter[j][jj]!=0) {
					canvas.drawCircle((float) (((float)j)/50.0 * mBitmap.getWidth()), (float) (((float)jj)/50.0 * mBitmap.getHeight()), counter[j][jj],p);
				}
			}	
		}


		img = new ImageView(getActivity());
		img.setImageBitmap(bmOverlay);
		img.setOnTouchListener(this);
		firstTime = true;
		return img;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Map");
	}

	/**
	 * Send current lat/lng, id and section to the database.
	 * 
	 */
	private boolean sendPosition() {
		LocationManager mlocManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE); 
		Location location = null;
		if(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			location= mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location!=null) {
				myLng = location.getLongitude();
				myLat = location.getLatitude();
				Log.d("GPS:Position",myLng +" "+myLat);
			} else {
				if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(location!=null){
						myLng = location.getLongitude();
						myLat = location.getLatitude();
						Log.d("Network1:Position",myLng +" "+myLat);
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(location!=null){
					myLng = location.getLongitude();
					myLat = location.getLatitude();
					Log.d("Network2:Position",myLng +" "+myLat);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		// Send position here!

		return true;
	}

	/**
	 * Get lat/lng, id and section from the database.
	 * 
	 */
	private void getPositions() {
		// Connect and get positions...
		ArrayList<Position> newPositions = new ArrayList<Position>();

		// have row by row here..
		for(Position p: newPositions) {
			// put id first, and create a new position.. Alt change object...
			positions.put(12312, p);
		}


	}
	/**
	 * Update the positions on the map.
	 * 
	 */
	private void updatePositions() {
		double startLonMap = 13;
		double startLatMap = 55;
		double endLonMap = 14;
		double endLatMap = 56;
		double diffLon = endLonMap - startLonMap;
		double diffLat = endLatMap - startLatMap;

		//Change this to the map.png..
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radio);

		// Create an overlay bitmap
		Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
		Canvas canvas = new Canvas();
		canvas.setBitmap(bmOverlay);
		canvas.drawBitmap(mBitmap, new Matrix(), null);

		Paint p = new Paint();
		p.setColor(Color.GREEN);

		int a = (int)(30*scale);
		int b =	(int)(30*scale);

		HashMap<Marker,Marker> markers = new HashMap<Marker,Marker>();

		for(Integer i:positions.keySet()) {
			Position pos = positions.get(i);
			//
			//Modify color here and +1 for the given section matrix
			// Check within range...
			double lat = (pos.getLat()-startLatMap)/diffLat;
			double lon = (pos.getLng()-startLonMap)/diffLon;
			int x = (int)(a*lat);
			int y = (int)(b*lon);
			Marker temp = new Marker(x,y,pos.getSection());
			Marker temp2 = markers.get(temp);
			if(temp2 != null) {
				temp2.sumX += pos.getLat();
				temp2.sumY += pos.getLng();
				++temp2.counter;
			} else {
				markers.put(temp, temp);
				temp.sumX += pos.getLat();
				temp.sumY += pos.getLng();
				++temp.counter;
			}
			// Very sparse, change to map/double arrays
			// I think HashMap
			// Include x,y,counter,sumlat/counter,sumlng/counter..

		} 
		for(Marker m: markers.keySet()) {
			// TODO
			// use sumX and sumY instead
			canvas.drawCircle((float) (((float)m.x)/((double)a) * mBitmap.getWidth()), (float) (((float)m.y)/((double)b) * mBitmap.getHeight()), m.counter,p);

		}

		img.setImageBitmap(bmOverlay);
	}

	/**
	 * ClusterAlgorithm.
	 * 
	 */
	private void clusteringAlgorithm() {


	}

	private class Position {
		private double lat;
		private double lng;
		private int section;	
		public Position(double lat, double lng, int section) {
			this.lat = lat;
			this.lng = lng;
			this.section = section;
		}
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		public double getLng() {
			return lng;
		}
		public void setLng(double lng) {
			this.lng = lng;
		}
		public int getSection() {
			return section;
		}	
	}


	private class Marker {
		public int x,y,section;
		public double sumX, sumY;
		public int counter;
		public Marker(int x, int y, int section) {
			this.x = x;
			this.y = y;
			this.section = section;
			counter = 0; sumX = 0; sumY = 0;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + section;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Marker other = (Marker) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (section != other.section)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		private MapFragment getOuterType() {
			return MapFragment.this;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		ImageView view = (ImageView) v;
		view.setScaleType(ImageView.ScaleType.MATRIX);
		if(firstTime) {
			matrix.set(view.getImageMatrix());
			firstTime =false;
		}
		float scale;

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:   // first finger down only
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG"); // write to LogCat
			mode = DRAG;
			break;

		case MotionEvent.ACTION_UP: // first finger lifted

		case MotionEvent.ACTION_POINTER_UP: // second finger lifted
			if(mode == ZOOM) {
				this.scale = this.scale*newDist/oldDist; 
				Log.d("END ZOOOM","zoom:" + oldDist/newDist + " scale:"+this.scale);
					generateDots(this.scale);
			}

			mode = NONE;
			Log.d(TAG, "mode=NONE");
			// Uppdatera mapen...

			break;

		case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

			oldDist = spacing(event);
			newDist = oldDist;
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 5f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");  
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) 
			{ 
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
			} 
			else if (mode == ZOOM) 
			{ 
				// pinch zooming
				float newDist2 = spacing(event);
				// Lock zoom out
				if(this.scale*newDist2/oldDist >= 1) {
					//newDist = newDist2;
					newDist = newDist2;
					Log.d(TAG, "newDist=" + newDist);
					if (newDist > 5f) {
						matrix.set(savedMatrix);
						scale = newDist / oldDist; 
						// setting the scaling of the
						// matrix...if scale > 1 means
						// zoom in...if scale < 1 means
						// zoom out
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
			}
			break;
		}
		view.setImageMatrix(matrix); // display the transformation on screen
		return true; // indicate event was handled
	}
	/*
	 * ReGenerate dots every time you zoom in/out
	 */
	private void generateDots(float scale){
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radio);

		Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
		Canvas canvas = new Canvas();
		canvas.setBitmap(bmOverlay);
		canvas.drawBitmap(mBitmap, new Matrix(), null);

		Paint p = new Paint();
		p.setColor(Color.GREEN);
		int i = 0;
		Random r = new Random();
		int a = (int)(30*scale);
		int b =	(int)(30*scale);
		int counter[][] = new int[a][b];

		while(i <2000) {
			++counter[r.nextInt(a)][r.nextInt(b)];
			i++;
		} 
		for(int j = 0;j<a;j++) {
			for(int jj = 0;jj<b;jj++) {
				if(counter[j][jj]!=0) {
					canvas.drawCircle((float) (((float)j)/((double)a) * mBitmap.getWidth()), (float) (((float)jj)/((double)b) * mBitmap.getHeight()), counter[j][jj],p);
				}
			}	
		}
		img.setImageBitmap(bmOverlay);
	}

	/**
	 * Calculate the space between the two fingers on touch
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculates the midpoint between the two fingers
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
