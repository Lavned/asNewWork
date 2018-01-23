package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.ViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.bean.GoodDeatail;
import com.shows.view.MyGridView;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeGoodActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    //UI声明
    private ImageView exchangegood_back;
    private TextView goos_exchage;
    private String id;
    private RelativeLayout buyCode;
    private TextView good_name,good_price,good_num,good_mail,good_content,good_choose_type;
    ImageView good_head;
    private MyGridView good_images;

    //viewpager声明图片轮播相关
    private ViewPager mViewpager;
    private ViewPagerAdapter mViewPagerAdp;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private LinearLayout layoutDots;
    private final int AUTO = 0;
    private final long delay = 3 * 1000;
    private int width;
    private int newWidth;
    private int padding;

    private  JSONObject json;
    private GoodHandler goodHandler;
    private List<GoodDeatail> goodList;
    private ProgressDialog progressDialog;

    private List<String> pics;
    private List<String> imageUrls;
    private ScrollView scrollview_good_detail;
    private int widths,height;


    protected ImageLoader imageLoader = ImageLoader.getInstance();
    //异步加载相关
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exchange_good);

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        WindowManager wm = (WindowManager) ExchangeGoodActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        widths = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();


        //获取商品ID
        Intent inten = getIntent();
        Bundle bundle = inten.getExtras();
        id = bundle.getString("ID");
        //初始化UI
        initGoodView();
        Asynphoto();

        scrollview_good_detail = (ScrollView) findViewById(R.id.scrollview_good_detail);
        scrollview_good_detail.smoothScrollTo(20, 20);

