package se.lundakarnevalen.android;

import se.lundakarnevalen.widget.LKButton;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SectionsFragment extends LKFragment {

	private ViewFlipper flipper1;
	private ViewFlipper flipper2;
	private ViewFlipper flipper3;
	private int mSpeed;
	private int mCount;
	private int mFactor;
	private boolean mAnimating;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.sections_layout, null);
		RelativeLayout right = (RelativeLayout) root
				.findViewById(R.id.right_tab);
		right.setOnClickListener(new ClickListener());

		flipper1 = (ViewFlipper) root.findViewById(R.id.view_flipper1);
		flipper2 = (ViewFlipper) root.findViewById(R.id.view_flipper2);
		flipper3 = (ViewFlipper) root.findViewById(R.id.view_flipper3);

		// flipper1.setOnClickListener(new LuckyListener());
		// flipper2.setOnClickListener(new LuckyListener());
		// flipper3.setOnClickListener(new LuckyListener());

		mAnimating = false;
		mCount = 0;
		mSpeed = 0;

		// LKButton choose = (LKButton) root.findViewById(R.id.choose);
		// choose.setOnClickListener(new ClickListener());

		LKButton lucky = (LKButton) root.findViewById(R.id.lucky);
		lucky.setOnClickListener(new LuckyListener());

		return root;
	}

	private Runnable r2 = new Runnable() {

		@Override
		public void run() {
			down();
			if (mCount < 1) {
				mAnimating = false;
			} else {
				Handler h = new Handler();
				h.postDelayed(r2, mSpeed);
			}
		}

	};

	private void down() {
		mCount--;
		mSpeed += mFactor;
		Animation outToBottom = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f);
		outToBottom.setInterpolator(new AccelerateInterpolator());
		outToBottom.setDuration(mSpeed);
		Animation inFromTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromTop.setInterpolator(new AccelerateInterpolator());
		inFromTop.setDuration(mSpeed);
		flipper1.clearAnimation();
		flipper1.setInAnimation(inFromTop);
		flipper1.setOutAnimation(outToBottom);
		if (flipper1.getDisplayedChild() == 0) {
			flipper1.setDisplayedChild(2);
		} else {
			flipper1.showPrevious();
		}
	}

	private Runnable r1 = new Runnable() {

		@Override
		public void run() {
			up();
			if (mCount < 1) {
				mAnimating = false;
			} else {
				Handler h = new Handler();
				h.postDelayed(r1, mSpeed);
			}
		}

	};

	private void up() {
		mCount--;
		mSpeed += mFactor;
		Animation inFromBottom = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromBottom.setInterpolator(new AccelerateInterpolator());
		inFromBottom.setDuration(mSpeed);
		Animation outToTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		outToTop.setInterpolator(new AccelerateInterpolator());
		outToTop.setDuration(mSpeed);
		flipper1.clearAnimation();
		flipper1.setInAnimation(inFromBottom);
		flipper1.setOutAnimation(outToTop);
		if (flipper1.getDisplayedChild() == 0) {
			flipper1.setDisplayedChild(2);
		} else {
			flipper1.showPrevious();
		}
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

			ContentActivity a = (ContentActivity) getActivity();
			a.getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();
		}

	}

	private class LuckyListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Log.d("Hej", "hejsan");

			if (mAnimating)
				return;
			mAnimating = true;
			mCount = (int) Math.abs(900) / 300;
			mFactor = (int) 300 / mCount;
			mSpeed = mFactor;
			// down
			Handler h = new Handler();
			h.postDelayed(r2, mSpeed);
		}
	}
}
