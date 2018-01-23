package com.shows.allactivty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.UploadUtil;
import com.myqcloud.filecloud.FileCloudSign;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class ChangeHeadActivity extends BaseActivity implements View.OnClickListener,UploadUtil.OnUploadProcessListener {


    private Intent intent;
    ImageView head;
    TextView submithead;
    Bitmap head_images;

    private static final String TAG = "uploadImage";

    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;  //
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;
    /***
     * URL
     */
    private static String requestURL = HttpAddress.ADDRESSHTTP+ "/custom/uploadHeader.do";
    private Button selectButton,uploadButton;
    private ImageView imageView;
    private TextView uploadImageResult;
    private ProgressBar progressBar;

    private String picPath = null;
    private ProgressDialog progressDialog;

    SharedPreferences mPreferences;
    String teletphone;

    ImageView change_head_back;

    Bitmap bitmap;

    //图片上传相关
    PhotoUploadTask imgUploadTask = null;
    private FileInfo mCurrPhotoInfo = null;
    TextView text;
    String bucket = null;
    String signUrl = "http://203.195.194.28/php/getsignv2.php";
    String result = null;
    String appid = null;
    String sign = null;
    String signOne = null;
    String persistenceId = null;
    UploadManager mFileUploadManager = null;
    List<String> ImageUrl =new ArrayList<String>();


    private static final String APP_ID = "10045379";
    private static final String SECRET_ID = "AKIDEPSAHkOIBpnigMnVhNgY43d9OCunAZXw";
    private static final String SECRET_KEY = "OlD9zfW63ylYg2EapNK4QAj1hiekCV2L";
    private String userid = "123456";
    private StringBuffer mySign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_head);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        teletphone = mPreferences.getString("telephone", "");

        Log.i("userT", teletphone + "haoma");


        // 去用户的业务务器获取签名
        getUploadImageSign(signUrl);

        long expired = System.currentTimeMillis() / 1000 + 2592000;
