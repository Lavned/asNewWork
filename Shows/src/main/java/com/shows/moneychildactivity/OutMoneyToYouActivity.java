package com.shows.moneychildactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

public class OutMoneyToYouActivity extends BaseActivity {

    EditText edit_account,edit_out_money,edit_marker;
    Button giveSubmit;

    SharedPreferences mPreferences;
    String userPhone ="";
    ImageView life_recharge_back;

    String mymoney;
    private Button choosePhone;
    private  String username,usernumber;

    //更新UI
    MyHandler myHandler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outmoney_toyou);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mymoney = bundle.getString("money");

        //进度条初始化
        progressDialog = new ProgressDialog(this);

        initGiveMoneyView();

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        //不在主线程更新UI
        myHandler = new MyHandler();
        life_recharge_back = (ImageView) findViewById(R.id.life_recharge_back);
        life_recharge_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /**
     * 初始化
     */
    private void initGiveMoneyView() {
        choosePhone = (Button) findViewById( R.id.choosephone);
        choosePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), 0);
            }
        });
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_out_money = (EditText) findViewById(R.id.edit_give_money);
        edit_marker = (EditText) findViewById(R.id.edit_marker);
        giveSubmit= (Button) findViewById(R.id.give_submit);
        giveSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    if (checkMoneyNum()) {
                        if (userPhone.equals(edit_account.getText().toString().trim())) {
                            Toast.makeText(OutMoneyToYouActivity.this, "不能给自己送钱", Toast.LENGTH_SHORT).show();
                        } else {
                            if(Double.parseDouble(mymoney) < Double.parseDouble(edit_out_money.getText().toString())){
                                Toast.makeText(OutMoneyToYouActivity.this,"您的金额不足！快去点广告赚钱吧！",Toast.LENGTH_SHORT).show();
                            }else{
                                new AlertDialog.Builder(OutMoneyToYouActivity.this).setTitle("送钱提示框").setMessage("确认给"+edit_account.getText().toString()+"送钱"+edit_out_money.getText().toString()+"元吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressDialog.setMessage("正在加载......");
                                                progressDialog.show();

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Message msg = new Message();
                                                        String isRediger = IsReg();
                                                        if (isRediger.contains("true")) {
                                                            Log.i("zhaunzhang", "可以转账");
                                                            String end = outMoneyToOther();
                                                            if (end.contains("true")) {
                                                                msg.what = 3;
                                                                myHandler.sendMessage(msg);
                                                                Log.i("zhaunzhang", "成功");
                                                            } else {
                                                                Log.i("zhaunzhang", "失败");
                                                                msg.arg2 = 2;
                                                                myHandler.sendMessage(msg);
                                                            }
                                                        } else {
                                                            Log.i("zhaunzhang", "没有注册不能转账");
                                                            msg.arg1 = 1;
                                                            myHandler.sendMessage(msg);
                                                        }
                                                    }
                                                }).start();
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                edit_account.setText(usernumber);
            }

        }
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
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Toast.makeText(OutMoneyToYouActivity.this, "没有找到该用户！", Toast.LENGTH_SHORT).show();
                }
                if(msg.arg2 ==2){
                    // 此处可以更新UI
                    Toast.makeText(OutMoneyToYouActivity.this, "送钱失败！", Toast.LENGTH_SHORT).show();
                }
                if(msg.what ==3){
                    // 此处可以更新UI
                    Toast.makeText(OutMoneyToYouActivity.this, "送钱成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断是否注册过
     */
    private String IsReg() {
        String resultData="";
        URL url = null;
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
            String content = "tel=" + URLEncoder.encode(edit_account.getText().toString(), "UTF-8");
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
                Log.i("zhuanzhang", resultData);
            } else {
                Log.i("zhuanzhang", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }
    /**
     * 赚钱操作请求服务器
     */
    String resultData="";
    private String outMoneyToOther() {
        URL url = null;
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/moveMoneys.do");
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
            String content = "mtel=" + URLEncoder.encode(userPhone.toString(), "UTF-8");
            content +="&ytel=" + URLEncoder.encode(edit_account.getText().toString(), "UTF-8");
            content +="&num=" + URLEncoder.encode(edit_out_money.getText().toString(), "UTF-8");
            content +="&type=" + URLEncoder.encode("c", "UTF-8");
            content +="&remark=" + URLEncoder.encode(edit_marker.getText().toString(), "UTF-8");
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
            if ( resultData != null ) {
                Log.i("zhuanqian",resultData);
            } else {
                Log.i("zhuanqian","读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }


    /**
     * 非空判断
     */
    public boolean checkInput(){
        if(TextUtils.isEmpty(edit_account.getText().toString().trim())){
            edit_account.setHint("对方账号不能为空");
            edit_account.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        if(TextUtils.isEmpty(edit_out_money.getText().toString().trim())){
            edit_out_money.setHint("金额不能为空");
            edit_out_money.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        return  true;
    }

    /**
     * 金额的数量判断
     * @return
     */
    public boolean checkMoneyNum(){
        Double moneyNum = Double.parseDouble(edit_out_money.getText().toString().trim());
        if (moneyNum < 1){
            Toast.makeText(OutMoneyToYouActivity.this,"金额不能小于1哦",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
    }

}
