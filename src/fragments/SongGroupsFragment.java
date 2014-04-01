package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fragments.SongsFragment.ShowSongFragment;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import static util.ViewHelper.*;

public class SongGroupsFragment extends LKFragment {
	
	public static SongGroupsFragment newInstance(){
		return new SongGroupsFragment();
	}
	
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sangbok_groups_layout, container, false);
		
		mListView = get(R.id.sangbok_groups_list, root, ListView.class);
		
		SongGroupAdapter a = new SongGroupAdapter(getContext());
		
		SongGroup sg1 = new SongGroup();
		sg1.name = "KAREVALSMELODIN";
		sg1.imageBox = R.drawable.songs_karnmel_box;
		sg1.imageShadow = R.drawable.songs_karnmel_shadow;
		sg1.songNumbers = new int[]{6};
		sg1.songs = new String[]{"KAREVALSMELODIN"};
		a.groups.add(sg1);
		
		SongGroup sg2 = new SongGroup();
		sg2.name = "ALKOHOLFRIA VISOR";
		sg2.imageBox = R.drawable.songs_noacl_box;
		sg2.imageShadow = R.drawable.songs_noacl_shadow;
		sg2.songNumbers = new int[]{9,10};
		sg2.songs = new String[]{"NYKTRONOUTEN","MIN KOMPIS RASMUS"};
		a.groups.add(sg2);
		
		SongGroup sg3 = new SongGroup();
		sg3.name = "ÖL VISOR";
		sg3.imageBox = R.drawable.songs_ol_box;
		sg3.imageShadow = R.drawable.songs_ol_shadow;
		sg3.songNumbers = new int[]{12,13};
		sg3.songs = new String[]{"KARNEVÖL","MIN ÖL"};
		a.groups.add(sg3);
		
		mListView.setAdapter(a);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle(getString(R.string.sangbok_title));
	}
	
	private static class SongGroup {
		String name;
		int imageBox;
		int imageShadow;
		String[] songs;
		int[] songNumbers;
		
	}
	
	private static class SongGroupAdapter extends BaseAdapter {
		final Context c;
		List<SongGroup> groups = new ArrayList<SongGroup>();
		
		public SongGroupAdapter(Context c){
			this.c = c;
		}
		
		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int arg0) {
			return groups.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup parent) {
			
			if(view == null) {
				view = LayoutInflater.from(c).inflate(R.layout.sangbok_groups_inflated_layout, parent, false);
			}
			ViewGroup layout = (ViewGroup) view;
			layout.removeAllViewsInLayout();	

			SongGroup sg = groups.get(pos);
			View h = LayoutInflater.from(c).inflate(R.layout.sangbok_groups_inflated_groupheader, layout, true);
			get(R.id.sangbok_groups_inflated_groupheader_box, h, ImageView.class).setImageResource(sg.imageBox);
			get(R.id.sangbok_groups_inflated_groupheader_shadow, h, ImageView.class).setImageResource(sg.imageShadow);
			get(R.id.sangbok_groups_inflated_groupheader_text, h, TextView.class).setText(sg.name);
			
			for (int i = 0; i < sg.songs.length; i++) {
				View t = LayoutInflater.from(c).inflate(R.layout.sangbok_groups_inflated_groupitem, layout, true);
				get(R.id.sangbok_groups_inflated_groupitem_text, t, TextView.class).setText(sg.songs[i]);
				get(R.id.sangbok_groups_inflated_groupitem_nbr, t, TextView.class).setText(String.valueOf(sg.songNumbers[i]));
			}
			
			return view;
		}
	}
	
	
}