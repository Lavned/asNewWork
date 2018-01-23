package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.com.shows.utils.StringFormatUtil;
import com.shows.bean.GoodDeatail;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitedActivity extends BaseActivity {

    private ProgressDialog progressDialog;

    ImageView back;
    ListView info_list;
    TextView info_detail,info_title,info_price,info_likenum,info_look_detail;
    private ProgressBar mynum_pro;
    TextView title_name;
    ImageView info_head,lim_image_icons_001,lim_image_icons_002;

    //解析相关
//    List<Map<String, String>> limitUrlsLists ;
//    List<Map<String, String>> limitUrls ;
    LimitHandler limithandler;
    JSONObject json;
    private  String buyResult;
    private  List<GoodDeatail> cfLists;
    List<GoodDeatail> cfUrlsLists;

    //异步加载图片
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shoper_info);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        //进度条
        progressDialog = new ProgressDialog(this);
        ImageLoadInfo();

        back  = (ImageView) findViewById(R.id.shop_info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("秀秀夺宝");

        info_list= (ListView) findViewById(R.id.shop_info_listview);

        limithandler = new LimitHandler();


    }

    /**
     * 请求数据
     */
    private void getData() {
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

                limithandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        getData();
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
            JSONArray jsonArray1  =  json.getJSONArray("Goods");
            for (int i =0;i<jsonArray1.length();i++){
                goodDeatail = new GoodDeatail();
                goodDeatail.setId(jsonArray1.getJSONObject(i).getString("id"));
                goodDeatail.setName(jsonArray1.getJSONObject(i).getString("name"));
                goodDeatail.setContent(jsonArray1.getJSONObject(i).getString("content"));
                goodDeatail.setHeader(jsonArray1.getJSONObject(i).getString("head"));
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
                cfUrlsLists.add(goodDeatail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  cfUrlsLists;
    }


    private String initGoods() {
        String requestUrl= HttpAddress.ADDRESSHTTP+"/CrowdFunding/getAllBabyGood.do";
        String resultStr = HttpUtils.getHttp(requestUrl);
        if(resultStr.equals("") || resultStr == null){
            Message mess = new Message();
            mess.what = 1;
            limithandler.sendMessage(mess);
        }
        Log.i("resultStr", resultStr);
        return resultStr;
    }


    /**
     * 更新UI
     */
    class LimitHandler extends Handler {
        public LimitHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("LimitHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    info_list.setAdapter(new MyInfoListAdpapter());
                }
                if(msg.arg1 ==2){
                    // 此处可以更新UI
                    Toast.makeText(LimitedActivity.this, "网络异常，请稍后再试", Toast.LENGTH_SHORT);
                }
                if(msg.arg1 ==2){
                    // 此处可以更新UI
                    Toast.makeText(LimitedActivity.this,"网络异常，请稍后再试",Toast.LENGTH_SHORT);
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
        imageLoader.init(ImageLoaderConfiguration.createDefault(LimitedActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.fzhanweitu)
                .showImageForEmptyUri(R.mipmap.fzhanweitu)
                .showImageOnFail(R.mipmap.fzhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 转精度
     * @param value
     * @param scale
     * @param roundingMode
     * @return
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
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
                convertView = LayoutInflater.from(LimitedActivity.this).inflate(R.layout.limited_listview_item, null);
            }
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
            info_detail.setText("当前的进度为"+round(percent,2,BigDecimal.ROUND_DOWN)+"%");
            info_title.setText(cfLists.get(position).getName());

            if(cfLists.get(position).getPrice().equals("1")){
                info_price.setText("一元夺宝");
            }else if(cfLists.get(position).getPrice().equals("5")){
                info_price.setText("五元夺宝");
            }else if(cfLists.get(position).getPrice().equals("10")){
                info_price.setText("十元夺宝");
            }
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
            imageLoader.displayImage(cfLists.get(position).getHeader(), info_head, options);

            info_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ClickUtils.isFastDoubleClick()) {
                        return;
                    } else {
                        if (Integer.parseInt(cfLists.get(position).getSum()) - Integer.parseInt(cfLists.get(position).getAccom()) == 0) {
                            Toast.makeText(LimitedActivity.this, "已经抢完了。下次早点哦亲！", Toast.LENGTH_SHORT).show();
                        }
                            Intent intent = new Intent();
                            intent.setClass(LimitedActivity.this, CrowdfundingActivity.class);
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

//                            bundle.putFloat("progress", percent);
//                            bundle.putString("surplus",surplus+"");
                            bundle.putStringArrayList("pics", (ArrayList<String>) cfLists.get(position).getPics());
                            bundle.putStringArrayList("images",(ArrayList<String>) cfLists.get(position).getImages());
                            intent.putExtras(bundle);
                            startActivity(intent);
//                            startActivity(new Intent(LimitedActivity.this,CrowdfundingActivity.class));
                    }
                }
            });

            return convertView;
        }
    }
}
