package com.shows.allactivty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.ViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BussRecommendActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    ImageView  buss_telephone,buss_back;;
    private int width;
    private int newWidth;
    private int padding;


    //图片轮播相关
    private ViewPager mViewpager;
    private ViewPagerAdapter mViewPagerAdp;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private LinearLayout layoutDots;
    private final int AUTO = 0;
    private final long delay = 3 * 1000;

    private TextView good_netaddress;
    private JSONObject json;


    private TextView reco_title,reco_content,reco_birfe,reco_address,reco_feature,reco_httpaddress,reco_info,reco_tel,reco_linkman;
    private ImageView reco_headimage;


    private Bundle bundle;
    private  ArrayList<String> pics = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buss_recommend);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        good_netaddress = (TextView) findViewById(R.id.good_netaddress);
        good_netaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(good_netaddress.getText().toString());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        seTextView();
        width = getResources().getDisplayMetrics().widthPixels;
        newWidth = (int) (divideWidth(width, 1080, 6) * 17);
        padding = (int) (divideWidth(width, 1080, 6) * 9);
        // registration view
        mViewpager = (ViewPager) findViewById(R.id.buss_viewPager);
        layoutDots = (LinearLayout) findViewById(R.id.buss_dotLayout);
        // set listener
        mViewpager.setOnPageChangeListener(this);
        try{
            initDots();
            initViewPager();
        }catch (Exception e) {

        }


        buss_telephone = (ImageView) findViewById(R.id.buss_telephone);
        buss_telephone.setOnClickListener(this);
        buss_back= (ImageView) findViewById(R.id.buss_back);
        buss_back.setOnClickListener(this);





    }
    /**
     * 给控件设置网络数据
     */
    private void seTextView() {
        reco_headimage = (ImageView) findViewById(R.id.reco_headimage);

        reco_title = (TextView) findViewById(R.id.reco_title);
        reco_birfe = (TextView) findViewById(R.id.reco_brief);
        reco_address = (TextView) findViewById(R.id.reco_address);
        reco_feature = (TextView) findViewById(R.id.reco_feature);
        reco_httpaddress = (TextView) findViewById(R.id.good_netaddress);
        reco_tel = (TextView) findViewById(R.id.tv_telephone);
        reco_linkman = (TextView) findViewById(R.id.reco_linkman);
        reco_info = (TextView) findViewById(R.id.reco_info);

        reco_title.setText(bundle.getString("title"));
        reco_birfe.setText(bundle.getString("birfe"));
        reco_address.setText(bundle.getString("address"));
        reco_feature.setText(bundle.getString("feature"));
        reco_httpaddress.setText(bundle.getString("httpaddress"));
        reco_tel.setText(bundle.getString("tel"));
        reco_linkman.setText(bundle.getString("linkman"));
        reco_info.setText(bundle.getString("info"));

        pics =  bundle.getStringArrayList("bussList");
//        pics.add("http://www.quazero.com/uploads/allimg/140412/1-140412005945.jpg");
//        pics.add("http://imgsrc.baidu.com/forum/pic/item/4057077ede58786134fa41d7.jpg");
//        pics.add("http://pic13.nipic.com/20110326/2457331_232645672000_2.jpg");

        Glide.with(BussRecommendActivity.this)
                .load(bundle.getString("headimage"))
                .placeholder(R.mipmap.czhanweitu)
                .error(R.mipmap.czhanweitu)
                .centerCrop()
                .crossFade()
                .into(reco_headimage);

        //
    }


    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buss_telephone :
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bundle.getString("tel")));
                startActivity(intent);
                break;
            case R.id.buss_back :
                finish();
                break;
        }
    }

    /**
     *         初始化ViewPager的底部小点
     *
     * */
    private void initDots() {

        mDots = new ImageView[pics.size()];

        for (int i = 0; i < pics.size(); i++) {
            ImageView iv = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newWidth, newWidth);
            lp.leftMargin = padding;
            lp.rightMargin = padding;
            lp.topMargin = padding;
            lp.bottomMargin = padding;
            iv.setLayoutParams(lp);
            iv.setImageResource(R.mipmap.dot_normal);
            layoutDots.addView(iv);
            mDots[i] = iv;
        }
        mDots[0].setImageResource(R.mipmap.dot_selected);
    }


    /**
     *         初始化ViewPager
     * */
    private void initViewPager() {

        mImageViews = new ImageView[2][];
        mImageViews[0] = new ImageView[pics.size()];
        mImageViews[1] = new ImageView[pics.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            for (int j = 0; j < mImageViews[i].length; j++) {
                ImageView iv = new ImageView(getApplicationContext());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
//                imageLoader.displayImage(pics.get(j), iv, options);
                Glide.with(BussRecommendActivity.this)
                        .load(pics.get(j))
                        .placeholder(R.mipmap.zhanweitu)
                        .error(R.mipmap.zhanweitu)
                        .centerCrop()
                        .crossFade()
                        .into(iv);
                mImageViews[i][j] = iv;
            }
        }

        mViewPagerAdp = new ViewPagerAdapter(getApplicationContext(),mImageViews, pics);
        mViewpager.setAdapter(mViewPagerAdp);
        mViewpager.setCurrentItem(pics.size() * 50);
        mHandler.sendEmptyMessageDelayed(AUTO, delay);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {

            switch (msg.what) {
                case AUTO:
                    int index = mViewpager.getCurrentItem();
                    mViewpager.setCurrentItem(index + 1);
                    mHandler.sendEmptyMessageDelayed(AUTO, delay);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position % pics.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置ViewPager当前的底部小点
     * */
    private void setCurrentDot(int currentPosition) {
        for (int i = 0; i < mDots.length; i++) {
            if (i == currentPosition) {
                mDots[i].setImageResource(R.mipmap.dot_selected);
            } else {
                mDots[i].setImageResource(R.mipmap.dot_normal);
            }
        }
    }

//    class ViewPagerAdapter extends PagerAdapter {
//
//        private ImageView[][] mImageViews;
//
//        private int[] mImageRes;
//
//        public ViewPagerAdapter(ImageView[][] imageViews, int[] imageRes) {
//            this.mImageViews = imageViews;
//            this.mImageRes = imageRes;
//        }
//
//        @Override
//        public int getCount() {
//            return Integer.MAX_VALUE;
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            if (pics.size() == 0) {
//                return mImageViews[position / pics.size() % 2][0];
//            } else {
//                ((ViewPager) container).removeView(mImageViews[position
//                        / pics.size() % 2][position % pics.size()]);
//                ((ViewPager) container).addView(mImageViews[position
//                        / pics.size() % 2][position % pics.size()], 0);
//            }
//            return mImageViews[position / pics.size() % 2][position
//                    % pics.size()];
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            if (pics.size() == 0) {
//                ((ViewPager) container).removeView(mImageViews[position
//                        / pics.size() % 2][0]);
//            } else {
//                ((ViewPager) container).removeView(mImageViews[position
//                        / pics.size() % 2][position % pics.size()]);
//            }
//        }
//    }
        /**
         * @param screenWidth 手机屏幕的宽度
         * @param picWidth    原始图片所用分辨率的宽度
         * @param retainValue 保留小数位
         * @return 手机屏幕分辨率与原始图片分辨率的宽度比
         * @author SheXiaoHeng
         */
        public double divideWidth(int screenWidth, int picWidth, int retainValue) {
            BigDecimal screenBD = new BigDecimal(Double.toString(screenWidth));
            BigDecimal picBD = new BigDecimal(Double.toString(picWidth));
            return screenBD.divide(picBD, retainValue, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        }
}
