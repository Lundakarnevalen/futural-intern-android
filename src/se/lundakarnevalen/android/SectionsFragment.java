package se.lundakarnevalen.android;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.remote.SectionSQLiteDB;
import se.lundakarnevalen.widget.LKButton;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import se.lundakarnevalen.widget.slotMachine.AbstractWheelAdapter;
import se.lundakarnevalen.widget.slotMachine.OnWheelChangedListener;
import se.lundakarnevalen.widget.slotMachine.OnWheelScrollListener;
import se.lundakarnevalen.widget.slotMachine.WheelView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SectionsFragment extends LKFragment {
	 
	private View root;
	private RelativeLayout right;
	private int counter = 0;
	private Handler handler;
	private SectionSQLiteDB db;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = (View) inflater.inflate(R.layout.sections_layout, null);
		right = (RelativeLayout) root
				.findViewById(R.id.right_tab);
		right.setOnClickListener(new ClickListener());
		
		LKButton lucky = (LKButton) root.findViewById(R.id.lucky);

		// Slot machine wheels
        initWheel(R.id.slot_1);
        initWheel(R.id.slot_2);
        initWheel(R.id.slot_3);
        
        lucky.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mixWheel(R.id.slot_1);
                mixWheel(R.id.slot_2);
                mixWheel(R.id.slot_3);
            }
        });
        
        handler = new Handler();
        	        
		return root;
	}
	
    /**
     * Mixes wheel
     * @param id the wheel id
     */
    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-350 + (int)(Math.random() * 50), 2000);
    }
	
    // Wheel scrolled flag
    private boolean wheelScrolled = false;
    
    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus();
        }
    };
    
    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                updateStatus();
            }
        }
    };
    
    /**
     * Updates status and randomizes a section to be shown.
     */
    private void updateStatus() {
    	counter++;
    	if	(counter == 3)	{ //Checks that all the three wheels are ready.
    		
    		handler.postDelayed(new Runnable() { //Handles the delay

                public void run() {
                	
                	db = new SectionSQLiteDB(getContext());
                	
                	LKSectionsItem item = db.getRandomSection();
                	
            		SectionsInformationFragment fragment = new SectionsInformationFragment();
            		Bundle bundle = new Bundle();

            		bundle.putString("title", item.title);
            		bundle.putInt("resourceId", item.icon);
            		bundle.putString("information", item.information);

            		fragment.setArguments(bundle);
            		
            		loadFragment(fragment, true);
                	
                }

            }, 320); //The delay in milliseconds 

    		counter = 0;
    	}
     
    }
	
	/**
     * Initializes wheel
     * @param id the wheel widget Id
     */
    private void initWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.setViewAdapter(new SlotMachineAdapter(getContext()));
        wheel.setCurrentItem((int)(Math.random() * 10));
        
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setEnabled(false);
    }
    
    /**
     * Returns wheel by Id
     * @param id the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) root.findViewById(id);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Sektioner");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Fragment fragment = new SectionsListFragment();

			/*ContentActivity a = (ContentActivity) getActivity();
			a.getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();*/
			
			loadFragment(fragment, false);
		}
	}
	
	/**
     * Slot machine adapter
     */
    private class SlotMachineAdapter extends AbstractWheelAdapter {
        // Image size
        final int IMAGE_WIDTH = 150;
        final int IMAGE_HEIGHT = 150;
        
        // Slot machine symbols
        private final int items[] = new int[] {
        		R.drawable.eld,
        		R.drawable.kugghjul,
        		R.drawable.blobb,
        		R.drawable.robothuvud,
        		R.drawable.moln_new
        };
        
        // Cached images
        private List<SoftReference<Bitmap>> images;
        
        // Layout inflater
        private Context context;
        
        /**
         * Constructor
         */
        public SlotMachineAdapter(Context context) {
            this.context = context;
            images = new ArrayList<SoftReference<Bitmap>>(items.length);
            for (int id : items) {
                images.add(new SoftReference<Bitmap>(loadImage(id)));
            }
        }
        
        /**
         * Loads image from resources
         */
        private Bitmap loadImage(int id) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            bitmap.recycle();
            return scaled;
        }

        @Override
        public int getItemsCount() {
            return items.length;
        }

        // Layout params for image view
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            ImageView img;
            if (cachedView != null) {
                img = (ImageView) cachedView;
            } else {
                img = new ImageView(context);
            }
            
            img.setLayoutParams(params);
            SoftReference<Bitmap> bitmapRef = images.get(index);
            Bitmap bitmap = bitmapRef.get();
            if (bitmap == null) {
                bitmap = loadImage(items[index]);
                images.set(index, new SoftReference<Bitmap>(bitmap));
            }
            img.setImageBitmap(bitmap);
            
            return img;
        }
    }
}
