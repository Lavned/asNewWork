package com.com.shows.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * @author Apathy����
 * 
 *         ViewPager������
 * */
public class ViewPagerAdapter extends PagerAdapter {

	private ImageView[][] mImageViews;

	private ArrayList<String> mImageRes;
	private Context mContext;

	public ViewPagerAdapter(Context context,ImageView[][] imageViews,ArrayList<String> imageRes) {
		this.mContext =context;
		this.mImageViews = imageViews;
		this.mImageRes = imageRes;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mImageRes.size() == 0) {
			return mImageViews[position / mImageRes.size() % 2][0];
		} else {
			((ViewPager) container).removeView(mImageViews[position
					/ mImageRes.size() % 2][position % mImageRes.size()]);
			((ViewPager) container).addView(mImageViews[position
					/ mImageRes.size() % 2][position % mImageRes.size()], 0);
		}



		return mImageViews[position / mImageRes.size() % 2][position
				% mImageRes.size()];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mImageRes.size() == 0) {
			((ViewPager) container).removeView(mImageViews[position
					/ mImageRes.size() % 2][0]);
		} else {
			((ViewPager) container).removeView(mImageViews[position
					/ mImageRes.size() % 2][position % mImageRes.size()]);
		}


	}
}
