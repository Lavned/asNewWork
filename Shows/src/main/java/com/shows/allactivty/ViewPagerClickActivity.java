package com.shows.allactivty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;

public class ViewPagerClickActivity extends BaseActivity {

    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_click);

        ImageView view_imag= (ImageView) findViewById(R.id.view_image);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String imapath =bundle.getString("images");
        ImageLoaderList();
        view_imag.setScaleType(ImageView.ScaleType.FIT_XY);
        // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
        imageLoader.displayImage(imapath, view_imag, options);

        final TextView view_click_getmoney = (TextView) findViewById(R.id.view_click_getmoney);
        view_click_getmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_click_getmoney.setText("您已领取0.05元");
            }
        });
    }

    /**
     * 图片加载设置
     */
    public void ImageLoaderList() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(ViewPagerClickActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.luncher)
                .showImageForEmptyUri(R.mipmap.luncher)
                .showImageOnFail(R.mipmap.luncher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }
}
