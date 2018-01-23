package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.shows.utils.CircleImageView;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.ViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.bean.GoodDeatail;
import com.shows.view.BaseActivity;
import com.yixinke.shows.InfoDetailsActivity;
import com.yixinke.shows.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrowdfundingActivity extends BaseActivity  implements ViewPager.OnPageChangeListener  {


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

    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();


    private  TextView addaddress_btn;
    private  TextView num_jia,num_jian,cf_rightbuy;
    private EditText num_edit;
    private Bundle bundle;
    private TextView tv_state,tv_cf_name,tv_cf_detail,tv_cf_sum,tv_cf_surplus;
    private TextView tv_cf_luckyTime,tv_cf_luckyName,tv_cf_luckyNum,tv_cf_luckyTel;
    private CircleImageView cf_head;
    private ProgressBar cf_progress;
    private RelativeLayout cf_picAndText,cf_allCode;
    private LinearLayout winner_info;
    private TextView nowinner_info;
    private SharedPreferences mPreferences;
    private String userPhone;
    private String cid;
    private String goodId;
    private String goodPrice;
    private cfHandler cfHandler;
    private ProgressDialog progressDialog;

    private  List<GoodDeatail> cfListsInfo;
    private List<GoodDeatail> cfUrlsListsInfo;
    private JSONObject json;
    private List<String> pics;
    private RelativeLayout lijiduobao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crowdfunding);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));



        Intent intent = getIntent();
        bundle = intent.getExtras();

        Asynheadphoto();
        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        cid = mPreferences.getString("uid","");
        Log.i("userpnheo", userPhone);
        cfHandler = new cfHandler();
        progressDialog = new ProgressDialog(this);

        initViews();
        getData();

