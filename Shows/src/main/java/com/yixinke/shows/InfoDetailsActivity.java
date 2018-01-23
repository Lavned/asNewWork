package com.yixinke.shows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.ViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.allactivty.SelectPicPopupWindow;
import com.shows.getmoney.hanzicode.CodePassActivity;
import com.shows.view.BaseActivity;
import com.shows.view.MyGridView;
import com.shows.zxing.test.ZxingMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public class InfoDetailsActivity extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {

    TextView tv_title,tv_detail,tv_tel,tv_address,tv_traffic,tv_http_address,tv_text_detail;
    ImageView iv_icon;
    private int width;
    private int newWidth;
    private int padding;
    private MyGridView images_list_urls;

    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    //图片轮播相关
    private ViewPager mViewpager;
    private ViewPagerAdapter mViewPagerAdp;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private LinearLayout layoutDots;
    private final int AUTO = 0;
    private final long delay = 3 * 1000;
    // 小图标
    ImageView im_telephone,im_message,im_back,im_savetel;

    Intent intent;


    SharedPreferences mPreferences;
    //收藏图标
    TextView collection;
    TextView shoucangtips;

    TextView tv_lingqu;

    private String codes;


    private JSONObject json;
    ArrayList<String> pics = new ArrayList<>();
    ArrayList<String> imagesUrl = new ArrayList<>();
    String id;
    String cid;
    String inputLine = "";
    String booleanColl;
    List<Map<String, String>> isConn;
    List<Map<String, String>> CollJsonLists;

    String content,phone;
    String MoneyState;

    //更新UI
    MyHandler myHandler;

    GetHandler getHandler;

    //是否收藏
    String iscollectionAdv;
    Bundle bundle;
    private ScrollView scrollview_detail;

    private  ImageView im_good_shoper;

    private int widths,height;
    private TextView main_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_detail);

        scrollview_detail = (ScrollView) findViewById(R.id.scrollview_detail);
        scrollview_detail.smoothScrollTo(20, 20);
        WindowManager wm = (WindowManager) InfoDetailsActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        widths = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        /**
         * 初始化
         */
        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        cid = mPreferences.getString("uid", "");
        phone = mPreferences.getString("telephone", "");
        Log.i("uid", cid+".."+phone);

        try{
            initAllView();
        }catch (Exception e){
            e.printStackTrace();
        }
        //不在主线程更新UI
        myHandler = new MyHandler();

        getHandler = new GetHandler();
        initInfoPager();

        new Thread(new Runnable() {
            @Override
            public void run() {
                iscollectionAdv = isCollection();
                Message msg = new Message();
                msg.what =1;
                myHandler.sendMessage(msg);
            }
        }).start();

        initCollection();

