package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.Filedir;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XiuCommActivity extends BaseActivity implements  DownloadListener{

    LinearLayout gonggao;
    RelativeLayout gongyilin;
    WebView gongyi_web;
    TextView xiuxiu_title;
    ImageView back;
    TextView aixinjuanzhu;

    TextView gg_title,gg_content,gg_stime,gg_write;
    List<Map<String, String>> ggUrlsLists ;
    List<Map<String, String>> ggUrls ;

    ggHandler gghandler;
    JSONObject json;
    SharedPreferences mPreferences;
    String phonenum;
    String isDocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiu_comm);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("telephone", "");

        gongyi_web = (WebView) findViewById(R.id.gongyi_webview);
        gonggao = (LinearLayout) findViewById(R.id.gonggaolin);
        gongyilin = (RelativeLayout) findViewById(R.id.gongyilin);

        initGGvIEW();
        gghandler = new ggHandler();

        aixinjuanzhu = (TextView) findViewById(R.id.aixinjuanzhu);
        aixinjuanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isDocation =  donation();
                        Log.i("isDocation",isDocation+"isDocation");
                        Message msg = new Message();
                        if(isDocation == null){
                            msg.arg2 = 1;
                        }
                        if(isDocation.contains("true")){
                            msg.what =1 ;
                        }else if(isDocation.contains("false")){
                            msg.what =2;
                        }
                        gghandler.sendMessage(msg);
                    }
                }).start();

            }
        });
        xiuxiu_title = (TextView) findViewById( R.id.xiuxiu);
        back = (ImageView) findViewById(R.id.gonggao_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String type = bundle.getString("type");
        Log.i("type",type+"ss");
        if(type.equals("1")){
            xiuxiu_title.setText("秀秀公告");
            gonggao.setVisibility(View.VISIBLE);
            gongyilin.setVisibility(View.GONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ggUrls = viewSetText();
                    Message msg = new Message();
                    msg.arg1 = 1;
                    gghandler.sendMessage(msg); // 向Handler发送消息,更新UI
                }
            }).start();

        }else if(type.equals("2")){
            xiuxiu_title.setText("秀秀公益");
            gonggao.setVisibility(View.GONE);
            gongyilin.setVisibility(View.VISIBLE);
            initWebView();
        }

    }

    private String donation(){
        String url = HttpAddress.ADDRESSHTTP+ "/mrecord/give.do";
        String result =  HttpUtils.postHttp(url,phonenum);
        Log.i("resuresultresultlt",result+";ll");
        return  result;
    }
    /**
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = initGongGao();
        ggUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("announ");
            Log.i("memem", "" + jsonArray1.getJSONObject(0).getString("title"));
                params = new HashMap<String, String>();
                params.put("title", jsonArray1.getJSONObject(0).getString("title"));
                params.put("content", jsonArray1.getJSONObject(0).getString("content"));
                params.put("stime", jsonArray1.getJSONObject(0).getString("stime"));
                params.put("writer", jsonArray1.getJSONObject(0).getString("writer"));
                ggUrlsLists.add(params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  ggUrlsLists;
    }
    
    class ggHandler extends Handler {
        public ggHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("ggHandler", "handleMessage......");
            try{
                if(msg.arg2 ==1){
                    Toast.makeText(XiuCommActivity.this,"服务异常，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    gg_title.setText(ggUrls.get(0).get("title").toString());
                    gg_content.setText(ggUrls.get(0).get("content").toString());
                    gg_stime.setText(ggUrls.get(0).get("stime").toString());
                    gg_write.setText(ggUrls.get(0).get("writer").toString());
                }
                if(msg.what ==1 ){
                    aixinjuanzhu.setText("感谢您的爱心，您已成功捐赠！");
                    aixinjuanzhu.setClickable(false);
//                    finish();
                }
                if(msg.what ==2 ){
                    Toast.makeText(XiuCommActivity.this,"服务异常，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }



    /**
     *公告数据解析
     */
    private void initGGvIEW() {
        gg_title = (TextView) findViewById(R.id.gonggao_title);
        gg_content = (TextView) findViewById(R.id.gonggao_content);
        gg_stime = (TextView) findViewById(R.id.gonggao_time);
        gg_write = (TextView) findViewById(R.id.gonggao_write);
    }

    /***
     *公告接口
     */
    private String  initGongGao() {
        String requestUrl=HttpAddress.ADDRESSHTTP+"/announ/announList.do";
        String result = HttpUtils.getHttp(requestUrl);
        Log.i("result",result);
        return  result;
    }

    private void initWebView() {
        String url =HttpAddress.ADDRESSHTTP+"/pubfit/pubfit.do";
        Log.i("urlurl",url+"url");
        gongyi_web.setHorizontalScrollBarEnabled(false);
        gongyi_web.setVerticalScrollBarEnabled(false);

        gongyi_web.getSettings().setDomStorageEnabled(true);
        gongyi_web.setFocusable(true);
        gongyi_web.getSettings().getJavaScriptCanOpenWindowsAutomatically();

        gongyi_web.getSettings().setDefaultTextEncodingName("UTF-8");
        gongyi_web.getSettings().setGeolocationEnabled(true);//启用地理定位
//	        webview.getSettings().setCacheMode();
        gongyi_web.getSettings().setGeolocationDatabasePath(Filedir.projectavderpath); ;//将图片调整到适合webview的大小
        gongyi_web.getSettings().setJavaScriptEnabled(true);
        gongyi_web.getSettings().setUseWideViewPort(true) ;//将图片调整到适合webview的大小
        gongyi_web.getSettings().setLoadsImagesAutomatically(true) ;//支持自动加载图片
        gongyi_web.setDownloadListener(this);
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        gongyi_web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && gongyi_web.canGoBack()) {  //表示按返回键  时的操作
                        gongyi_web.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });


        gongyi_web.setWebViewClient(new WebViewClient(){
            ProgressDialog pd = new ProgressDialog(XiuCommActivity.this);

            //页面加载开始执行
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pd.setMessage("正在加载，请耐心等待...");
//	        		Toast.makeText(getBaseContext(), "正在加载，请稍后……", 5000).show();
                if (!pd.isShowing()) {
                    pd.show();
                }

            }

            //页面加载结束时执行
            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url2) {
//					view.loadUrl(url2);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
                view.loadData("亲，网络连接失败！", "text/html; charset=UTF-8", null);

            }

        });


        gongyi_web.loadUrl(url);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                long contentLength) {
        if (url.indexOf(".apk")!=-1) {
            return;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
