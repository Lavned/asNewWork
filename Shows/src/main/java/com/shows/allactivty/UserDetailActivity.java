package com.shows.allactivty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.ActionSheetDialog;
import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import com.yixinke.shows.whileview.picage.ChangeBirthDialog;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener{

    TextView info_name,info_sex,info_job,info_address,info_telephone,info_age,info_area;
    String info_Header;
    SharedPreferences mPreferences;
    ImageView image_head;
    Intent intent;

    Bitmap bitmap;

    ImageView info_back;
    TextView user_change,ed_sex,user_submit,ed_age,ed_area;

    EditText ed_name,ed_address;
    private TextView ed_job;

    String endResult;
    UserHandler userHandler;

    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_detail);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        Asynphoto();

        info_name= (TextView) findViewById(R.id.user_nicheng);
        info_sex= (TextView) findViewById(R.id.user_user_sex);
        info_job= (TextView) findViewById(R.id.user_user_job);
        info_address= (TextView) findViewById(R.id.user_user_address);
        info_telephone = (TextView) findViewById(R.id.user_telphone);
        info_age = (TextView) findViewById(R.id.user_user_age);
        info_area = (TextView) findViewById(R.id.user_user_area);

        image_head = (ImageView) findViewById(R.id.user_icon);
        image_head.setOnClickListener(this);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        info_name.setText(mPreferences.getString("name",""));
        info_sex.setText(mPreferences.getString("sex",""));
        info_job.setText(mPreferences.getString("job",""));
        info_address.setText(mPreferences.getString("address",""));
        info_telephone.setText(mPreferences.getString("telephone",""));
        info_age.setText(mPreferences.getString("age",""));
        info_area.setText(mPreferences.getString("area",""));

        info_back = (ImageView) findViewById(R.id.userinfo_back);
        info_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initChangeView();
        userHandler = new UserHandler();
    }

    class UserHandler extends Handler {
        public UserHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("user", "handleMessage......");
            try{
                if(msg.arg1 ==1){
                    user_change.setVisibility(View.VISIBLE);
                    user_submit.setVisibility(View.GONE);
                    info_name.setVisibility(View.VISIBLE);
                    ed_name.setVisibility(View.GONE);
                    info_name.setText(mPreferences.getString("name", ""));
                    info_job.setVisibility(View.VISIBLE);
                    ed_job.setVisibility(View.GONE);
                    info_job.setText(mPreferences.getString("job", ""));
                    info_sex.setVisibility(View.VISIBLE);
                    ed_sex.setVisibility(View.GONE);
                    info_sex.setText(mPreferences.getString("sex", ""));
                    info_address.setVisibility(View.VISIBLE);
                    ed_address.setVisibility(View.GONE);
                    info_address.setText(mPreferences.getString("address", ""));
                    info_age.setVisibility(View.VISIBLE);
                    ed_age.setVisibility(View.GONE);
                    info_age.setText(mPreferences.getString("age", ""));
                    info_area.setVisibility(View.VISIBLE);
                    ed_area.setVisibility(View.GONE);
                    info_area.setText(mPreferences.getString("area", ""));

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void SaveUser(){

        SharedPreferences uid_preferences = getSharedPreferences(
                "uid_pref", MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("name", ed_name.getText().toString());
        uid_editor.putString("sex", ed_sex.getText().toString());
        uid_editor.putString("age", ed_age.getText().toString());
        uid_editor.putString("job", ed_job.getText().toString());
        uid_editor.putString("area", ed_area.getText().toString());
        uid_editor.putString("address", ed_address.getText().toString());
        uid_editor.commit();
    }


    /**
     * 修改信息初始化
     */
    private void initChangeView() {
        user_change = (TextView) findViewById(R.id.user_change);
        user_submit = (TextView) findViewById(R.id.user_submit);

        ed_name = (EditText) findViewById(R.id.ed_user_nicheng);
        ed_address = (EditText) findViewById(R.id.ed_user_user_address);
        ed_job = (TextView) findViewById(R.id.ed_user_user_job);
        ed_sex = (TextView) findViewById(R.id.ed_user_user_sex);
        ed_age = (TextView) findViewById(R.id.ed_user_user_age);
        ed_area = (TextView) findViewById(R.id.ed_user_user_area);

        user_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_change.setVisibility(View.GONE);
                user_submit.setVisibility(View.VISIBLE);
                info_name.setVisibility(View.GONE);
                ed_name.setVisibility(View.VISIBLE);
                ed_name.setText(mPreferences.getString("name", ""));
                info_job.setVisibility(View.GONE);
                ed_job.setVisibility(View.VISIBLE);
                ed_job.setText(mPreferences.getString("job", ""));
                info_sex.setVisibility(View.GONE);
                ed_sex.setVisibility(View.VISIBLE);
                info_address.setVisibility(View.GONE);
                ed_address.setVisibility(View.VISIBLE);
                ed_address.setText(mPreferences.getString("address",""));
                info_age.setVisibility(View.GONE);
                ed_age.setVisibility(View.VISIBLE);
                info_area.setVisibility(View.GONE);
                ed_area.setVisibility(View.VISIBLE);
            }
        });
        user_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        endResult = submitUserInfo();
                        SaveUser();
                        Message msg = new Message();
                        msg.arg1 = 1;
                        userHandler.sendMessage(msg);

                    }
                }).start();
            }
        });
        ed_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(UserDetailActivity.this).builder()
                        .setTitle("请选择行业")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("互联网", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("互联网");
                                    }
                                })
                        .addSheetItem("销售", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("销售");
                                    }
                                })
                        .addSheetItem("医疗", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("医疗");
                                    }
                                })
                        .addSheetItem("汽车", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("汽车");
                                    }
                                })
                        .addSheetItem("教育", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("教育");
                                    }
                                })
                        .addSheetItem("服务", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("服务");
                                    }
                                })
                        .addSheetItem("建筑", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("建筑");
                                    }
                                })
                        .addSheetItem("金融", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("金融");
                                    }
                                })
                        .addSheetItem("学生", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("学生");
                                    }
                                })
                        .addSheetItem("工人", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("工人");
                                    }
                                })
                        .addSheetItem("公务员", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("公务员");
                                    }
                                })
                        .addSheetItem("无业", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_job.setText("无业");
                                    }
                                })
                        .show();
            }
        });
        ed_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(UserDetailActivity.this).builder()
                        .setTitle("请选择性别")
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("男", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_sex.setText("男");
                                    }
                                })
                        .addSheetItem("女", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_sex.setText("女");
                                    }
                                })
                        .show();
            }
        });
        ed_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(UserDetailActivity.this).builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("西固区", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_area.setText("西固区");
                                    }
                                })
                        .addSheetItem("七里河区", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_area.setText("七里河区");
                                    }
                                })
                        .addSheetItem("安宁区", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_area.setText("安宁区");
                                    }
                                })
                        .addSheetItem("城关区", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_area.setText("城关区");
                                    }
                                })
                        .addSheetItem("红谷区", ActionSheetDialog.SheetItemColor.Red,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        ed_area.setText("红古区");
                                    }
                                })
                        .show();
            }
        });
        ed_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ActionSheetDialog(UserDetailActivity.this).builder()
