package com.shows.allactivty;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AddAddressActivty extends BaseActivity {
    ImageView add_ress_back;

    EditText get_name,get_phone,get_address;
    TextView submit_ok;
    String result;
    SharedPreferences mPreferences;
    String phonenum;

    AddressHandler addHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_address_activty);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("telephone", "");
        Log.i("userphone", phonenum);
        addHandler = new AddressHandler();

        add_ress_back = (ImageView) findViewById(R.id.add_ress_back);
        add_ress_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initAddView();

    }


    class AddressHandler extends Handler {
        public AddressHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("add", "handleMessage......");
            try{
                if(msg.arg1 ==1){
                    Toast.makeText(AddAddressActivty.this,"添加成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     *添加控件初始化
     */
    private void initAddView() {
        get_name = (EditText) findViewById(R.id.get_goods_name);
        get_phone = (EditText) findViewById(R.id.get_good_phone);
        get_address = (EditText) findViewById(R.id.get_good_address);
        submit_ok = (TextView) findViewById(R.id.add_ok);
        submit_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result = addressSubmit();
                            if(result.contains("true")){
                                saveAddress();
                                Message msg = new Message();
                                msg.arg1 = 1;
                                addHandler.sendMessage(msg);
                            }
                        }
                    }).start();
                }
            }
        });
    }


    /**
     * 保存地址
     */
    public void saveAddress(){

        SharedPreferences uid_preferences = getSharedPreferences(
                "address_pref", MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("gname",  get_name.getText().toString());
        uid_editor.putString("gphone", get_phone.getText().toString());
        uid_editor.putString("gaddress",  get_address.getText().toString());
        uid_editor.commit();
    }

    /**
     * 非空判断
     */
    public boolean checkInput(){
        if(TextUtils.isEmpty(get_name.getText().toString().trim())){
            get_name.setHint("不能为空");
            get_name.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        if(TextUtils.isEmpty(get_phone.getText().toString().trim())){
            get_phone.setHint("不能为空");
            get_phone.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        if(TextUtils.isEmpty(get_address.getText().toString().trim())){
            get_address.setHint("不能为空");
            get_address.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
        return  true;
    }

    /**
     * 吧数据请求到服务器
     * @return
     */
    public String addressSubmit(){
        String inputEnd="";
        String  localURL = null;
        Log.i("xxxx",get_address.getText()+"xxx"+get_name.getText()+"xccc"+get_phone.getText());
        localURL = HttpAddress.ADDRESSHTTP+"/address/addAddress.do?tel="+phonenum
                +"&Address="+get_address.getText()+"&username="+get_name.getText()+"&recipientstel="+get_phone.getText();
        URL url = null;
        try {
            url = new URL(localURL);
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
            content += "&Address=" + URLEncoder.encode(get_address.getText().toString(), "UTF-8");
            content += "&username=" + URLEncoder.encode(get_name.getText().toString(), "UTF-8");
            content += "&recipientstel=" + URLEncoder.encode(get_phone.getText().toString(), "UTF-8");
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
                inputEnd += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (inputEnd != null) {
                Log.i("address", inputEnd);
            } else {
                Log.i("address", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  inputEnd;
    }
}
