package com.shows.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shows.bean.Picture;
import com.bumptech.glide.Glide;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SwipeAdapter extends BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Context mContext = null;
	private List<Picture> data;

	private int mRightWidth = 0;
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	/**
	 * 图片加载设置
	 */
	public void ImageLoaderList() {
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.luncher)
				.showImageForEmptyUri(R.mipmap.luncher)
				.showImageOnFail(R.mipmap.luncher)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
				.build();
	}


	/**
	 * @param
	 */
	public SwipeAdapter(Context ctx, List<Picture> data, int rightWidth) {
		mContext = ctx;
		this.data = data;
		mRightWidth = rightWidth;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listview_item, parent, false);
			holder = new ViewHolder();
			holder.item_left = (LinearLayout) convertView
					.findViewById(R.id.item_left);
			holder.item_right = (RelativeLayout) convertView
					.findViewById(R.id.item_right);

			holder.tv_title = (TextView) convertView.findViewById(R.id.text_title);
			holder.tv_detail = (TextView) convertView.findViewById(R.id.text_detail);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.image_icons);
			holder.listback = (ImageView) convertView.findViewById(R.id.listback);

			holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
			holder.item_right_txt = (TextView) convertView .findViewById(R.id.item_right_txt);

			LinearLayout.LayoutParams lp1 = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			holder.item_left.setLayoutParams(lp1);

			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth,
					LinearLayout.LayoutParams.MATCH_PARENT);
			holder.item_right.setLayoutParams(lp2);

			holder.item_right_txt = (TextView) convertView
					.findViewById(R.id.item_right_txt);
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}

		LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
		LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		holder.item_right.setLayoutParams(lp2);

		Picture msg = data.get(position);

		holder.tv_title.setText(msg.getTitle());
		holder.tv_detail.setText(msg.getContent());

		holder.tv_title.setVisibility(View.GONE);
		holder.tv_detail.setVisibility(View.GONE);
		holder.iv_pic.setVisibility(View.GONE);
		holder.listback.setVisibility(View.VISIBLE);

		 // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
		Glide.with(mContext)
				.load(msg.getHeader())
				.placeholder(R.mipmap.zhanweitu)
				.error(R.mipmap.zhanweitu)
				.centerCrop()
				.crossFade()
				.into(holder.listback);
       imageLoader.displayImage(msg.getHeader(), holder.iv_pic, options);


		holder.item_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		LinearLayout item_left;
		RelativeLayout item_right;

		TextView tv_title;
		TextView tv_detail;
		ImageView iv_pic;
		ImageView listback;
		TextView item_right_txt;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
}