//        initViewpager(bundle.getStringArrayList("pics"));


    }


    //异步加载图片
    private void Asynheadphoto() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.wawatwo)
                .showImageForEmptyUri(R.mipmap.wawatwo)
                .showImageOnFail(R.mipmap.wawatwo)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    @Override
    protected void onResume() {
//        getData();
        super.onResume();

    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cfListsInfo = viewSetText();
                Message mess = new Message();
                mess.what = 1;
                cfHandler.sendMessage(mess);
            }
        }).start();
    }

    /**
     * 请求
     * @return
     */
    private String initGetById(){
        String requesturl = HttpAddress.ADDRESSHTTP + "/CrowdFunding/getGoodById.do";
        return  HttpUtils.postHttp2(requesturl,"goodId",bundle.getString("ID"));
    }
    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<GoodDeatail> viewSetText(){
        String respon = initGetById();
        cfUrlsListsInfo=new ArrayList<GoodDeatail>();
        GoodDeatail goodDeatail=null;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("goods");
            for (int i =0;i<jsonArray1.length();i++){
                goodDeatail = new GoodDeatail();
                goodDeatail.setId(jsonArray1.getJSONObject(i).getString("id"));
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

                List<String> lucysList=new ArrayList<String>();

                JSONArray jsonArraylucy =  jsonArray1.getJSONObject(i).getJSONArray("luckyCustom");
                for (int j = 0;j < jsonArraylucy.length();j++){
                    String  lucky  =jsonArraylucy.getString(j);
                    lucysList.add(lucky);
                }
                goodDeatail.setLuckyCustom(lucysList);
                goodDeatail.setPics(iamgeList);
                goodDeatail.setImages(urlsList);
                cfUrlsListsInfo.add(goodDeatail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  cfUrlsListsInfo;
    }

    /**
     * 初始化所有的控件
     */
    private void initViews() {
        addaddress_btn= (TextView) findViewById(R.id.addaddress_btn);
        lijiduobao = (RelativeLayout) findViewById(R.id.lijiduobao);
        winner_info = (LinearLayout) findViewById(R.id.cf_winner_info);
        nowinner_info = (TextView) findViewById(R.id.cf_tips);
        tv_cf_luckyTel = (TextView) findViewById(R.id.cf_winner_luky_num);
        tv_cf_luckyName = (TextView) findViewById(R.id.cf_winner_name);
        tv_cf_luckyNum =(TextView)  findViewById(R.id.cf_winner_buy_num);
        tv_cf_luckyTime = (TextView) findViewById(R.id.cf_time);
        cf_head = (CircleImageView) findViewById(R.id.cf_winner_head);

        tv_state = (TextView) findViewById(R.id.cf_state);
        tv_cf_name = (TextView) findViewById(R.id.cf_name);
        tv_cf_detail = (TextView) findViewById(R.id.cf_titlecontent);
        tv_cf_sum = (TextView) findViewById(R.id.cd_sumnum);
        tv_cf_surplus = (TextView) findViewById(R.id.cf_shengyu);
        cf_progress = (ProgressBar) findViewById(R.id.cf_progress);
        cf_picAndText = (RelativeLayout) findViewById(R.id.cf_infopicandtext);
        cf_allCode = (RelativeLayout) findViewById(R.id.cf_allcode);
        cf_picAndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInfoActivity();
            }
        });
        cf_allCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CrowdfundingActivity.this, CFAllCodeActivity.class);
                Bundle bun = new Bundle();
                bun.putString("ID", bundle.getString("ID"));
                intent.putExtras(bun);
                startActivity(intent);
            }
        });

        num_edit = (EditText) findViewById(R.id.cf_editnum);
        num_jia = (TextView) findViewById(R.id.cf_jia);
        num_jian =(TextView)  findViewById(R.id.cf_jian);
        num_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_edit.getText().toString().trim().equals("")){
                    num_edit.setText("1");
                }
                int num = Integer.parseInt(num_edit.getText().toString());
                if(num <= 1){
                    num_edit.setText("1");
                }else{
                    num_edit.setText(num - 1 +"");
                }


            }
        });
        num_jia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_edit.getText().toString().trim().equals("")){
                    num_edit.setText("1");
                }
                int num = Integer.parseInt(num_edit.getText().toString());
                num_edit.setText(num + 1 + "");
            }
        });
        cf_rightbuy = (TextView) findViewById(R.id.cf_rightbuy);
    }

    private void jsonSetView(final List<GoodDeatail> lists) {
        pics = lists.get(0).getPics();
        goodId = lists.get(0).getId();
        tv_cf_name.setText(lists.get(0).getName());
        tv_cf_detail.setText(lists.get(0).getInfor());
        tv_cf_sum.setText("总量：" + lists.get(0).getSum());
        int num =Integer.parseInt(lists.get(0).getSum())-Integer.parseInt(lists.get(0).getAccom());
        tv_cf_surplus.setText("剩余：" + num);
        int j = Integer.parseInt(lists.get(0).getAccom());
        int k =  Integer.parseInt(lists.get(0).getSum());
        final float percent = (((float)j / (float)k))*100;
        cf_progress.setProgress((int) percent);

        if(lists.get(0).getLuckyCustom().size() == 0){
            winner_info.setVisibility(View.GONE);
            nowinner_info.setVisibility(View.VISIBLE);
        }else{
            tv_state.setText("已揭晓");
            winner_info.setVisibility(View.VISIBLE);
            imageLoader.displayImage(lists.get(0).getLuckyCustom().get(0), cf_head, options);
            tv_cf_luckyTime.setText("本期揭晓信息—" + lists.get(0).getLuckyCustom().get(4));
            tv_cf_luckyName.setText("获奖者："+ lists.get(0).getLuckyCustom().get(1));
            tv_cf_luckyNum.setText("本次购买份数：" + lists.get(0).getLuckyCustom().get(2));
            tv_cf_luckyTel.setText("获奖者号码：" + lists.get(0).getLuckyCustom().get(3));
            nowinner_info.setVisibility(View.GONE);
            lijiduobao.setVisibility(View.GONE);
            if(cid.equals(lists.get(0).getLuckyCustom().get(5))){
                addaddress_btn.setVisibility(View.VISIBLE);
                SharedPreferences aPreferences =getSharedPreferences("address_ok", MODE_PRIVATE);
                String addressState = aPreferences.getString("address_state", "");
                String gid = aPreferences.getString("goodId","");
                if(gid.equals(bundle.getString("ID"))){
                    if(addressState.equals("yes")){
                        addaddress_btn.setText("地址添加成功，如需修改请点击！");
                    }
                }
            }
            addaddress_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  Intent ins = new Intent(CrowdfundingActivity.this, CrowFundAddressListViewActivty.class);
                    Bundle bun =  new Bundle();
                    bun.putString("goodId",bundle.getString("ID"));
                    ins.putExtras(bun);
                    startActivity(ins);
                }
            });
        }

        final int synums = Integer.parseInt(cfListsInfo.get(0).getSum()) - Integer.parseInt(cfListsInfo.get(0).getAccom());
        cf_rightbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num_edit.getText().toString().trim().equals("")){
                    num_edit.setText("1");
                }
                if (Integer.parseInt(num_edit.getText().toString()) > synums) {
                    Toast.makeText(CrowdfundingActivity.this, "选择的份数大于剩余份数，请重新选择！", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("请稍后....");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String ends = rightBuy();
                            Message msg = new Message();
                            if (ends.contains("true")) {
                                msg.arg1 = 1;
                            } else {
                                msg.arg1 = 2;
                            }
                            cfHandler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });



    }


    /**
     * 异步更新UI
     */
    class cfHandler extends Handler {
        public cfHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    num_edit.setText("1");
                    Toast.makeText(CrowdfundingActivity.this,"夺宝成功！",Toast.LENGTH_SHORT).show();
                    cf_rightbuy.setText("再夺一次");
                    cf_rightbuy.setTextColor(Color.parseColor("#000000"));
                    cf_rightbuy.setBackgroundResource(R.drawable.searchback);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            cfListsInfo = viewSetText();
                            Message mess = new Message();
                            mess.arg2 = 1;
                            cfHandler.sendMessage(mess);
                        }
                    }).start();
                }
                if(msg.arg1 == 2 ){
                    Toast.makeText(CrowdfundingActivity.this,"夺宝人数太多，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg2 == 1) {
                    jsonSetView(cfListsInfo);
                }
                if(msg.what == 1){
                    jsonSetView(cfListsInfo);
                    initInfoPager();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    //初始化轮播图片
    private void initInfoPager() {
        width = getResources().getDisplayMetrics().widthPixels;
        newWidth = (int) (divideWidth(width, 1080, 6) * 17);
        padding = (int) (divideWidth(width, 1080, 6) * 9);
        // registration view
        mViewpager = (ViewPager) findViewById(R.id.cf_viewpager);
        layoutDots = (LinearLayout) findViewById(R.id.showLayout);
        // set listener
        mViewpager.setOnPageChangeListener(this);
        initDots();
        initViewPager();
    }

    /**
     *         初始化ViewPager的底部小点
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
                Glide.with(CrowdfundingActivity.this)
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


    void clickInfoActivity(){
        Intent intent = new Intent();
        intent.setClass(CrowdfundingActivity.this, ExchangeGoodActivity.class);
        Bundle bun = new Bundle();
        bun.putString("type","cf");
        bun.putString("ID",bundle.getString("ID"));
        bun.putString("name", bundle.getString("name"));
        bun.putString("accom",bundle.getString("accom"));
        bun.putString("content",bundle.getString("content"));
        bun.putString("infor",bundle.getString("infor"));
        bun.putString("price",bundle.getString("price"));
        bun.putString("post", bundle.getString("post"));
        bun.putString("sum",bundle.getString("sum"));
        bun.putStringArrayList("pics", bundle.getStringArrayList("pics"));
        bun.putStringArrayList("images",bundle.getStringArrayList("images"));
        intent.putExtras(bun);
        startActivity(intent);
    }


    /**
     *
     * 立即夺宝
     */
    private String rightBuy() {
        String requestUrl= HttpAddress.ADDRESSHTTP + "/CrowdFunding/oneBuy.do";
        String resultData="";
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            // 设置以POST方式
            urlConn.setRequestMethod("POST");
            // Post 请求不能使用缓存
            urlConn.setUseCaches(false);
            urlConn.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            urlConn.setRequestProperty("Content-type", "text/html");
            urlConn.setRequestProperty("contentType", "utf-8");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            urlConn.connect();
            //DataOutputStream流
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            //要上传的参数
            String content = "mTel=" + URLEncoder.encode(userPhone, "UTF-8");
            content += "&goodId=" + URLEncoder.encode(bundle.getString("ID"), "UTF-8");
            content += "&buyNum=" + URLEncoder.encode(num_edit.getText().toString(), "UTF-8");
            content += "&buyMoney=" + URLEncoder.encode(bundle.getString("price"), "UTF-8");
            content += "&cid=" + URLEncoder.encode(cid,"UTF-8");

            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("gongyongda", resultData);
            } else {
                Log.i("gongyongda", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }





    @Override
    protected void onStop() {
        super.onStop();
    }
}
