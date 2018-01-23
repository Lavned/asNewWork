package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.ActionSheetDialog;

import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.MainActivity;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;
import com.yixinke.shows.whileview.picage.ChangeBirthDialog;


import org.json.JSONArray;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RedigerActivity extends BaseActivity {

    private List<String> list = new ArrayList<String>();
    private TextView myTextView;
    private Spinner user_address_province,user_address_city,user_address_area;
    private ArrayAdapter<String> adapter;
    RadioButton male,female;
    Button save;
    EditText user_name,pass,repass;
    TextView job;
    String resultData = "";
    String sex;
    String age;
    String area;
    EditText address;
    TextView user_age;
    String phonenum;

    //获取ID
    List<Map<String, String>> JsonsLists ;
    JSONObject json;
    String cid ,userid;
    List<Map<String, String>> UidJsonsLists;

    SharedPreferences mPreferences;
    private ProgressDialog progressDialog;

    //更新UI
    RedigerHandler redigerHandler;
    String result;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rediger);

        redigerHandler = new RedigerHandler();
        progressDialog = new ProgressDialog(this);
        user_name = (EditText) findViewById(R.id.user_name);
        initRadio();
        initAllView();
        initSpinner();

        mPreferences =getSharedPreferences("phone_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("phone", "");
        System.out.print("hhhh"+phonenum);
;

    }
    //异步线程更新UI
    class RedigerHandler extends Handler {
        public RedigerHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("panduan", "panduan......");
            try{
                if(msg.arg2 == 1){
                    progressDialog.dismiss();
                    Toast.makeText(RedigerActivity.this, "服务器反应异常，请稍等再试！",Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 1){
                    progressDialog.dismiss();
                    Toast.makeText(RedigerActivity.this, "数据异常！",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == 1){
                    json = new JSONObject(result);
                    JSONArray jsonArray1  =  json.getJSONArray("sign");
                    uid = jsonArray1.getJSONObject(0).getString("id");
                    Log.i("uuuuuid",uid+"uid");
                    saveInfo();

                    //保存登录信息
                    SharedPreferences preferences = getSharedPreferences(
                            "first_pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstIn", false);
                    editor.commit();

                    startActivity(new Intent(RedigerActivity.this, MainActivity.class));
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    //保存信息并提交
    private void saveInfo() {
        SharedPreferences uid_preferences = getSharedPreferences(
                "uid_pref", MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("uid", uid) ;
        uid_editor.putString("name",user_name.getText().toString());
        uid_editor.putString("sex",  sex);
        uid_editor.putString("telephone",  phonenum);
        uid_editor.putString("age",  age);
        uid_editor.putString("job", job.getText().toString());
        uid_editor.putString("area", area.toString());
        uid_editor.putString("address", address.getText().toString());
        uid_editor.commit();

    }

    /**
     * 上传信息到服务器
     */
    private String uploadInfo() {
        Log.i("sex", sex+user_age.getText().toString()+area+phonenum);
        URL url = null;
        String  BOUNDARY =  UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/register.do");
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
            String content = "name=" + URLEncoder.encode(user_name.getText().toString(), "UTF-8");
            content +="&tel=" + URLEncoder.encode(phonenum, "UTF-8");
            content +="&passwd=" + URLEncoder.encode(pass.getText().toString().trim(), "UTF-8");
            content +="&job=" + URLEncoder.encode(job.getText().toString().trim(), "UTF-8");
            content +="&age=" + URLEncoder.encode(user_age.getText().toString().trim(), "UTF-8");
            content +="&sex=" + URLEncoder.encode(sex.toString(), "UTF-8");
            content +="&province=" + URLEncoder.encode("甘肃省", "UTF-8");
            content +="&city=" + URLEncoder.encode("兰州市", "UTF-8");
            content +="&area=" + URLEncoder.encode(area.toString(), "UTF-8");
            content +="&address=" + URLEncoder.encode(address.getText().toString().trim(), "UTF-8");

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


//    public List<Map<String, String>> getUid(){
////        String respon = uploadInfo();
//        JsonsLists = new ArrayList<>();
//        HashMap<String,String> params;
//        try {
//            json = new JSONObject(respon);
//            JSONArray jsonArray1  =  json.getJSONArray("sign");
//            Log.i("rediger", "" + jsonArray1.getJSONObject(0).getString("ID"));
//            for (int i =0;i<jsonArray1.length();i++){
//                params = new HashMap<String, String>();
//                cid = jsonArray1.getJSONObject(i).getString("ID");
//                params.put("cid", cid);
//                JsonsLists.add(params);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return  JsonsLists;
//    }

//    public void RedigerSaveUid(){
//        UidJsonsLists = getUid();
//
//        Log.i("rediger",UidJsonsLists.size()+"xxx");
//        userid = UidJsonsLists.get(0).get("cid");
//        Log.i("rediger", UidJsonsLists.get(0).get("cid") + "用户ID");
//        SharedPreferences uid_preferences = getSharedPreferences( "uid_pref", MODE_PRIVATE);
//        SharedPreferences.Editor uid_editor = uid_preferences.edit();
//        uid_editor.putString("uid", userid) ;
//        uid_editor.commit();
//    }
    /**
     *初始化所有view
     */
    private void initAllView() {
        user_name = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.user_password);
        repass = (EditText) findViewById(R.id.user_reqpasseord);
        job= (TextView) findViewById(R.id.user_department);
        job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(RedigerActivity.this).builder()
                        .setTitle("请选择行业")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("互联网", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("互联网");
                                    }
                                })
                        .addSheetItem("销售", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("销售");
                                    }
                                })
                        .addSheetItem("医疗", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("医疗");
                                    }
                                })
                        .addSheetItem("汽车", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("汽车");
                                    }
                                })
                        .addSheetItem("教育", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("教育");
                                    }
                                })
                        .addSheetItem("服务", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("服务");
                                    }
                                })
                        .addSheetItem("建筑", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("建筑");
                                    }
                                })
                        .addSheetItem("金融", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("金融");
                                    }
                                })
                        .addSheetItem("学生", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("学生");
                                    }
                                })
                        .addSheetItem("工人", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("工人");
                                    }
                                })
                        .addSheetItem("公务员", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("公务员");
                                    }
                                })
                        .addSheetItem("无业", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        job.setText("无业");
                                    }
                                })
                        .show();
            }
        });

        address = (EditText) findViewById(R.id.user_address_info);
        user_name.setText(user_name.getText());
        pass.setText(pass.getText());
        repass.setText(repass.getText());
        job.setText(job.getText());


        save= (Button) findViewById(R.id.user_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput()){
                    progressDialog.setMessage("正在加载......");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result = uploadInfo();
                            Message msg = new Message();
                            if(result.equals("")){
                                msg.arg2 = 1;
                            }else{
                                msg.arg1 = 1;
                            }
                            if(result.contains("false")){
                                msg.what = 1;
                            }
                            redigerHandler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 非空判断
     */
    public boolean checkInput(){
        if(TextUtils.isEmpty(user_name.getText().toString().trim())){
            user_name.setHint("昵称不能为空");
            user_name.setHintTextColor(Color.parseColor("#EA2000"));
            return  false;
        }
       if(TextUtils.isEmpty(pass.getText().toString().trim())){
           pass.setHint("密码不能为空");
           pass.setHintTextColor(Color.parseColor("#EA2000"));
           return  false;
        }
//       if(TextUtils.isEmpty(repass.getText().toString().trim())){
//            repass.setHint("确认的密码不能为空");
//            repass.setHintTextColor(Color.parseColor("#EA2000"));
//            return  false;
//        }
//        if(!repass.getText().toString().equals(pass.getText().toString())){
//            Toast.makeText(this,"两次输入的密码不一样",Toast.LENGTH_SHORT).show();
//            return  false;
//        }
//        if(TextUtils.isEmpty(address.getText().toString().trim())){
//            address.setHint("详细位置不能为空");
//            address.setHintTextColor(Color.parseColor("#EA2000"));
//            return  false;
//        }
        return  true;
    }


    /**
     * 初始化单选框
     */
    private void initRadio() {
        male = (RadioButton) findViewById(R.id.radio_male);
        female =  (RadioButton) findViewById(R.id.radio_female);
        if(male.isChecked() == true){
            female.setChecked(false);
            sex = "男";
        }
        if(female.isChecked() == true){
            female.setChecked(true);
            sex = "女";
        }
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setChecked(false);
                sex = "男";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setChecked(true);
                sex = "女";
            }
        });
        user_age = (TextView) findViewById(R.id.user_age);
        user_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeBirthDialog mChangeBirthDialog = new ChangeBirthDialog(
                        RedigerActivity.this);
                mChangeBirthDialog.setDate(2016, 03);
                mChangeBirthDialog.show();
                mChangeBirthDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {

                    @Override
                    public void onClick(String year, String month, String day) {
                        user_age.setText(year + "年" + month + "月");
                        Toast.makeText(RedigerActivity.this,
                                year + "年" + month + "月",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

    /**
     * 初始化下拉框
     */
    private void initSpinner() {
        list.add("西固区");
        list.add("七里河区");
        list.add("安宁区");
        list.add("城关区");
        list.add("和平");
        user_address_area = (Spinner)findViewById(R.id.user_address_area);
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item, list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(R.layout.simple_spinner_down_item);
        //第四步：将适配器添加到下拉列表上
        user_address_area.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        user_address_area.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                /* 将所选mySpinner 的值带入myTextView 中*/
                area = adapter.getItem(arg2);
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        user_address_area.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 *
                 */
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        user_address_area.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

}
