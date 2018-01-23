package com.shows.allactivty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.moneychildactivity.OutMoneyActivity;
import com.shows.moneychildactivity.OutMoneyToYouActivity;
import com.shows.moneychildactivity.RechargeActivity;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 我的钱包
 */
public class MyMoneyActiviity extends BaseActivity {

    TextView recharge,file_recharge,getMoneyInfo,OutMoneyInfo;
    TextView money,TodayMoney,YesTodayMoney;
    ImageView back;

    private  TextView today,yestoday;
    SharedPreferences mPreferences;

    String req;
    String userPhone;

    JSONObject json;
    List<Map<String, String>> outMoney;
    List<Map<String, String>> MoneyJsonLists;

    //更新UI
    MyHandler myHandler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_money_activiity);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        Log.i("userpnheo", userPhone);

        progressDialog = new ProgressDialog(this);


        //不在主线程更新UI
        myHandler = new MyHandler();
        req = HttpAddress.ADDRESSHTTP+"/mrecord/tsee.do";
        money = (TextView) findViewById(R.id.money);
        TodayMoney= (TextView) findViewById(R.id.jinrishouyi);
        YesTodayMoney = (TextView) findViewById(R.id.zuorishouyi);
        today = (TextView) findViewById(R.id.jinrishouyi);
        yestoday = (TextView) findViewById(R.id.zuorishouyi);

        initView();

        progressDialog.setMessage("加载中......");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                outMoney = getJsonInfo();
                Message msg = new Message();
                msg.what =1;
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 更新UI
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

                    money.setText(outMoney.get(0).get("all"));
                    today.setText("今日收益"+outMoney.get(0).get("tod")+"元");
                    yestoday.setText("昨日收益"+outMoney.get(0).get("yes")+"元");
                    progressDialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                outMoney = getJsonInfo();
                Message msg = new Message();
                msg.what =1;
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<Map<String, String>> getJsonInfo(){
        String  respon = HttpUtils.postHttp(req, userPhone);
        Log.i("reponse",respon);
        MoneyJsonLists=new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("sign");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                Log.i("all",jsonArray1.getJSONObject(i).getString("all"));
                params.put("all", jsonArray1.getJSONObject(i).getString("all"));
                params.put("yes", jsonArray1.getJSONObject(i).getString("yes"));
                params.put("tod", jsonArray1.getJSONObject(i).getString("tod"));
                MoneyJsonLists.add(params);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return  MoneyJsonLists;
    }


    /**
     * 初始化所有参数
     */
    private void initView() {
        recharge = (TextView) findViewById(R.id.recharge);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MyMoneyActiviity.this,RechargeActivity.class));
                Intent inent = new Intent(MyMoneyActiviity.this,RechargeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("money",money.getText().toString());
                inent.putExtras(bundle);
                startActivity(inent);
            }
        });
        file_recharge = (TextView) findViewById(R.id.life_recharge);
        file_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inent = new Intent(MyMoneyActiviity.this,OutMoneyToYouActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("money",money.getText().toString());
                inent.putExtras(bundle);
                startActivity(inent);
//                startActivity(new Intent(MyMoneyActiviity.this,OutMoneyToYouActivity.class));
            }
        });
        getMoneyInfo = (TextView) findViewById(R.id.getmoney_info);
        getMoneyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inent = new Intent(MyMoneyActiviity.this,OutMoneyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("opcationType","in");
                inent.putExtras(bundle);
                startActivity(inent);
            }
        });
        OutMoneyInfo =  (TextView) findViewById(R.id.out_money_info);
        OutMoneyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inent = new Intent(MyMoneyActiviity.this,OutMoneyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("opcationType","out");
                inent.putExtras(bundle);
                startActivity(inent);
            }
        });
        back = (ImageView) findViewById(R.id.money_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRecharge() {
        recharge = (TextView) findViewById(R.id.recharge);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder str = new StringBuilder();//定义变长字符串
                        Random random = new Random();
                        for (int i = 8; i < 32; i++) {
                            str.append(random.nextInt(10));
                        }
                        String phone = "15348005990";
                        String price = "1";
                        String orderid = str.toString();
                        String sign = MD5(phone + price + orderid);
                        System.out.println("MD5" + sign + "dingdanhao" + orderid);
                        String rechargeArga = "phone=15348005990&price=1&orderid=" + str.toString() + "&sign=" + sign;
                        String httpUrl = "http://p.apix.cn/apixlife/pay/phone/phone_recharge";
                        httpUrl = httpUrl + "?" + rechargeArga;
                        URL url = null;
                        try {
                            url = new URL(httpUrl);
                            HttpURLConnection connection = (HttpURLConnection) url
                                    .openConnection();
                            connection.setRequestMethod("GET");
                            // 填入apix-key到HTTP header
                            connection.setRequestProperty("apix-key",  "cc813119ce4140dc60055228d1adecf2");
                            connection.setRequestProperty("content-type", "application/json");
                            connection.connect();
                            InputStream is = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                            String inputLine = null;
                            while ((inputLine = reader.readLine()) != null) {
                               Log.i("iii",inputLine);
                            }
                            System.out.println(inputLine + "jieguo");
                            reader.close();
                            //关闭http连接
                            connection.disconnect();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public String MD5(String s) {
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
}
