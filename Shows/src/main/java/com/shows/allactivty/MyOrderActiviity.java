package com.shows.allactivty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.CacheUtils;
import com.com.shows.utils.DataCleanManager;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderActiviity extends BaseActivity {

    //控件初始化
    ListView order_listview;
    SharedPreferences mPreferences;
    String userPhone;

    //handler
    OrderHandler oderHandler;

    //解析相关
    List<Map<String, String>> orderUrlsLists ;
    List<Map<String, String>> orderUrls ;
    JSONObject json;

    //listview
    ImageView order_head,order_back;
    TextView order_name,del_order;
    TextView order_time;
    TextView order_price;
    TextView order_state;
    LinearLayout order_click;

    private ProgressDialog progressDialog;

    ListViewAdapter listLv;


    //图片加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_order_activiity);

        listLv = new ListViewAdapter();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MyOrderActiviity.this));
        ImageLoadInfo();

//加载进度条
        progressDialog = new ProgressDialog(this);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        Log.i("userpnheo", userPhone);

        order_listview = (ListView) findViewById(R.id.order_listview);

        oderHandler = new OrderHandler();

        order_back = (ImageView) findViewById(R.id.order_back);
        order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                orderUrls = viewOrderSetText();
                Message msg = new Message();
                if(orderUrls.size() == 0){
                    msg.arg1 = 1;
                }
                msg.arg2 = 1;
                oderHandler.sendMessage(msg);
            }
        }).start();

    }


    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(MyOrderActiviity.this));

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
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewOrderSetText(){
        String respon = getMyOrderInfo();
        orderUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            if(respon.contains("false")){
                Message mess = new Message();
                mess.what =3 ;
                oderHandler.sendMessage(mess);
            }
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("GoodbyAddressbyPH");
            for (int i = 0 ; i < jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("id", jsonArray1.getJSONObject(i).getString("id"));
                params.put("head", HttpAddress.ADDRESSHTTP+jsonArray1.getJSONObject(i).getString("head"));
                params.put("name", jsonArray1.getJSONObject(i).getString("name"));
                params.put("price", jsonArray1.getJSONObject(i).getString("price"));
                params.put("remark", jsonArray1.getJSONObject(i).getString("remark"));
                params.put("state", jsonArray1.getJSONObject(i).getString("state"));
                params.put("time", jsonArray1.getJSONObject(i).getString("time"));
                params.put("addresss", jsonArray1.getJSONObject(i).getString("addresss"));
                params.put("recipientstel", jsonArray1.getJSONObject(i).getString("recipientstel"));
                params.put("username", jsonArray1.getJSONObject(i).getString("username"));
                orderUrlsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  orderUrlsLists;
    }


    /**
     * 异步更新UI
     */
    class OrderHandler extends Handler {
        public OrderHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("orderHandler", "handleMessage......");
            try{
                if(msg.arg2 ==1){
                    progressDialog.dismiss();
                    order_listview.setAdapter(listLv);
                }
                if(msg.what == 5 ){
                    progressDialog.dismiss();
                    Toast.makeText(MyOrderActiviity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                    listLv.notifyDataSetChanged();
                    order_listview.setAdapter(listLv);
                }
                if(msg.arg1 ==1 ){
                    progressDialog.dismiss();
                    TextView tv= (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("还没历史订单!");
                }
                if(msg.what == 3 ){
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
     *删除功能
     * @param orderId
     * @return
     */
    private String deleteOrder(String orderId){
        String requestUrls = HttpAddress.ADDRESSHTTP +  "/purchasehistory/delOrder.do";
        String ends = HttpUtils.postHttp2(requestUrls,"orderId",orderId);
        Log.i("endsend", ends + "ends");
        if(ends.contains("true")){
            Message  msg = new Message();
            msg.what = 1;
            oderHandler.sendMessage(msg);
        }
        return  ends;
    }


    /**
     * 数据适配器
     */
    class ListViewAdapter extends BaseAdapter {
        public ListViewAdapter() {
        }

        @Override
        public int getCount() {
            return orderUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return orderUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrderActiviity.this).inflate(R.layout.my_order_lv_item, null);
            }

            order_name = (TextView) convertView.findViewById(R.id.order_name);
            order_time = (TextView) convertView .findViewById(R.id.order_stime);
            order_price = (TextView) convertView.findViewById(R.id.order_price);
            order_state = (TextView) convertView.findViewById(R.id.order_state);
            order_head = (ImageView) convertView.findViewById(R.id.order_head);
            order_click = (LinearLayout) convertView.findViewById(R.id.order_click);
            del_order = (TextView) convertView.findViewById(R.id.del_order);
            del_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MyOrderActiviity.this)
                            .setMessage("该笔交易删除后无法恢复，请谨慎操作！")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    DataCleanManager.cleanInternalCache(getApplicationContext());
//                                    final ProgressDialog progressDialog = new ProgressDialog(MyOrderActiviity.this);
                                    progressDialog.setMessage("正在删除...");
                                    progressDialog.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            deleteOrder(orderUrls.get(position).get("id"));
                                            orderUrls = viewOrderSetText();
                                            Message mmm = new Message();
                                            mmm.what = 5;
                                            oderHandler.sendMessage(mmm);
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();

                }
            });
            order_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(MyOrderActiviity.this,OrderInfoDetailActivity.class);
                    Bundle bunele = new Bundle();
                    bunele.putString("name", orderUrls.get(position).get("name"));
                    bunele.putString("price", orderUrls.get(position).get("price"));
                    bunele.putString("address",orderUrls.get(position).get("addresss"));
                    bunele.putString("recipientstel",orderUrls.get(position).get("recipientstel"));
                    bunele.putString("username",orderUrls.get(position).get("username"));
                    bunele.putString("remark",orderUrls.get(position).get("remark"));
                    bunele.putString("state",orderUrls.get(position).get("state"));
                    bunele.putString("gid",orderUrls.get(position).get("id"));
                    intent.putExtras(bunele);
                    startActivity(intent);
                }
            });

            final Map<String, String> map = orderUrls.get(position);
            order_time.setText(map.get("time"));
            order_price.setText("￥"+map.get("price"));
            order_state.setText(map.get("state"));
            order_name.setText(map.get("name"));
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            imageLoader.displayImage(map.get("head"), order_head, options);
//
//            order_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                }
//            });

            return convertView;
        }
    }

    /**
     * 获取我的订单网络数据
     * @return
     */
    private String  getMyOrderInfo(){
        String httpUrl= HttpAddress.ADDRESSHTTP+"/purchasehistory/getpurchase.do";
        String requestStr = HttpUtils.postHttp(httpUrl,userPhone);
        Log.i("requestStr",requestStr+"\n");
        return requestStr;
    }
}
