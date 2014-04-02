package fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fragments.SongsPagerFragment.ShowSongFragment;
import se.lundakarnevalen.android.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	private ViewGroup mLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sangbok_groups_layout, container, false);
		
		mLayout = get(R.id.sangbok_groups_layout, root, ViewGroup.class);
		List<SongGroup> groups = new ArrayList<SongGroupsFragment.SongGroup>();
		
		SongGroup sg1 = new SongGroup();
		sg1.name = "KAREVALSMELODIN";
		sg1.imageBox = R.drawable.songs_karnmel_box;
		sg1.imageShadow = R.drawable.songs_karnmel_shadow;
		readGroup(sg1,R.array.sangbok_songs_group_karnmel);
		
		groups.add(sg1);
		
		SongGroup sg2 = new SongGroup();
		sg2.name = "ALKOHOLFRIA VISOR";
		sg2.imageBox = R.drawable.songs_noacl_box;
		sg2.imageShadow = R.drawable.songs_noacl_shadow;
		readGroup(sg2,R.array.sangbok_songs_group_noacl);
		groups.add(sg2);
		
		SongGroup sg3 = new SongGroup();
		sg3.name = "ÖL VISOR";
		sg3.imageBox = R.drawable.songs_ol_box;
		sg3.imageShadow = R.drawable.songs_ol_shadow;
		readGroup(sg3,R.array.sangbok_songs_group_ol);
		groups.add(sg3);
		
		for(final SongGroup sg: groups){
			createHeader(inflater, mLayout, sg);
			for (int i = 0; i < sg.songs.length; i++) {
				final int songIdx = i;
				createSong(inflater, mLayout, sg, songIdx, new OnClickListener() {
					@Override
					public void onClick(View v) {
						openSongGroup(sg, songIdx);
					}

				});
			}
		}
		return root;
	}

	private void readGroup(SongGroup sg1, int arrayId) {
		sg1.songArrayId = arrayId;
		String[] karnmel = getResources().getStringArray(arrayId);
		sg1.songNumbers = new int[karnmel.length];
		sg1.songs = new String[karnmel.length];
		for (int i = 0; i < karnmel.length; i++){ 
			String song = karnmel[i]; 
			String[] split = song.split("\\|");
			sg1.songNumbers[i] = Integer.parseInt(split[0]);
			sg1.songs[i] = split[1];
		}
	}

	private void openSongGroup(SongGroup sg, int selected) {
		loadFragment(SongsPagerFragment.newInstance(sg.songArrayId, sg.imageBox, selected), true);
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
		int songArrayId;
		String[] songs;
		int[] songNumbers;
		
	}


	private void createSong(LayoutInflater inflater, ViewGroup layout, SongGroup sg, int i, OnClickListener l) {
		View t = inflater.inflate(R.layout.sangbok_groups_inflated_groupitem, layout, false);
		get(R.id.sangbok_groups_inflated_groupitem_text, t, TextView.class).setOnClickListener(l);
		get(R.id.sangbok_groups_inflated_groupitem_text, t, TextView.class).setText(sg.songs[i]);
		get(R.id.sangbok_groups_inflated_groupitem_nbr, t, TextView.class).setText(String.valueOf(sg.songNumbers[i]));
		layout.addView(t);
	}

	private void createHeader(LayoutInflater inflater, ViewGroup layout, SongGroup sg) {
		View h = inflater.inflate(R.layout.sangbok_groups_inflated_groupheader, layout, false);
		get(R.id.sangbok_groups_inflated_groupheader_text, h, TextView.class).setBackgroundResource(sg.imageBox);
		get(R.id.sangbok_groups_inflated_groupheader_text, h, TextView.class).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, sg.imageShadow);
		get(R.id.sangbok_groups_inflated_groupheader_text, h, TextView.class).setText(sg.name);
		layout.addView(h);
	}
}