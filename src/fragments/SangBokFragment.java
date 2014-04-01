package fragments;

import se.lundakarnevalen.android.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static util.ViewHelper.*;

public class SangBokFragment extends LKFragment {
	
	private ViewPager mViewPager;
	private static int PAGE_MARGIN_DP = 20;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sangbok_songs_layout, container, false);
		
		final Context c = inflater.getContext();
		final FragmentManager fm = getActivity().getSupportFragmentManager();
		mViewPager = get(R.id.sangbok_layout_viewpager, root, ViewPager.class);
		mViewPager.setAdapter(new MyAdapter(fm, c));
		mViewPager.setPageMargin(Math.round(LKFragment.dpToPx(PAGE_MARGIN_DP, c)));
		mViewPager.setClipToPadding(false);
		
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.sangbok_title));
	}
	
	private static class MyAdapter extends FragmentPagerAdapter {
        final String[] songs;
        
		public MyAdapter(FragmentManager fm, Context c) {
            super(fm);
            songs = c.getResources().getStringArray(R.array.sangbok_songs);
        }

        @Override
        public int getCount() {
            return songs.length;
        }

        @Override
        public Fragment getItem(int position) {
        	String[] split = songs[position].split("\\|", 3);
    		String title = split[0];
    		String subtitle = split[1];
    		String text = split[2];
    		return SongFragment.newInstance(title, subtitle, text);
        }
        
        @Override
        public float getPageWidth (int position)
    	{
    		return 0.93f;
    	}
        
       
    }
	
	public static class SongFragment extends Fragment {
		private static String KEY_TITLE = "title";
		private static String KEY_SUBTITLE = "subtitle";
		private static String KEY_TEXT = "text";
		
		public static SongFragment newInstance(String title, String subtitle, String text){
			SongFragment f = new SongFragment();
			Bundle b = new Bundle();
			b.putString(KEY_TITLE, title);
			b.putString(KEY_TEXT, text);
			b.putString(KEY_SUBTITLE, subtitle);
			f.setArguments(b);
			f.setRetainInstance(true);
			return f;
		}

		public SongFragment(){
			super();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sangbok_layout_song_layout, container, false);
			Bundle b = getArguments();
			
			get(R.id.sangbok_layout_song_layout_title, root, TextView.class).setText(b.getString(KEY_TITLE));
			get(R.id.sangbok_layout_song_layout_subtitle, root, TextView.class).setText(b.getString(KEY_SUBTITLE));
			get(R.id.sangbok_layout_song_layout_text, root, TextView.class).setText(b.getString(KEY_TEXT));
			
			return root;
		}
		
		
	}
}