//        main_title = (TextView) findViewById(R.id.main_title);
//        main_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(InfoDetailsActivity.this,CodePassActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("SHOUCANG", "SHOUCANG......");
            try{
                if(msg.what ==1){
                    // 此处可以更新UI
                   String  mainIsColls  = iscollectionAdv;
                    Log.i("SHOUCANG",mainIsColls);
                    if (mainIsColls.contains("false")) {
                        collection.setBackgroundResource(R.mipmap.shoucangweixuan);
                        shoucangtips.setText("收藏该广告");
                    }else {
                        collection.setBackgroundResource(R.mipmap.shoucangxuanding);
                        shoucangtips.setText("已收藏");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class GetHandler extends Handler {
        public GetHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("mymymy", "mymymy......");
            try{
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Log.i("mymymys",MoneyState);
                    if (MoneyState.contains("false")) {
                        Toast.makeText(InfoDetailsActivity.this,"你已经领过了",Toast.LENGTH_SHORT).show();
                    }else if (MoneyState.contains("true")) {
                        Toast.makeText(InfoDetailsActivity.this,"领取0.05元",Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    String resultData="";
    private String isGetMoney() {
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/mrecord/advByGetMoney.do");
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
            String content = "tel=" + URLEncoder.encode(phone, "UTF-8");
            content += "&aid=" + URLEncoder.encode(id, "UTF-8");
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
                Log.i("hhh",resultData);
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("hhhh", resultData);
            } else {
                Log.i("hhh", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }



    /**
     *初始化收藏
     */
    boolean isClick = false;
    private void initCollection() {
        collection = (TextView) findViewById(R.id.shoucang);
        shoucangtips = (TextView) findViewById(R.id.shoucangtips);
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(isClick == false){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String  mainIsColls  = iscollectionAdv;
                                Log.i("iscolls", iscollectionAdv);
                                if (mainIsColls.contains("false")) {
                                    CollectionCare();
                                }
                            }
                        }).start();
                        collection.setBackgroundResource(R.mipmap.shoucangxuanding);
                        shoucangtips.setText("已收藏");
                        isClick =true;
                    }else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                unCollection();
                            }
                        }).start();
                        collection.setBackgroundResource(R.mipmap.shoucangweixuan);
                        shoucangtips.setText("收藏该广告");
                        isClick = false;
                        //unCollection
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 收藏
     */
    public String CollectionCare(){
        Log.i("idididid",id+"dd"+cid);
        URL localURL = null;
        StringBuffer resultBuffer = null;
        try {
            localURL = new URL(HttpAddress.ADDRESSHTTP+"/advert/advertCare.do?aid="+id+"&cid="+cid);
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            resultBuffer = new StringBuffer();
            if (httpURLConnection.getResponseCode() >= 500) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());}
            try {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                while ((inputLine = reader.readLine()) != null) {
                    resultBuffer.append(inputLine);
                    Log.i("CollectionCare",resultBuffer.toString());
                }
            } finally {
                if (reader != null) {
                    reader.close();}
                if (inputStreamReader != null) {
                    inputStreamReader.close();}
                if (inputStream != null) {
                    inputStream.close();}
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBuffer.toString();

    }

    /**
     *  取消收藏
     */

    public String unCollection(){
        URL localURL = null;
        StringBuffer resultBuffer = null;
        try {
            localURL = new URL(HttpAddress.ADDRESSHTTP+"/advert//advertCel.do?aid="+id+"&cid="+cid);
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            resultBuffer = new StringBuffer();
            if (httpURLConnection.getResponseCode() >= 500) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());}
            try {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                while ((inputLine = reader.readLine()) != null) {
                    resultBuffer.append(inputLine);
                    Log.i("Collectioncel",resultBuffer.toString());
                }
            } finally {
                if (reader != null) {
                    reader.close();}
                if (inputStreamReader != null) {
                    inputStreamReader.close();}
                if (inputStream != null) {
                    inputStream.close();}
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Collectioncel",resultBuffer.toString());
        return resultBuffer.toString();
    }

    //初始化轮播图片
    private void initInfoPager() {
        width = getResources().getDisplayMetrics().widthPixels;
        newWidth = (int) (divideWidth(width, 1080, 6) * 17);
        padding = (int) (divideWidth(width, 1080, 6) * 9);
        // registration view
        mViewpager = (ViewPager) findViewById(R.id.ads_viewPager);
        layoutDots = (LinearLayout) findViewById(R.id.dotLayout);
        // set listener
        mViewpager.setOnPageChangeListener(this);
        initDots();
        initViewPager();
    }


    /**
     * 初始化
     */

    private void initAllView() {
        pics.clear();
        Asynphoto();
        Intent intent = getIntent();
        bundle = intent.getExtras();
        String title = bundle.getString("title");
        String header = bundle.getString("header");
        id =  bundle.getString("id");
        Log.i("ididididi",id+"");
        pics =  bundle.getStringArrayList("picsss");

        imagesUrl = bundle.getStringArrayList("imagesurl");

        iv_icon = (ImageView) findViewById(R.id.info_image);
        tv_title = (TextView) findViewById(R.id.tv_name);
        tv_detail = (TextView) findViewById(R.id.tv_logo);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_traffic = (TextView) findViewById(R.id.tv_traffic);
        tv_tel = (TextView) findViewById(R.id.tv_telephone);
        tv_http_address = (TextView) findViewById(R.id.tv_http_address);
        tv_text_detail = (TextView) findViewById(R.id.tv_text_detail);
        images_list_urls = (MyGridView) findViewById(R.id.images_urls);

//
//        AbsListView.LayoutParams lp1s = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.MATCH_PARENT);
////        lp1s.width=0;
////        lp1s.height =0;
//        images_list_urls.setLayoutParams(lp1s);
        images_list_urls.setAdapter(new ImagesAdapter());

        tv_title.setText(title);
        tv_detail.setText(bundle.getString("content"));
        imageLoader.displayImage(header, iv_icon, options);
        tv_tel.setText(bundle.getString("tel"));
        tv_address.setText(bundle.getString("address"));
        tv_traffic.setText(bundle.getString("route"));
        tv_http_address.setText(bundle.getString("httpaddress"));
        tv_text_detail.setText(bundle.getString("introduce"));


        im_message = (ImageView) findViewById(R.id.im_message);
        im_telephone = (ImageView) findViewById(R.id.im_telephone);
        im_savetel = (ImageView) findViewById(R.id.im_savetel);
        im_savetel.setOnClickListener(this);
        im_message.setOnClickListener(this);
        im_telephone.setOnClickListener(this);
        im_back = (ImageView) findViewById(R.id.title_back);
        im_back.setOnClickListener(this);
        tv_lingqu = (TextView) findViewById(R.id.tv_lingqu);
        im_good_shoper = (ImageView) findViewById(R.id.im_good_shoper);
        Log.i("codecode",bundle.getString("code")+"code");
        codes  =bundle.getString("code");
        Glide.with(InfoDetailsActivity.this)
                .load(HttpAddress.ADDRESSHTTP + codes)
                .placeholder(R.mipmap.zhanweitu)
                .error(R.mipmap.zhanweitu)
                .centerCrop()
                .crossFade()
                .into(im_good_shoper);
        im_good_shoper.setOnClickListener(this);

        tv_http_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_http_address.getText().toString().contains("暂无网址")){
                    return;
                }else{
                    Uri uri = Uri.parse(tv_http_address.getText().toString());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }

            }
        });

        tv_lingqu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MoneyState = isGetMoney();
                        Log.i("MoneyState",MoneyState);
                        Message msg = new Message();
                        msg.arg1 = 1;
                        getHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }



    //baseAdpater适配器
    public class ImagesAdapter extends BaseAdapter {
//        int index;
        public ImagesAdapter(){
//            this.index= index;
        }
        @Override
        public int getCount() {
            return imagesUrl.size();
        }

        @Override
        public Object getItem(int position) {
            return imagesUrl.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = LayoutInflater.from(InfoDetailsActivity.this).inflate(R.layout.shows_grideview_item, null);
            }

            imageView = (ImageView) convertView.findViewById(R.id.re_itemImage);

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams lp1s = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp1s.width=widths;

            double ss = widths * 0.7;
            int heights =  (int) ss;
            lp1s.height = heights;
            imageView.setLayoutParams(lp1s);
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            if(imagesUrl.get(position)!= null){
                Log.i("shuliang",imagesUrl.get(position)+"h");
                imageLoader.displayImage(imagesUrl.get(position), imageView, options);
            }
            return convertView;
        }
    }


