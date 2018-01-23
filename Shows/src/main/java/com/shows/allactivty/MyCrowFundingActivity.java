package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.bean.GoodDeatail;
import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MyCrowFundingActivity extends BaseActivity {


    private TextView smx_title;
    private ListView mycf_lv;


    private ProgressDialog progressDialog;

    ImageView back;
    TextView info_detail,info_title,info_price,info_likenum,info_look_detail,my_cf_num;
    private ProgressBar mynum_pro;
    TextView title_name;
    ImageView info_head,lim_image_icons_001,lim_image_icons_002;

    MyCFHandler myCFHandler;
    JSONObject json;
    private  String buyResult;
    private List<GoodDeatail> cfLists;
    List<GoodDeatail> cfUrlsLists;

    //异步加载图片
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private SharedPreferences mPreferences;
    String customId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_crow_funding);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        customId = mPreferences.getString("uid", "");
        Log.i("uid", customId);

        smx_title = (TextView) findViewById(R.id.smx_title);
        smx_title.setText("我的夺宝记录");


        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        //进度条
        progressDialog = new ProgressDialog(this);
        ImageLoadInfo();


        mycf_lv= (ListView) findViewById(R.id.mycf_lv);

        myCFHandler = new MyCFHandler();

//        loadingData();

    }

    /**
     * 加载网络数据
     */
    private void loadingData() {
        progressDialog.setMessage("正在加载......");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cfLists = viewSetText();
                Message msg = new Message();
                if(cfLists.size() ==0){
                    msg.arg1 = 2 ;
                }else{
                    msg.arg1 =1 ;
                }

                myCFHandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        loadingData();
        super.onResume();
    }

    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<GoodDeatail> viewSetText(){
        String respon = initGoods();
        cfUrlsLists=new ArrayList<GoodDeatail>();
        GoodDeatail goodDeatail=null;
        try {
            json = new JSONObject(respon);
            if(respon.contains("null")){
                Message msg = new Message();
                msg.what = 2;
                myCFHandler.sendMessage(msg);
            }
            JSONArray jsonArray1  =  json.getJSONArray("CrowdFunding");
            for (int i =0;i<jsonArray1.length();i++){
                goodDeatail = new GoodDeatail();
                goodDeatail.setId(jsonArray1.getJSONObject(i).getString("goodId"));
                goodDeatail.setName(jsonArray1.getJSONObject(i).getString("name"));
                goodDeatail.setContent(jsonArray1.getJSONObject(i).getString("content"));
                goodDeatail.setHeader(HttpAddress.ADDRESSHTTP + jsonArray1.getJSONObject(i).getString("head"));
                goodDeatail.setAccom(jsonArray1.getJSONObject(i).getString("accom"));
                goodDeatail.setDescribes(jsonArray1.getJSONObject(i).getString("content"));
                goodDeatail.setInfor(jsonArray1.getJSONObject(i).getString("infor"));
                goodDeatail.setPost(jsonArray1.getJSONObject(i).getString("post"));
                goodDeatail.setPrice(jsonArray1.getJSONObject(i).getString("price"));
                goodDeatail.setSum(jsonArray1.getJSONObject(i).getString("sum"));
                goodDeatail.setFnum(jsonArray1.getJSONObject(i).getString("fnum"));

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
                cfUrlsLists.add(goodDeatail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  cfUrlsLists;
    }


    private String initGoods() {
        String requestUrl= HttpAddress.ADDRESSHTTP+"/CrowdFunding/myOneBuy.do";
        String resultStr = HttpUtils.postHttp2(requestUrl,"customId",customId);
        Log.i("resultStr", resultStr);
        return resultStr;
    }


    /**
     * 更新UI
     */
    class MyCFHandler extends Handler {
        public MyCFHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("LimitHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    mycf_lv.setAdapter(new MyInfoListAdpapter());
                }
                if(msg.arg1 ==2){
                    // 此处可以更新UI
                    Toast.makeText(MyCrowFundingActivity.this, "网络异常，请稍后再试", Toast.LENGTH_SHORT);
                }
                if(msg.what ==2){
                    progressDialog.dismiss();
                    TextView tv= (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("还没历史订单!");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(MyCrowFundingActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.fzhanweitu)
                .showImageForEmptyUri(R.mipmap.fzhanweitu)
                .showImageOnFail(R.mipmap.fzhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    //
    class MyInfoListAdpapter  extends BaseAdapter {

        /**
         * @param
         */
        public MyInfoListAdpapter() {
        }

        @Override
        public int getCount() {
            return cfUrlsLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cfUrlsLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(MyCrowFundingActivity.this).inflate(R.layout.mycf_listview_item, null);
            }
            my_cf_num = (TextView) convertView.findViewById(R.id.my_cf_num);

            mynum_pro = (ProgressBar) convertView.findViewById(R.id.my_progress);
            info_title = (TextView) convertView.findViewById(R.id.lim_text_title);
            info_detail = (TextView) convertView.findViewById(R.id.lim_text_detail);
            info_price = (TextView) convertView.findViewById(R.id.lim_text_price);
            info_likenum = (TextView) convertView.findViewById(R.id.lim_text_number);
            info_head = (ImageView) convertView.findViewById(R.id.lim_image_icons);
            lim_image_icons_001 =  (ImageView) convertView.findViewById(R.id.lim_image_icons_001);
            lim_image_icons_002 =  (ImageView) convertView.findViewById(R.id.lim_image_icons_002);

            int j = Integer.parseInt(cfLists.get(position).getAccom());
            int k =  Integer.parseInt(cfLists.get(position).getSum());
            final float percent = (((float)j / (float)k))*100;
            mynum_pro.setProgress((int) percent);
            info_detail.setText("当前的进度为" + LimitedActivity.round(percent, 2, BigDecimal.ROUND_DOWN) + "%");
            info_title.setText(cfLists.get(position).getName());
            my_cf_num.setText("本次参与份数："+ cfLists.get(position).getFnum());
//            if(cfLists.get(position).getPrice().equals("1")){
//                info_price.setText("一元夺宝");
//            }else if(cfLists.get(position).getPrice().equals("5")){
//                info_price.setText("五元夺宝");
//            }else if(cfLists.get(position).getPrice().equals("10")){
//                info_price.setText("十元夺宝");
//            }
            info_price.setText("夺宝详情");
            final int surplus = Integer.parseInt(cfLists.get(position).getSum()) - Integer.parseInt(cfLists.get(position).getAccom());
            info_likenum.setText("总筹："+cfLists.get(position).getSum()+"\n"+"剩余："+surplus);
            if(surplus == 0){
                lim_image_icons_001.setVisibility(View.VISIBLE);
                lim_image_icons_002.setVisibility(View.GONE);
            }else{
                lim_image_icons_001.setVisibility(View.GONE);
                lim_image_icons_002.setVisibility(View.VISIBLE);
            }
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            imageLoader.displayImage(HttpAddress.ADDRESSHTTP + cfLists.get(position).getHeader(), info_head, options);

            mycf_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ClickUtils.isFastDoubleClick()) {
                        return;
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(MyCrowFundingActivity.this, CrowdfundingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID",cfLists.get(position).getId());
                        bundle.putString("name", cfLists.get(position).getName());
                        bundle.putString("accom",cfLists.get(position).getAccom());
                        bundle.putString("content",cfLists.get(position).getContent());
                        bundle.putString("infor",cfLists.get(position).getInfor());
                        bundle.putString("price",cfLists.get(position).getPrice());
                        bundle.putString("post",cfLists.get(position).getPost());
                        bundle.putString("sum",cfLists.get(position).getSum());
                        bundle.putString("accom",cfLists.get(position).getAccom());
                        bundle.putStringArrayList("pics", (ArrayList<String>) cfLists.get(position).getPics());
                        bundle.putStringArrayList("images",(ArrayList<String>) cfLists.get(position).getImages());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }
    }


    /**
     * exit this activity
     * @param view
     */
    public void finisThis(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
