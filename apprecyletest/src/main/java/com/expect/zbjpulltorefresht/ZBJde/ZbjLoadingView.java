package com.expect.zbjpulltorefresht.ZBJde;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.expect.zbjpulltorefresht.R;

public class ZbjLoadingView extends ImageView {
	// 动画图片列表
	private Bitmap[] images = new Bitmap[4];
	private float scale;// 缩放比例
	private long startTime;// 动画开始时间
	private boolean run;// 动画播放标识
	private Rect rect = new Rect();// 绘图区域

	public ZbjLoadingView(Context context) {
		super(context);
		init();
	}

	public ZbjLoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZbjLoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// 初始化动画图片
	private void init() {
		images[0] = BitmapFactory.decodeResource(getContext().getResources(),
				R.mipmap.bajie_1);
		images[1] = BitmapFactory.decodeResource(getContext().getResources(),
				R.mipmap.bajie_2);
		images[2] = BitmapFactory.decodeResource(getContext().getResources(),
				R.mipmap.bajie_3);
		images[3] = BitmapFactory.decodeResource(getContext().getResources(),
				R.mipmap.bajie_4);

	}

	// 修改缩放比例
	public void setScale(float scale) {
		this.scale = scale;
		if(scale<1){
			run = false;
			postInvalidate();
		}
	}

	// 翻滚吧猪宝宝
	public void startRun() {
		if (run) {
			return;
		}
		run = true;
		scale = 1;
		startTime = System.currentTimeMillis();
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int index = 0;
		if (run) {
			// 100毫秒切换下一张图片
			long time = System.currentTimeMillis();
			index = (int) ((time - startTime) / 100 % images.length);
			postInvalidateDelayed(5);
			if(index<0){
				index=0;
			}
		}
		if (images[index] != null && rect != null) {
			// 设置绘图区域
			rect.set((int) (getWidth() / 2 - getWidth() * scale / 2),
					(int) (getHeight() / 2 - getHeight() * scale / 2),
					(int) (getWidth() / 2 + getWidth() * scale / 2),
					(int) (getHeight() / 2 + getHeight() * scale / 2));
			// 绘制图片
			canvas.drawBitmap(images[index], null, rect, null);
		}
	}
}
