package com.shows.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class GalleryActivity extends Activity {
	public int indexs;	// 当前显示图片的位置
	public String positionUrl;
	public ArrayList<String> listPi;

	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);	 
		
		myGallery  galllery = (myGallery) findViewById(R.id.mygallery);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		indexs = bundle.getInt("position");	// 获取GridViewActivity传来的图片位置position
		positionUrl = bundle.getString("positionUrl");
		listPi = bundle.getStringArrayList("listPi");

		ImageLoaderList();

		int index =0;
		ImageAdapter imgAdapter=new ImageAdapter(this,index);
//		for(index = 0; index < position;index++){
			galllery.setAdapter(imgAdapter);		// 设置图片ImageAdapter
			galllery.setSelection(index);
//		}
//		ImageAdapter imgAdapter=new ImageAdapter(this,index);
//		galllery.setAdapter(imgAdapter);		// 设置图片ImageAdapter
//		galllery.setSelection(position); 		// 设置当前显示图片
		galllery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				finish();
				
			}
		});
//	 	Animation an= AnimationUtils.loadAnimation(this,R.anim.scale );		// Gallery动画
//	 	galllery.setAnimation(an); 
	}


	/**
	 * 图片加载设置
	 */
	public void ImageLoaderList() {
		imageLoader.init(ImageLoaderConfiguration.createDefault(GalleryActivity.this));

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.zhanweitu)
				.showImageForEmptyUri(R.mipmap.zhanweitu)
				.showImageOnFail(R.mipmap.zhanweitu)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
				.build();
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext; 
		private int mPos;
		int index;

		public ImageAdapter(Context context,int index) {
			mContext = context;
			this.index =index;
		}

		public void setOwnposition(int ownposition) {
			this.mPos = ownposition;
		}

		public int getOwnposition() {
			return mPos;
		}

		@Override
		public int getCount() {
			return indexs;
		}

		@Override
		public Object getItem(int position) { 
			mPos=position;
			return position;
		}

		@Override
		public long getItemId(int position) {
			mPos=position; 
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mPos=position;
			ImageView imageview = new ImageView(mContext);
			imageview.setBackgroundColor(0xFF000000);
			imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageview.setLayoutParams(new myGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			imageview.setImageResource(R.mipmap.present);
			// 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView

			for(index = 0; index <= position ;index++){
				imageLoader.displayImage(listPi.get(index), imageview, options);
				Log.i("list",listPi.get(index));
			}

			return imageview;
		}
	}
}