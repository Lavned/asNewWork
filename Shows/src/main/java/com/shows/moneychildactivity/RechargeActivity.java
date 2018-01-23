package com.shows.moneychildactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.DataCleanManager;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.Utilty;
import com.shows.allactivty.ChooseLoginActivty;
import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 * 充话费的界面
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    EditText ed_phone;
    Button button;
    TextView oneyuan,fivetyyuan,fiveyuan,tenyuan,twentyyuan,thirtyyuan;
    String price;
    String dockPrice;

    ImageView add_moeny_back;

    SharedPreferences mPreferences;
    String phonenum;
    String rechargeResult;
    RechargeHandler rechargeHandler;
    String mymoney;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recharge);
        initPrice();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mymoney = bundle.getString("money");

        rechargeHandler = new RechargeHandler();
        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("telephone", "");
        Log.i("userphone", phonenum);

        //进度条
        progressDialog = new ProgressDialog(this);

        add_moeny_back= (ImageView) findViewById(R.id.add_moeny_back);
        add_moeny_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ed_phone = (EditText) findViewById(R.id.edit_phone);
        button= (Button) findViewById(R.id.chargebut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecharge();
            }
        });

    }

    /**
     * 开始充值
     */
    boolean isclick = false;
    private void startRecharge() {

//                if(isclick == true){
//                    button.setClickable(false);
//                    Toast.makeText(RechargeActivity.this,"正在充值，请稍后！",Toast.LENGTH_SHORT).show();
//                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String end = payToPhone();
                                Log.i("end", end);
                                Message msg =new Message();
                                if(end.contains("true")){
//                                    rechargeResult = initRecharge();
//                                    if(rechargeResult.contains("success")){
                                    msg.arg1 =1 ;
                                    rechargeHandler.sendMessage(msg);
//                                    }
                                }else{
                                    msg.arg2 = 2 ;
                                    rechargeHandler.sendMessage(msg);
                                    Log.i("shibai","失败");
                                }

                                isclick = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    Log.i("xxxx", ed_phone.getText() + "nnn" + price);
//                }

    }


    /**
     *
     */
    class RechargeHandler extends Handler {
        public RechargeHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("MyHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Toast.makeText(RechargeActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                if(msg.arg2 == 2){
                    // 此处可以更新UI
                    Toast.makeText(RechargeActivity.this,"服务器异常！请稍后再试！",Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    /**
     * 扣钱
     */
    String resultData="";
    public String payToPhone() {
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/phoneMoney.do");
            ///
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
            String content = "mtel=" + URLEncoder.encode(phonenum, "UTF-8");
            content += "&ytel=" + URLEncoder.encode(ed_phone.getText().toString(), "UTF-8");
            content += "&price=" + URLEncoder.encode(dockPrice, "UTF-8");
//            content +="&type=" + URLEncoder.encode("a", "UTF-8");
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
                Log.i("chonghuafei",resultData);
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("chonghuafei", resultData);
            } else {
                Log.i("chonghuafei", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }


    /**
     * 选择价格
     */
    private void initPrice() {
        oneyuan = (TextView) findViewById(R.id.oneyuan);
        oneyuan.setOnClickListener(this);
        fiveyuan = (TextView) findViewById(R.id.fiveyuan);
        fiveyuan.setOnClickListener(this);
        tenyuan = (TextView) findViewById(R.id.tenyuan);
        tenyuan.setOnClickListener(this);
        twentyyuan = (TextView) findViewById(R.id.twentyyuan);
        twentyyuan.setOnClickListener(this);
        thirtyyuan = (TextView) findViewById(R.id.thirtyyuan);
        thirtyyuan.setOnClickListener(this);
        fivetyyuan = (TextView) findViewById(R.id.fivetyyuan);
        fivetyyuan.setOnClickListener(this);
    }


    private String initRecharge() {
        String inputEnd = "";
        StringBuilder str = new StringBuilder();//定义变长字符串
        Random random = new Random();
        for (int i = 8; i < 32; i++) {
            str.append(random.nextInt(10));
        }
        String phone = ed_phone.getText().toString();
//                        price = "1";
        String orderid = str.toString();
        String sign = MD5(phone + price + orderid);
        System.out.println("MD5" + sign + "dingdanhao" + orderid);
        String rechargeArga = "phone=" + phone + "&price=" + price + "&orderid=" + str.toString() + "&sign=" + sign;
        String httpUrl = "http://p.apix.cn/apixlife/pay/phone/phone_recharge";
        httpUrl = httpUrl + "?" + rechargeArga;
        Log.i("httpUrl", httpUrl);
        URL url = null;
        try {
            url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apix-key到HTTP header
            connection.setRequestProperty("apix-key", "cc813119ce4140dc60055228d1adecf2");
            connection.setRequestProperty("content-type", "application/json");
            connection.connect();
            InputStream is = connection.getInputStream();
            String inputLine = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((inputLine = reader.readLine()) != null) {
                Log.i("iii", inputLine);
                inputEnd += inputLine + "";
            }
            System.out.println(inputLine + "jieguo");
            reader.close();
            //关闭http连接
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputEnd;
    }

    public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.oneyuan :
                price = "2";
                dockPrice ="2.3";
                break;
            case R.id.fiveyuan :
                price = "5";
                dockPrice ="5.3";
                break;
            case R.id.tenyuan :
                price = "10";
                dockPrice ="10";
                break;
            case R.id.twentyyuan :
                price = "20";
                dockPrice ="20";
                break;
            case R.id.thirtyyuan :
                price = "30";
                dockPrice ="30";
                break;
            case R.id.fivetyyuan :
                price = "50";
                dockPrice ="50";
                break;
        }

        if(!ed_phone.getText().toString().trim().equals("")){
            if(dockPrice!=null&& !dockPrice.equals("")){
                if(Double.parseDouble(mymoney) < Double.parseDouble(dockPrice)){
                    Toast.makeText(RechargeActivity.this, "您的金额不足！快去点广告赚钱吧！", Toast.LENGTH_SHORT).show();
                }else{
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setMessage("确定要给"+ed_phone.getText().toString()+"充值话费"+price+"元吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    DataCleanManager.cleanInternalCache(getApplicationContext());

                                    progressDialog.setMessage("正在充值，请稍后！");
                                    progressDialog.show();
                                    startRecharge();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            } else{
                Toast.makeText(RechargeActivity.this,"请先选择金额",Toast.LENGTH_SHORT).show();
                return;
            }
        } else{
            Toast.makeText(RechargeActivity.this,"请先输入电话号码",Toast.LENGTH_SHORT).show();
            return;
        }

//        Toast.makeText(this,"您选择了"+price+"元",Toast.LENGTH_SHORT).show();
    }
}
