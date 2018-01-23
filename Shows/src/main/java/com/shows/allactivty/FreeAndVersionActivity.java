package com.shows.allactivty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.UpdateManager;
import com.shows.bean.VersionInfo;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FreeAndVersionActivity extends BaseActivity {

    TextView tree;
    LinearLayout version;
    ImageView frees_back;
    private  TextView downloadUrl;
    private  TextView versionCode;

    private  String tel,isaftertime;
    private SharedPreferences mPreferences,updatePrrferences;
    private ImageView version_update;
    private JSONObject json;
    private VersionInfo versionInfo;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_free_and_version);

        downloadUrl = (TextView) findViewById(R.id.downloadurl);
        versionCode = (TextView) findViewById(R.id.version_code_name);

        tree= (TextView) findViewById(R.id.free_text);
        version = (LinearLayout) findViewById(R.id.version_lin);
        frees_back = (ImageView) findViewById(R.id.frees_back);
        frees_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intet = getIntent();
        Bundle bundle = intet.getExtras();
        myHandler = new MyHandler();
        if(bundle.getString("type").equals("free")){
            tree.setVisibility(View.VISIBLE);
            version.setVisibility(View.GONE);
        }else if(bundle.getString("type").equals("version")){
            version.setVisibility(View.VISIBLE);
            tree.setVisibility(View.GONE);
            if(bundle.getString("tag").equals("noupdate")){
                downloadUrl.setVisibility(View.GONE);
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ends = initCheckUpdate();
                    versionInfo = viewSetText(ends);
                    Message msg = new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
                }
            }).start();
        }

        downloadUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.yixinke.shows");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
    }


    class MyHandler extends Handler {
        public MyHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                if(msg.what ==1){
                    versionCode.setText("当前版本为：" + versionInfo.getVersionName());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String initCheckUpdate() {
        String requestUrl= HttpAddress.ADDRESSHTTP+"/version/getVersion.do";
        String checkresultStr = HttpUtils.getHttp(requestUrl);
        Log.i("checkresultStr", checkresultStr);
        return checkresultStr;
    }

    /**
     * 给文本框设置
     * @param respon
     * @return
     */
    public VersionInfo viewSetText(String respon){
        VersionInfo versionInfo = null;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("version");
//            for (int i =0;i<jsonArray1.length();i++){
            versionInfo =new VersionInfo();
            versionInfo.setVersionCode(jsonArray1.getJSONObject(0).getInt("versioncode"));
            versionInfo.setContent(jsonArray1.getJSONObject(0).getString("content"));
            versionInfo.setDownUrl(jsonArray1.getJSONObject(0).getString("downurl"));
            versionInfo.setVersionName(jsonArray1.getJSONObject(0).getString("versionname"));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  versionInfo;
    }
}
