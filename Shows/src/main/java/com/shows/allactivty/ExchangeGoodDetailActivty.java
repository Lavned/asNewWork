package com.shows.allactivty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ExchangeGoodDetailActivty extends BaseActivity {

    RelativeLayout add_address_re;
    TextView confirmationexchange,tx_goods_get_address;
    ImageView exchangere_back;
    SharedPreferences mPreferences,pPreferences;
    String phonenum;
    String aid,address;
    String goodId;

    //商品信息
    TextView good_price,good_sumprice,good_name,good_num;
    EditText good_remark;

    //解析相关
    List<Map<String, String>> goodUrlsLists ;
    List<Map<String, String>> goodUrls ;
    GoodHandler goodhandler;
    JSONObject json;
    private  String buyResult;

   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_good_detail_activty);


        pPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = pPreferences.getString("telephone", "");
        Log.i("userphone", phonenum);

        //获取新增的地址
        mPreferences =getSharedPreferences("address_pref", MODE_PRIVATE);
        aid = mPreferences.getString("id", "");
        address  = mPreferences.getString("address", "");

        Log.i("addressid", address);
        Log.i("aid", aid);

        progressDialog = new ProgressDialog(this);

        //获取商品ID
        Intent inten = getIntent();
        if(inten!=null){
            Bundle bundle = inten.getExtras();
            if(bundle !=null){
                Log.i("gid",bundle.getString("gid"));
                goodId = bundle.getString("gid");
                SharedPreferences uid_preferences = getSharedPreferences(
                        "address_pref", MODE_PRIVATE);
                SharedPreferences.Editor uid_editor = uid_preferences.edit();
                uid_editor.putString("goodid", goodId);
                uid_editor.commit();
            }
        }



        goodhandler = new GoodHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                goodUrls = viewSetText();
                Message msg = new Message();
                if(goodUrls.size() == 0){
                    msg.arg1 = 1 ;
                }
                msg.arg2 = 1 ;
                goodhandler.sendMessage(msg);
            }
        }).start();
        initView();
    }


    class GoodHandler extends Handler {
        public GoodHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("ggHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg2 ==1){
                    // 此处可以更新UI
                    good_price.setText("￥"+goodUrls.get(0).get("price").toString());
//                    good_num.setText(goodUrls.get(0).get("sum").toString()+"1件");
                    good_num.setText("1件");
                    good_name.setText(goodUrls.get(0).get("name").toString());
                    good_sumprice.setText("￥"+goodUrls.get(0).get("price").toString());
//                    good_remark.setText(goodUrls.get(0).get("writer").toString());
                }
                if(msg.arg1 ==1 ){
                    Toast.makeText(ExchangeGoodDetailActivty.this,"服务异常，请稍后再试！",Toast.LENGTH_SHORT).show();
                }

                if(msg.what ==1 ){
//                    progressDialog.dismiss();
                    finish();
                    Toast.makeText(ExchangeGoodDetailActivty.this,"兑换成功！",Toast.LENGTH_SHORT).show();
//                    finish();
                }else if(msg.what ==2){
                    Toast.makeText(ExchangeGoodDetailActivty.this,"兑换失败！",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    /**
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = initGoodInfo();
        goodUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("good");
            params = new HashMap<String, String>();
            params.put("ID", jsonArray1.getJSONObject(0).getString("id"));
            params.put("price", jsonArray1.getJSONObject(0).getString("price"));
            params.put("sum", jsonArray1.getJSONObject(0).getString("sum"));
            params.put("name", jsonArray1.getJSONObject(0).getString("name"));
            goodUrlsLists.add(params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  goodUrlsLists;
    }

    /***
     *商品信息接口
     */
    private String  initGoodInfo() {

        String argname="ID";
        String requestUrl= HttpAddress.ADDRESSHTTP+"/good/getGoodObject.do";
        String result = HttpUtils.postHttp2(requestUrl, argname, mPreferences.getString("goodid", ""));
        Log.i("result", result);
        return  result;
    }

    /**
     * 界面初始化
     */
    private void initView() {
        good_remark = (EditText) findViewById(R.id.good_remark);
        good_name = (TextView) findViewById(R.id.good_name);
        good_price = (TextView) findViewById(R.id.good_price);
        good_sumprice = (TextView) findViewById(R.id.good_sumprice);
        good_num = (TextView) findViewById(R.id.good_num);

        exchangere_back = (ImageView) findViewById(R.id.exchangere_back);
        exchangere_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmationexchange = (TextView) findViewById(R.id.confirmationexchange);
        confirmationexchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tx_goods_get_address.getText().toString().trim().equals("")){
                    Toast.makeText(ExchangeGoodDetailActivty.this,"亲，请先设置详细的收货地址哦！",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    progressDialog.setMessage("正在加载...");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg= new Message();
                                Thread.sleep(1000);
                                buyResult = initBuyGoog();
                                if(buyResult.contains("true")){
                                    msg.what = 1;
                                }else{
                                    msg.what =2;
                                }
                                goodhandler.sendMessage(msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
        });
        tx_goods_get_address = (TextView) findViewById(R.id.tx_goods_get_address);
//        tx_goods_get_address.setText("甘肃省兰州市"+address);

        add_address_re = (RelativeLayout) findViewById(R.id.add_address_re);
        add_address_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExchangeGoodDetailActivty.this, AddressListViewActivty.class));
            }
        });
    }

    private  String initBuyGoog(){
        String resultData ="";
        URL url = null;
        String  BOUNDARY =  UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/good/purchase.do");
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
            String content = "tel=" + URLEncoder.encode(phonenum, "UTF-8");
            content +="&goodID=" + URLEncoder.encode(mPreferences.getString("goodid",""), "UTF-8");
            content +="&number=" + URLEncoder.encode("1", "UTF-8");
            content +="&remark=" + URLEncoder.encode(good_remark.getText().toString().trim(), "UTF-8");
            content +="&addressid=" + URLEncoder.encode(aid, "UTF-8");
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
                Log.i("resultData",resultData);
            }else{
                Log.i("resultData","读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }


    @Override
    protected void onResume() {
        mPreferences =getSharedPreferences("address_pref", MODE_PRIVATE);
        address = mPreferences.getString("address", "");
        tx_goods_get_address.setText(address);
        super.onResume();

    }
}