//
//    /**
//     * 获取是否收藏状态
//     */
//    public String getMoneyState(String content){
//        String moneystate="";
//        try {
//            content = "tel=" + URLEncoder.encode(phone, "UTF-8");
//            content += "&type=" + URLEncoder.encode("b", "UTF-8");
//            content += "&aid=" + URLEncoder.encode(id, "UTF-8");
//            moneystate = HttpUtils.isLingqu(content);
//            Log.i("moneystate",id+"id"+phone+"..");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return moneystate;
//    }
//

    /**
     * 获取是否收藏状态
     */
    private String isCollection() {
        URL localURL = null;
        StringBuffer resultBuffer = null;
        try {
            localURL = new URL(HttpAddress.ADDRESSHTTP+"/advert/judgeCare.do?aid="+id+"&cid="+cid);
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            resultBuffer = new StringBuffer();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());}
            try {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                while ((inputLine = reader.readLine()) != null) {
                    resultBuffer.append(inputLine);
                    Log.i("iscoll",resultBuffer.toString());
                }
            } finally {
                if (reader != null) {
                    reader.close();}
                if (inputStreamReader != null) {
                    inputStreamReader.close();}
                if (inputStream != null) {
                    inputStream.close();}
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("resultBuffer.toString()",resultBuffer.toString());
        return resultBuffer.toString();
    }




    public List<Map<String, String>> getBoolean(){
        String respon = isCollection();
        Log.i("shou",respon);
        isConn = new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
                params = new HashMap<String, String>();
                booleanColl = json.getString("sign");
                Log.i("booleanColl",booleanColl);
                params.put("booleanColl", booleanColl);
                isConn.add(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  isConn;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.im_good_shoper :
                if(ClickUtils.isFastDoubleClick()){
                    return;
                }else {
                    Intent intents = new Intent(new Intent(InfoDetailsActivity.this, ZxingMainActivity.class));
                    Bundle bundles = new Bundle();
                    bundles.putString("codes",HttpAddress.ADDRESSHTTP+codes);
                    intents.putExtras(bundles);
                    startActivity(intents);
                }

                break;
            case R.id.im_savetel :
                Intent it = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
                        Uri.parse("content://com.android.contacts"), "contacts"));
                it.setType("vnd.android.cursor.dir/person");

            // 联系人姓名
                it.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, "");
            // 公司
                it.putExtra(android.provider.ContactsContract.Intents.Insert.COMPANY,
                        bundle.getString("title"));
            // email
//                it.putExtra(android.provider.ContactsContract.Intents.Insert.EMAIL,
//                        "123456@qq.com");
            // 手机号码
            // 手机号码
                it.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,
                         bundle.getString("tel"));
            // 单位电话
//                it.putExtra(
//                        android.provider.ContactsContract.Intents.Insert.SECONDARY_PHONE,
//                        "18600001111");
            // 住宅电话
//                it.putExtra(
//                        android.provider.ContactsContract.Intents.Insert.TERTIARY_PHONE,
//                        "010-7654321");
                // 备注信息
                it.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE,
                        "名片");
                startActivity(it);
                break;
            case R.id.im_telephone :
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bundle.getString("tel")));
                startActivity(intent);
//                Intent intent=new Intent(Intent.ACTION_EDIT,Uri.parse("content://com.android.contacts/contacts/"+"1"));
//                startActivity(intent);
                break;
            case R.id.im_message :
                Uri smsToUri = Uri.parse("smsto:"+ bundle.getString("tel"));
                Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );
                mIntent.putExtra("sms_body", "");
                startActivity( mIntent );
                break;
            case R.id.title_back :
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
                imageLoader.displayImage(pics.get(j), iv, options);
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
    public static double divideWidth(int screenWidth, int picWidth, int retainValue) {
        BigDecimal screenBD = new BigDecimal(Double.toString(screenWidth));
        BigDecimal picBD = new BigDecimal(Double.toString(picWidth));
        return screenBD.divide(picBD, retainValue, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

}
