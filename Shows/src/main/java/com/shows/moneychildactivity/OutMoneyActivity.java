package com.shows.moneychildactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OutMoneyActivity extends BaseActivity {

    SharedPreferences mPreferences;
    String userPhone ="";
    String type;
    String out_in_opcation;
    String req;
    ListView ls;

    TextView out_title,out_content,out_stime,out_money,out_in_commont_title;
    ImageView out_image;

    JSONObject json;
    List<Map<String, String>> outMoney;
    List<Map<String, String>> MoneyJsonLists;
    ImageView out_money_back;

    //更新UI
    MyHandler myHandler;
    private ProgressDialog progressDialog;
    private TextView deleteout;
    private  String INOUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_out_money);
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("正在加载......");
        progressDialog.show();

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        Log.i("userpnheo", userPhone);

        //不在主线程更新UI
        myHandler = new MyHandler();
        ls = (ListView) findViewById(R.id.outmoney_listview);
        out_in_commont_title = (TextView) findViewById(R.id.out_in_commont_title);
        deleteout = (TextView) findViewById(R.id.deleteout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String opcation=bundle.getString("opcationType");
        if(opcation.equals("in")){
            type ="in";
            req = HttpAddress.ADDRESSHTTP+"/mrecord/msee.do";
            out_in_commont_title.setText("收入明细");
            out_in_opcation = "1";
            INOUT ="in";
        }else if(opcation.equals("out")){
            type ="out";
            req = HttpAddress.ADDRESSHTTP+"/mrecord/msee.do";
            out_in_commont_title.setText("支出明细");
            out_in_opcation = "2";
            INOUT ="out";
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                outMoney = getJsonInfo();
                Log.i("end",outMoney.size()+"xx"+"xxxxxx"+type);
                Message msg = new Message();
                msg.what =1;
                myHandler.sendMessage(msg);
            }
        }).start();

        out_money_back = (ImageView) findViewById(R.id.out_money_back);
        out_money_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String requltEnd = deleteInfo(INOUT);
                        if(requltEnd.contains("true")){
                            Message msg = new Message();
                            msg.arg2 = 3;
                            myHandler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });
    }


    /**
     * 删除信息请求服务
     */
    String resultData = "";
    private String deleteInfo(String inOrOut) {
        Log.i("outMoneyoutMoney",inOrOut+"outMoney");
        URL url = null;
        String  BOUNDARY =  UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/mrecord/hideRecord.do");
            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
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
            String content = "tel=" + URLEncoder.encode(userPhone, "UTF-8");
            content +="&inOrOut=" + URLEncoder.encode(inOrOut, "UTF-8");
            Log.i("inOrOutinOrOut",inOrOut+"inOrOut");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null))
            {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if ( resultData != null ){
                Log.i("rediger",resultData);
            }else{
                Log.i("rediger","读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * handler
     */
    class MyHandler extends android.os.Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                if(msg.what ==1){
                    // 此处可以更新UI
                    progressDialog.dismiss();
                   ls.setAdapter(new ListViewAdpater());
                }
                if(msg.arg2  == 3 ){
                    // 此处可以更新UI
                    progressDialog.dismiss();
                    outMoney.clear();
                    ls.setAdapter(new ListViewAdpater());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<Map<String, String>> getJsonInfo(){
        String  respon =HttpUtils.postHttpMoney(req, userPhone, type);
        Log.i("reposess",respon);
        MoneyJsonLists=new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("mrecord");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("type", jsonArray1.getJSONObject(i).getString("type"));
                params.put("froma", jsonArray1.getJSONObject(i).getString("froma"));
                params.put("stime", jsonArray1.getJSONObject(i).getString("stime"));
                params.put("to", jsonArray1.getJSONObject(i).getString("toa"));
                params.put("money", jsonArray1.getJSONObject(i).getString("money"));
                MoneyJsonLists.add(params);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return  MoneyJsonLists;
    }



    /**
     * 适配器
     */
    class ListViewAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return outMoney.size();
        }

        @Override
        public Object getItem(int position) {
            return outMoney.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(OutMoneyActivity.this).inflate(R.layout.out_in_listview_item, null);
            }
            Map<String, String> map = outMoney.get(position);
            out_title = (TextView) convertView.findViewById(R.id.out_in_title);
            out_content = (TextView) convertView.findViewById(R.id.out_in_detail);
            out_stime = (TextView) convertView.findViewById(R.id.out_in_money_time);
            out_money = (TextView) convertView.findViewById(R.id.out_in_price);
            out_image = (ImageView) convertView.findViewById(R.id.out_in_icon);

            out_stime.setText(map.get("stime"));
            out_money.setText("￥"+map.get("money"));

            if(out_in_opcation.equals("1")){
                if(map.get("type").equals("2")){
                    out_title.setText("点一点");
                    out_content.setText("每日点一点收益");
                    out_image.setImageResource(R.mipmap.dot);
                }else if(map.get("type").equals("c")){
                    out_title.setText("收红包");
                    out_content.setText("来自"+map.get("froma")+"送来的红包");
                    out_image.setImageResource(R.mipmap.present);
                }
            }else if(out_in_opcation.equals("2")){
                if(map.get("type").equals("a")){
                    out_title.setText("充话费");
                    out_content.setText("给手机" + map.get("to")+"充话费支出");
                            out_image.setImageResource(R.mipmap.full);
                }else if(map.get("type").equals("c")){
                    out_title.setText("赠送");
                    out_content.setText("给"+map.get("to")+"送红包");
                    out_image.setImageResource(R.mipmap.present);
                }else if(map.get("type").equals("b")){
                    out_title.setText("兑换支出");
                    out_content.setText("兑换商品："+map.get("to"));
                    out_image.setImageResource(R.mipmap.trade);
                }else if(map.get("type").equals("d")){
                    out_title.setText("爱心公益");
                    out_content.setText("爱心公益");
                    out_image.setImageResource(R.mipmap.juan);
                }else if(map.get("type").equals("f")){
                    out_title.setText("秀秀夺宝");
                    out_content.setText("参与商品："+map.get("to")+"的夺宝活动");
                    out_image.setImageResource(R.mipmap.chou);
            }

            }
            return convertView;
        }
    }
}
