/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.expect.zbjpulltorefresht.ZBJde;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.expect.zbjpulltorefresht.R;


public class XListViewHeader extends LinearLayout {
	private TextView mHintTextView;
	private ZbjLoadingView mZbjLoadingView;
	private int mState = STATE_NORMAL;
	private LinearLayout mContainer;
	private String customaHintText;
	private int headContentHeight;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	public final static int STATE_CUSTOMA = 3;

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		headContentHeight = ConvertUtils.dip2px(context, 80);
		addView(mContainer, lp);

		mZbjLoadingView = (ZbjLoadingView) findViewById(R.id.headAnim);
		mHintTextView = (TextView) findViewById(R.id.headState);
	}

	public void setImageVisible(int visibility) {
		if(mZbjLoadingView!=null){
			mZbjLoadingView.setVisibility(visibility);
		}
	}

	public void setCustomaHeaderText(String hintText) {
		customaHintText = hintText;
		setState(mState, true);
	}

	public void setState(int state) {

		setState(state, false);
	}

	private void setState(int state, boolean forceRefresh) {

		if (state == mState && !forceRefresh)
			return;

		switch (state) {
			case STATE_NORMAL:
				if (mState == STATE_READY) {

				}
				if (mState == STATE_REFRESHING) {

				}
				if (TextUtils.isEmpty(customaHintText)) {
					mHintTextView.setText("");
				} else {
					mHintTextView.setText(customaHintText);
				}
				break;
			case STATE_READY:
				if (mState != STATE_READY) {

					if (TextUtils.isEmpty(customaHintText)) {
						mHintTextView.setText("");
					} else {
						mHintTextView.setText(customaHintText);
					}
				}
				break;
			case STATE_REFRESHING:

				if (TextUtils.isEmpty(customaHintText)) {
					mHintTextView.setText("");
				} else {
					mHintTextView.setText(customaHintText);
				}
				mZbjLoadingView.startRun();
				break;
			default:
				break;
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		setHeight(height);
		float scale = height * 1.0f / headContentHeight;
		if (scale > 1f) {
			scale = 1f;
		}
		mZbjLoadingView.setScale(scale);
	}

	public void setHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

	//跟新view高度
}