//        Log.i("typetype",bundle.getString("type"+"dd"));
        if(bundle.getString("type")!=null){
            if(bundle.getString("type").equals("cf")){
                pics = bundle.getStringArrayList("pics");
                initInfoPager();
                good_name.setText(bundle.getString("name"));
                int num =  Integer.parseInt(bundle.getString("sum")) - Integer.parseInt(bundle.getString("accom"));
                good_num.setText("剩余" +num+"件");
                good_price.setText("￥"+bundle.getString("price"));
                good_mail.setText("邮费："+bundle.getString("post"));
                good_content.setText(bundle.getString("infor"));
                imageUrls = bundle.getStringArrayList("images");
                good_images.setAdapter(new ImagesAdapter());
                goos_exchage.setVisibility(View.GONE);
                buyCode.setVisibility(View.GONE);
            }
            }else{
            progressDialog = new ProgressDialog(this);
            goodHandler = new GoodHandler();
            progressDialog.setMessage("正在加载...");
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = getResult();
                    Log.i("resultresaulty",result+"kkk");
                    goodList = jsonSetText(result);
                    Message msg = new Message();
                    if(goodList.size()==0){
                        msg.arg1 = 2 ;
                    }else{
                        msg.arg1 = 1;
                    }
                    goodHandler.sendMessage(msg);
                }
            }).start();
        }



    }


    //异步加载图片
    private void Asynphoto() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.zhanweitu)
                .showImageForEmptyUri(R.mipmap.zhanweitu)
                .showImageOnFail(R.mipmap.zhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 异步更新
     */
    class GoodHandler extends Handler {
        public GoodHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("SHOUCANG", "SHOUCANG......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Log.i("goodlist",goodList.size()+"jjj"+goodList.get(0).getName());
                    pics = goodList.get(0).getPics();
                    initInfoPager();
                    good_name.setText(goodList.get(0).getName());
                    int num =  Integer.parseInt(goodList.get(0).getSum()) - Integer.parseInt(goodList.get(0).getAccom());
                    good_num.setText("剩余" +num+"件");
                    if(num  == 0){
                        goos_exchage.setText("该商品已售完");
                        goos_exchage.setBackgroundColor(Color.parseColor("#ff0000"));
                        goos_exchage.setClickable(false);
                    }
                    good_price.setText("￥"+goodList.get(0).getPrice());
                    good_mail.setText("邮费："+goodList.get(0).getPost());
                    good_content.setText(goodList.get(0).getInfor());
                    imageUrls = goodList.get(0).getImages();
                    good_images.setAdapter(new ImagesAdapter());
                }
                if(msg.arg1 ==2){
                    Toast.makeText(ExchangeGoodActivity.this,"数据异常！",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    //baseAdpater适配器
    public class ImagesAdapter extends BaseAdapter {
        //        int index;
        public ImagesAdapter(){
//            this.index= index;
        }
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return imageUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = LayoutInflater.from(ExchangeGoodActivity.this).inflate(R.layout.shows_grideview_item, null);
            }
            imageView = (ImageView) convertView.findViewById(R.id.re_itemImage);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams lp1s = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp1s.width=widths;

            double ss = widths * 0.65;
            int heights =  (int) ss;
            lp1s.height = heights;
            imageView.setLayoutParams(lp1s);

            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            if(imageUrls.get(position)!= null){
                imageLoader.displayImage(imageUrls.get(position), imageView, options);
            }
            return convertView;
        }
    }


    /**
     * 获取网络数据
     * @return
     */
    private String getResult(){
        String requestUrl = HttpAddress.ADDRESSHTTP + "/good/getGood.do?id="+id;
        String end = HttpUtils.getHttp(requestUrl);
        return  end;
    }

    /**
     * 解析JSON
     * @param respon
     * @return
     */
    public List<GoodDeatail> jsonSetText(String respon){
        List<GoodDeatail> goodListPic=new ArrayList<GoodDeatail>();
        GoodDeatail goodDeatail=null;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("goods");
            for (int i =0;i<jsonArray1.length();i++){
                goodDeatail = new GoodDeatail();
                goodDeatail.setName(jsonArray1.getJSONObject(i).getString("name"));
                goodDeatail.setContent(jsonArray1.getJSONObject(i).getString("content"));
                goodDeatail.setHeader(HttpAddress.ADDRESSHTTP + jsonArray1.getJSONObject(i).getString("head"));
                goodDeatail.setAccom(jsonArray1.getJSONObject(i).getString("accom"));
                goodDeatail.setDescribes(jsonArray1.getJSONObject(i).getString("content"));
                goodDeatail.setInfor(jsonArray1.getJSONObject(i).getString("infor"));
                goodDeatail.setPost(jsonArray1.getJSONObject(i).getString("post"));
                goodDeatail.setPrice(jsonArray1.getJSONObject(i).getString("price"));
                goodDeatail.setSum(jsonArray1.getJSONObject(i).getString("sum"));

                JSONArray jsonArrayImages =  jsonArray1.getJSONObject(i).getJSONArray("gPics");
                List<String> iamgeList=new ArrayList<String>();
                for (int j = 0;j < jsonArrayImages.length();j++){
                    String  pic  =jsonArrayImages.getString(j);
                    iamgeList.add(HttpAddress.ADDRESSHTTP + pic);
                }
                JSONArray jsonArrayurls =  jsonArray1.getJSONObject(i).getJSONArray("gPicturesContent");
                List<String> urlsList=new ArrayList<String>();
                for (int j = 0;j < jsonArrayurls.length();j++){
                    String  pic  =jsonArrayurls.getString(j);
                    urlsList.add(HttpAddress.ADDRESSHTTP + pic);
                }

                goodDeatail.setPics(iamgeList);
                goodDeatail.setImages(urlsList);
                goodListPic.add(goodDeatail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  goodListPic;
    }


    //初始化轮播图片
    private void initInfoPager() {
        width = getResources().getDisplayMetrics().widthPixels;
        newWidth = (int) (divideWidth(width, 1080, 6) * 17);
        padding = (int) (divideWidth(width, 1080, 6) * 9);
        // registration view
        mViewpager = (ViewPager) findViewById(R.id.good_viewPager);
        layoutDots = (LinearLayout) findViewById(R.id.dotLayout);
        // set listener
        mViewpager.setOnPageChangeListener(this);
        initDots();
        initViewPager();
    }

    /**
     * 兑换控件初始化
     */
    private void initGoodView() {
        good_name = (TextView) findViewById(R.id.tv_good_name);
        good_content = (TextView) findViewById(R.id.tv_good_content);
        good_price = (TextView) findViewById(R.id.tv_good_price);
        good_num = (TextView) findViewById(R.id.tv_good_num);
        good_mail= (TextView) findViewById(R.id.tv_good_mail);
        good_images = (MyGridView) findViewById(R.id.images_urlsss);

        exchangegood_back = (ImageView) findViewById(R.id.exchangegood_back);
        exchangegood_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        goos_exchage = (TextView) findViewById(R.id.goos_exchage);
        goos_exchage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExchangeGoodActivity.this,ExchangeGoodDetailActivty.class);
                Bundle bundle =  new Bundle();
                bundle.putString("gid",id);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        buyCode = (RelativeLayout) findViewById(R.id.tv_good_buy_code);
        buyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExchangeGoodActivity.this,BuyCodeHistoryActivity.class);
                Bundle bundle =  new Bundle();
                bundle.putString("gid", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**初始化ViewPager的底部小点
     *
     * */
    private void initDots() {

        mDots = new ImageView[pics.size()];

        for (int i = 0; i <pics.size(); i++) {
            ImageView iv = new ImageView(this);
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
                Glide.with(ExchangeGoodActivity.this)
                        .load(pics.get(j))
                        .placeholder(R.mipmap.zhanweitu)
                        .error(R.mipmap.zhanweitu)
                        .fitCenter()
                        .crossFade()
                        .dontAnimate()
                        .into(iv);
                mImageViews[i][j] = iv;
            }
        }


        mViewPagerAdp = new ViewPagerAdapter(getApplicationContext(),mImageViews, (ArrayList<String>) pics);
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
        }
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

    /**
     * @author SheXiaoHeng
     * @param screenWidth
     *            手机屏幕的宽度
     * @param picWidth
     *            原始图片所用分辨率的宽度
     * @param retainValue
     *            保留小数位
     * @return 手机屏幕分辨率与原始图片分辨率的宽度比
     *
     * */
    public double divideWidth(int screenWidth, int picWidth, int retainValue) {
        BigDecimal screenBD = new BigDecimal(Double.toString(screenWidth));
        BigDecimal picBD = new BigDecimal(Double.toString(picWidth));
        return screenBD.divide(picBD, retainValue, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