//                        .setCancelable(false)
//                        .setCanceledOnTouchOutside(false)
//                        .addSheetItem("20岁以下", ActionSheetDialog.SheetItemColor.Red,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        ed_age.setText("20岁以下");
//                                    }
//                                })
//                        .addSheetItem("20岁-30岁", ActionSheetDialog.SheetItemColor.Red,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        ed_age.setText("20-30");
//                                    }
//                                })
//                        .addSheetItem("30岁-40岁", ActionSheetDialog.SheetItemColor.Red,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        Toast.makeText(UserDetailActivity.this,ed_age.getText().toString(), Toast.LENGTH_SHORT).show();
//                                        ed_age.setText("30-40");
//                                    }
//                                })
//                        .addSheetItem("40岁以上", ActionSheetDialog.SheetItemColor.Red,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        ed_age.setText("40岁以上");
//                                    }
//                                })
//                        .show();
                ChangeBirthDialog mChangeBirthDialog = new ChangeBirthDialog(
                        UserDetailActivity.this);
                mChangeBirthDialog.setDate(2000, 01);
                mChangeBirthDialog.show();
                mChangeBirthDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {

                    @Override
                    public void onClick(String year, String month, String day) {
                        ed_age.setText( year + "年" + month+"月");
                        Toast.makeText(UserDetailActivity.this,
                                year + "-" + month ,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * 上传修改信息到服务器
     */
    public String submitUserInfo(){
        String resultInput="";
        Log.i("sex", ed_sex.getText()+"cc");
        URL url = null;
        String  BOUNDARY =  UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/custom/editSave.do");
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
            String content = "tel=" + URLEncoder.encode(mPreferences.getString("telephone",""), "UTF-8");
            content +="&name=" + URLEncoder.encode(ed_name.getText().toString(), "UTF-8");
            content +="&sex=" + URLEncoder.encode(ed_sex.getText().toString(), "UTF-8");
            content +="&job=" + URLEncoder.encode(ed_job.getText().toString(), "UTF-8");
            content +="&age=" + URLEncoder.encode(ed_age.getText().toString(), "UTF-8");
            content +="&area=" + URLEncoder.encode(ed_area.getText().toString(), "UTF-8");
            content +="&address=" + URLEncoder.encode("", "UTF-8");
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
                resultInput += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if ( resultInput != null ){
                Log.i("change",resultInput);
            }else{
                Log.i("change","读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultInput;
    }
//    /
    /**
     * 获取存好的bitmap
     */
    private void getBitmapFromSharedPreferences(){
        SharedPreferences sharedPreferences=getSharedPreferences("uid_pref", Context.MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString=sharedPreferences.getString("head", "");
        //第二步:利用Base64将字符串转换为ByteArrayInputStream
        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        //第三步:利用ByteArrayInputStream生成Bitmap
        bitmap=BitmapFactory.decodeStream(byteArrayInputStream);
        image_head.setImageBitmap(bitmap);
    }



    //异步加载图片
    private void Asynphoto() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.luncher)
                .showImageForEmptyUri(R.mipmap.luncher)
                .showImageOnFail(R.mipmap.luncher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    @Override
    protected void onResume() {
        Log.i("onResume", "onResume called.");
        if(mPreferences.getString("head","").equals("")){
            image_head.setBackgroundResource(R.mipmap.wawatwo);
        }
        if(mPreferences.getString("head","").contains("http")){
            info_Header = mPreferences.getString("head","");
            imageLoader.displayImage(info_Header, image_head, options);
        }else{
            getBitmapFromSharedPreferences();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
//            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        Log.i("uuuu", "hhh1");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_nicheng :
                break;
            case R.id.user_user_sex :
                break;
            case R.id.user_user_job :
                break;
            case R.id.user_user_address :
                break;
            case R.id.user_icon :
                Intent intnet = new Intent(UserDetailActivity.this,ChangeHeadActivity.class);
                startActivity(intnet);
                break;
        }
    }


}
