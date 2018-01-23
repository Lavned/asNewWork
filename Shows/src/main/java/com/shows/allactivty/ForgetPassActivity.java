package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;

public class ForgetPassActivity extends BaseActivity {

    //更新UI
    PassHandler myHandler;
    private ProgressDialog progressDialog;
    ImageView back;
    EditText pass;
    Button save;
    String phonenum ,resultEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_pass);

        Intent intent = getIntent();
        Bundle  bundle = intent.getExtras();
        phonenum = bundle.getString("tel");
        Log.i("tel",phonenum);

        myHandler = new PassHandler();
        progressDialog = new ProgressDialog(this);
        pass = (EditText) findViewById(R.id.user_login_pass);
        back = (ImageView) findViewById(R.id.pass_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save = (Button) findViewById(R.id.pass_pass);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("正在提交...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        resultEnd = repassPhone();
                        Message msg = new Message();
                        if(resultEnd.equals("")){
                            msg.arg1 = 1;
                        }else if(resultEnd.contains("true")){
                            msg.arg1 = 2;
                        }else if(resultEnd.contains("false")){
                            msg.arg1 = 3;
                        }
                        myHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }


    /**
     * 上传信息到服务器
     */
    String resultData="";
    private String repassPhone() {
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/repasswd.do");
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
            String content = "tel=" + URLEncoder.encode(phonenum, "UTF-8");
            content += "&passwd=" + URLEncoder.encode(pass.getText().toString(), "UTF-8");
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
                Log.i("resultData",resultData);
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


    //异步线程更新UI
    class PassHandler extends android.os.Handler {
        public PassHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("PassHandler", "PassHandler......");
            try{
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Toast.makeText(ForgetPassActivity.this, "服务器反应异常，请稍等再试！", Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == 2){
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPassActivity.this, "修改成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(ForgetPassActivity.this, PhoneAndPassLogiActivtyn.class);
                    startActivity(intent);
                    finish();
                }
                if(msg.arg1 == 3){
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPassActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
