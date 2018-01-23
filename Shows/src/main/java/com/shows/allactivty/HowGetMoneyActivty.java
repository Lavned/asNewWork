package com.shows.allactivty;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.com.shows.utils.Filedir;
import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

@SuppressLint("JavascriptInterface")
public class HowGetMoneyActivty extends BaseActivity implements OnClickListener ,DownloadListener{
    private WebView webview;

    private ImageView back,forward,reload,closed;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_get_money_activty);
        initConsalView();
        initConsalEvent();
//        String url =getIntent().getStringExtra("url");
        String url = HttpAddress.ADDRESSHTTP+"/custom/help.do";
        webview=(WebView) findViewById(R.id.webview);
        webview.setHorizontalScrollBarEnabled(false);
        webview.setVerticalScrollBarEnabled(false);

        webview.getSettings().setDomStorageEnabled(true);
        webview.setFocusable(true);
        webview.getSettings().getJavaScriptCanOpenWindowsAutomatically();

        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.getSettings().setGeolocationEnabled(true);//启用地理定位
//	        webview.getSettings().setCacheMode();
        webview.getSettings().setGeolocationDatabasePath(Filedir.projectavderpath); ;//将图片调整到适合webview的大小
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUseWideViewPort(true) ;//将图片调整到适合webview的大小
        webview.getSettings().setLoadsImagesAutomatically(true) ;//支持自动加载图片
        webview.setDownloadListener(this);
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {  //表示按返回键  时的操作
                        webview.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });


        webview.setWebViewClient(new WebViewClient(){
            ProgressDialog pd = new ProgressDialog(HowGetMoneyActivty.this);

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


        webview.loadUrl(url);

    }

    private void initConsalEvent() {
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        reload.setOnClickListener(this);
        closed.setOnClickListener(this);
    }

    private void initConsalView() {
        back=(ImageView) findViewById(R.id.back);
        forward=(ImageView) findViewById(R.id.forward);
        reload=(ImageView) findViewById(R.id.reload);
        closed=(ImageView) findViewById(R.id.closed);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                webview.goBack();
                break;
            case R.id.forward:
                webview.goForward();
                break;
            case R.id.reload:
                webview.reload();
                break;
            case R.id.closed:
                HowGetMoneyActivty.this.finish();
                overridePendingTransition(R.anim.fadeinfinish, R.anim.fadeoutfinish);
                break;
        }
    }

    /**
     * 按两次键退出
     * fucktion1
     *
     * **/
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次返回首页", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            overridePendingTransition(R.anim.fadeinfinish, R.anim.fadeoutfinish);
//            System.exit(0);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webview.onPause();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        webview.destroy();
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

