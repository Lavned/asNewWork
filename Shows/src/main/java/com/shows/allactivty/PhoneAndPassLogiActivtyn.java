package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.MainActivity;
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
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhoneAndPassLogiActivtyn extends BaseActivity {

    //获取ID
    List<Map<String, String>> JsonsLists ;
    JSONObject json;
    String cid,uid,cphone,area;
    String sex;
    String age;
    String address,head,job,name;

    List<Map<String, String>> UidJsonsLists;

    EditText user_login_phone,user_login_pass;
    ImageView login_back;
    TextView jumpRediger;

    TextView forgetpass;

    //更新UI
    MyHandler myHandler;
    List<Map<String, String>> isredigers;
    List<Map<String, String>> isredigersLists;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_phone_and_pass_logi_activtyn);

        progressDialog = new ProgressDialog(this);
        user_login_phone = (EditText) findViewById(R.id.user_login_phone);
        user_login_pass = (EditText) findViewById(R.id.user_login_pass);
        forgetpass = (TextView) findViewById(R.id.forgetpass);

        //不在主线程更新UI
        myHandler = new MyHandler();

        Button btn = (Button) findViewById(R.id.user_login);
        try {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkInput()) {
                        if(LoginActivity.isMobileNO(user_login_phone.getText().toString())){
                            progressDialog.setMessage("正在加载...");
                            progressDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        isredigersLists = getBoolean();
                                        Message msg = new Message();
                                        String isRegiger  = isredigersLists.get(0).get("booleanRidiger");
//                                        if(isRegiger.equals("")){
//                                            msg.what =1;
//                                            myHandler.sendMessage(msg);
//                                        }else if(isRegiger.contains("false")){
//                                            msg.what =2;
//                                            myHandler.sendMessage(msg);
//                                        }
//                                        Log.i("panduanle", isRegiger);
                                        if(isRegiger.trim().equals("true")){
                                            Log.i("panduanzhen","该手机号注册信息");
    //                                        uploadPhone();
                                            SaveUid();
                                            SaveLoginState();
                                            startActivity(new Intent(PhoneAndPassLogiActivtyn.this, MainActivity.class));
                                            finish();
                                        }else{
                                            Log.i("panduan","该手机号还没有注册信息");
                                            msg.arg2 =1;
                                            myHandler.sendMessage(msg);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }else{
                            Toast.makeText(PhoneAndPassLogiActivtyn.this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }


        login_back = (ImageView) findViewById(R.id.login_back);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneAndPassLogiActivtyn.this, ChooseLoginActivty.class));
                finish();
            }
        });

        jumpRediger = (TextView) findViewById(R.id.jumpRediger);
        jumpRediger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhoneAndPassLogiActivtyn.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhoneAndPassLogiActivtyn.this, ForgetPassConfimActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    //异步线程更新UI
    class MyHandler extends Handler {
        public MyHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("panduan", "panduan......");
            try{

                progressDialog.dismiss();
                if(msg.arg2 ==1){

                    Toast.makeText(PhoneAndPassLogiActivtyn.this, "该手机号还没有注册信息！", Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 1){
                    Toast.makeText(PhoneAndPassLogiActivtyn.this, "服务器反应异常，请稍等再试！",Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 2){
                    Toast.makeText(PhoneAndPassLogiActivtyn.this, "用户名或密码不匹配",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    String booleanRidiger;
    public List<Map<String, String>> getBoolean(){
        String respon = IsReg();
        Log.i("shou",respon);
        isredigers = new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            params = new HashMap<String, String>();
            booleanRidiger = json.getString("sign");
            Log.i("booleanRidiger",booleanRidiger);
            params.put("booleanRidiger", booleanRidiger);
            isredigers.add(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  isredigers;
    }



    /**
     * 非空判断
     */
    public boolean checkInput(){
        if(TextUtils.isEmpty(user_login_phone.getText().toString().trim())){
            user_login_phone.setHint("手机号不能为空");
            user_login_phone.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        if(TextUtils.isEmpty(user_login_pass.getText().toString().trim())){
            user_login_pass.setHint("密码不能为空");
            user_login_pass.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        return  true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            startActivity(new Intent(PhoneAndPassLogiActivtyn.this, ChooseLoginActivty.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void SaveLoginState(){
        SharedPreferences preferences = getSharedPreferences(
                "first_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    /**
     * 判断是否注册过
     */
    private String IsReg() {
        String resultData="";
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/judgeReg.do");
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
            String content = "tel=" + URLEncoder.encode(user_login_phone.getText().toString(), "UTF-8");
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
                Log.i("panduan", resultData);
            } else {
                Log.i("panduan", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  resultData;
    }
    /**
     * 上传信息到服务器
     */
    String resultData="";
    private String uploadPhone() {
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/login.do");
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
            String content = "tel=" + URLEncoder.encode(user_login_phone.getText().toString(), "UTF-8");
            content += "&passwd=" + URLEncoder.encode(user_login_pass.getText().toString(), "UTF-8");
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
                Log.i("login",resultData);
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("qingqiu", resultData);
            } else {
                Log.i("qingqiu", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }



    public List<Map<String, String>> getUid(){
        String respon = uploadPhone();
        JsonsLists = new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("sign");
            Log.i("memem", "" + jsonArray1.getJSONObject(0).getString("id"));
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                cid = jsonArray1.getJSONObject(i).getString("id");
                cphone = jsonArray1.getJSONObject(i).getString("tel");

                name = jsonArray1.getJSONObject(i).getString("name");
                job = jsonArray1.getJSONObject(i).getString("job");
                head = jsonArray1.getJSONObject(i).getString("head");
                age = jsonArray1.getJSONObject(i).getString("age");
                sex = jsonArray1.getJSONObject(i).getString("sex");
                address = jsonArray1.getJSONObject(i).getString("address");
                area = jsonArray1.getJSONObject(i).getString("area");

                params.put("cid", cid);
                params.put("cphone", cphone);
                params.put("name", name);
                params.put("job", job);
                params.put("head", head);
                params.put("age", age);
                params.put("sex", sex);
                params.put("address", address);
                params.put("area", area);

                Log.i("memem", "" + address);
                JsonsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  JsonsLists;
    }

    /**
     *
     */
    public void SaveUid(){
        UidJsonsLists = getUid();
        Message msg= new Message();
        if(UidJsonsLists== null){
            msg.what =1;
            myHandler.sendMessage(msg);
        }else if(UidJsonsLists.size()==0){
            msg.what =2;
            myHandler.sendMessage(msg);
        }
        Log.i("xxx",UidJsonsLists.size()+"xxx");
        uid = UidJsonsLists.get(0).get("cid");
        cphone = UidJsonsLists.get(0).get("cphone");
        address = UidJsonsLists.get(0).get("address");
        Log.i("xxxx", UidJsonsLists.get(0).get("cid") + "用户ID");
        SharedPreferences uid_preferences = getSharedPreferences(
                "uid_pref", MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("uid", uid) ;
        uid_editor.putString("name", UidJsonsLists.get(0).get("name"));
        uid_editor.putString("sex",  UidJsonsLists.get(0).get("sex"));
        uid_editor.putString("telephone",  cphone);
        uid_editor.putString("password",user_login_pass.getText().toString()  );
        uid_editor.putString("age",   UidJsonsLists.get(0).get("age"));
        uid_editor.putString("job",  UidJsonsLists.get(0).get("job"));
        uid_editor.putString("area",  UidJsonsLists.get(0).get("area"));
        uid_editor.putString("address", address );

        Log.i("xxxx", UidJsonsLists.get(0).get("address") + "用户ID");
        uid_editor.putString("head",  UidJsonsLists.get(0).get("head"));
        uid_editor.commit();
    }


}
