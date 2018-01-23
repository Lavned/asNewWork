package com.shows.allactivty;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class FeedbackActivity extends BaseActivity {

    EditText edit_feedback;
    Button submit_freeback;

    SharedPreferences mPreferences;
    String phonenum;
    ImageView about_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("telephone", "");
        Log.i("userphone", phonenum);
        initEdit();

        about_back = (ImageView) findViewById(R.id.about_back);
        about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 初始化
     */
    private void initEdit() {
        edit_feedback = (EditText) findViewById(R.id.edit_feedback);
        submit_freeback = (Button) findViewById(R.id.submit_freeback);
        submit_freeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edit_feedback.getText().toString().trim().equals("")) {
                    Log.i("userphone", phonenum);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadInfo();
                        }
                    }).start();
                    Toast.makeText(FeedbackActivity.this,"反馈成功，谢谢您的使用！",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedbackActivity.this,"说出你宝贵的意见啦",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String resultData="";
    private void uploadInfo() {
        URL url = null;
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/opinion/getOpinion.do");
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
            String content = "tel=" + URLEncoder.encode(phonenum.toString(), "UTF-8");
            content +="&content=" + URLEncoder.encode(edit_feedback.getText().toString(), "UTF-8");
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
            if ( resultData != null )
            {
                Log.i("yijian",resultData);
            }
            else
            {
                Log.i("yijian","读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