//		//生成一个时效性签名
        mySign = new StringBuffer("");
        FileCloudSign.appSign(APP_ID, SECRET_ID, SECRET_KEY, expired, userid, mySign);

        System.out.println("demo mysign: " + mySign.toString());

        // 上传需要到的参数
        appid = "10045379";
        bucket = "shoumeixiu";
        persistenceId = "persistenceId";
        // 1，创建一个上传容器 需要1.appid 2.上传文件类型3.上传缓存（类型字符串，要全局唯一否则）
        mFileUploadManager = new UploadManager(this, appid, Const.FileType.Photo,persistenceId);

        intent = getIntent();
        initView();
        change_head_back = (ImageView) findViewById(R.id.change_head_back);
        change_head_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // 获取app 的签名
    private void getUploadImageSign(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(s);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    InputStreamReader in = new InputStreamReader(urlConnection
                            .getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inpuLine = null;
                    while ((inpuLine = buffer.readLine()) != null) {
                        result = inpuLine + "\n";
                    }
                    JSONObject jsonData = new JSONObject(result);
//                    sign = jsonData.getString("sign");
                    sign = mySign.toString();
                } catch (Exception e) {
                }
            }
        }).start();

    }

    /**
     * 初始化数据
     */
    private void initView() {
        selectButton = (Button) this.findViewById(R.id.selectImage);
        uploadButton = (Button) this.findViewById(R.id.uploadImage);
        selectButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        uploadImageResult = (TextView) findViewById(R.id.uploadImageResult);
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectImage:
                Intent intent = new Intent(this,SelectPicPopupWindow.class);
                startActivityForResult(intent, TO_SELECT_PHOTO);
                break;
            case R.id.uploadImage:
                try {
                    if(picPath!=null)
                    {
                        progressDialog.setMessage("正在上传......");
                        progressDialog.show();
                        handler.sendEmptyMessage(TO_UPLOAD_FILE);
                    }else{
                        Toast.makeText(this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
            {
                picPath = data.getStringExtra(SelectPicPopupWindow.KEY_PHOTO_PATH);
                Log.i(TAG, "最终选择的图片=" + picPath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                bitmap = BitmapFactory.decodeFile(picPath,options);
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                imageView.setImageBitmap(bitmap);
                options.inJustDecodeBounds = true;
                //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
                byte[] byteArray=byteArrayOutputStream.toByteArray();
                String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                //第三步:将String保持至SharedPreferences
                SharedPreferences sharedPreferences=getSharedPreferences("uid_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("head", imageString);
                editor.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 上传服务器响应回调
     */
    @Override
    public void onUploadDone(int responseCode, String message) {
        progressDialog.dismiss();
        Message msg = Message.obtain();
        msg.what = UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        handler.sendMessage(msg);
    }


    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(android.os.Message msg) {
            if(msg.arg1 == 1){
                System.out.println("chandu"+picPath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       String changeEnd = toUploadImages();
                        if(changeEnd.contains("true")){
                            finish();
                        }
                    }
                }).start();

            }
        };
    };

    //上传图片
    private void upLoadImg() {
//				m_pDialog.show();
            // 构建要上传的任务
            imgUploadTask = new PhotoUploadTask(picPath,
                    new IUploadTaskListener() {
                        @Override
                        public void onUploadSucceed(final FileInfo result) {
                            Log.e("jia", "upload succeed: " + result.url);
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCurrPhotoInfo = result;
                                    ImageUrl.add(Html.fromHtml("<u>" + result.url + "</u>") + "");
                                    System.out.println("listurl------"+ImageUrl);
                                    Message msg = new Message();
                                    msg.arg1 = 1;
                                    mMainHandler.sendMessage(msg);
                                    System.out.println("chandu"+ImageUrl.size()+"ge"+ImageUrl.get(0));
                                }
                            });
                        }

                        @Override
                        public void onUploadStateChange(ITask.TaskState state) {
                        }

                        @Override
                        public void onUploadProgress(final long totalSize, final long sendSize) {
                            long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
                            Log.e("jia", "上传进度: " + p + "%");
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
                                    progressDialog.setMessage("上传进度: " + p + "%");
                                    if(p == 100){
                                        progressDialog.dismiss();
                                    }
//                                    Log.e("jia", "上传进度: " + p + "%");
//                                    Log.e("UOLOADSSS","上传进度:" + "%");
                                }
                            });
                        }

                        @Override
                        public void onUploadFailed(final int errorCode,
                                                   final String errorMsg) {
                            Log.i("Demo", "上传结果:失败! ret:" + errorCode + " msg:" + errorMsg);
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    Log.i("上传结果:失败! ret:", errorCode + " msg:" + errorMsg);
                                }
                            });
                        }
                    });
            imgUploadTask.setBucket(bucket);
            // task.setFileId("test_fileId_"); // 为图片自定义FileID(可选)
            imgUploadTask.setAuth(sign);
            mFileUploadManager.upload(imgUploadTask); // 开始上传
    }


    /**
     * 上传头像
     */
    String resultData="";
    private String toUploadImages(){
        System.out.print("listurl"+ImageUrl.get(0));

        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(requestURL);
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
            String content = "tel=" + URLEncoder.encode(teletphone, "UTF-8");
            content += "&pictureAddress=" + (ImageUrl.get(0));
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

    private void toUploadFile()
    {
        try{
            uploadImageResult.setText("正在上传中...");
            progressDialog.setMessage("正在上传...");
            progressDialog.show();
            String fileKey = "pic";
            UploadUtil uploadUtil = UploadUtil.getInstance();
            uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

            Map<String, String> params = new HashMap<String, String>();
            params.put("orderId", "11111");
            uploadUtil.uploadFile(picPath, fileKey, requestURL, params, teletphone);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
//                    toUploadFile();
                    upLoadImg();
                    break;
                case UPLOAD_INIT_PROCESS:
                    progressBar.setMax(msg.arg1);
                    break;
                case UPLOAD_IN_PROCESS:
                    progressBar.setProgress(msg.arg1);
                    break;
                case UPLOAD_FILE_DONE:
                    String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
                    String rankds = msg.obj+"";
                    Toast.makeText(ChangeHeadActivity.this,result,Toast.LENGTH_SHORT).show();
                    if(rankds.contains("true")){
                        uploadImageResult.setText("上传成功");
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        handler.sendMessage(msg );
    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        handler.sendMessage(msg );
    }
}
