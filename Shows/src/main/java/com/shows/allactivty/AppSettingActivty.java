package com.shows.allactivty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.com.shows.utils.DataCleanManager;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

public class AppSettingActivty extends BaseActivity implements View.OnClickListener{

    TextView t_setting,t_version,t_user_help,t_cache,t_free;
    Button exitlogin;

    private String tag ;
    private ImageView tagImage;
    private RelativeLayout re_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_app_setting_activty);

        initView();


        Intent intent= getIntent();
        Bundle bundle= intent.getExtras();
        tag = bundle.getString("tag");
        if(tag.equals("true")){
            tagImage.setVisibility(View.VISIBLE);
        }else{
            tagImage.setVisibility(View.GONE);
        }


    }

    /**
     * 初始化
     */
    private void initView() {
        re_version = (RelativeLayout) findViewById(R.id.version);
        re_version.setOnClickListener(this);
        tagImage = (ImageView) findViewById(R.id.updatetag);
        t_setting = (TextView) findViewById(R.id.set_setting);
        t_setting.setOnClickListener(this);
        t_free = (TextView)findViewById(R.id.t_free);
        t_free.setOnClickListener(this);
        t_version = (TextView) findViewById(R.id.set_version);
        t_version.setOnClickListener(this);
        t_user_help = (TextView) findViewById(R.id.set_userhelp);
        t_user_help.setOnClickListener(this);
        t_cache = (TextView) findViewById(R.id.set_cache);
        t_cache.setOnClickListener(this);
        exitlogin = (Button) findViewById(R.id.exit_login);
        exitlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_setting :
                break;
            case R.id.set_userhelp :
                startActivity(new Intent(AppSettingActivty.this, HowGetMoneyActivty.class));
                break;
            case R.id.version :
                Intent intent_version = new Intent(AppSettingActivty.this, FreeAndVersionActivity.class);
                Bundle bundles = new Bundle();
                bundles.putString("type", "version");
                if(tag.equals("true")){
                    bundles.putString("tag", "updates");
                }else{
                    bundles.putString("tag","noupdate");
                }
                intent_version.putExtras(bundles);
                startActivity(intent_version);
                tagImage.setVisibility(View.GONE);
//                finish();
                break;
            case R.id.set_cache :
                new AlertDialog.Builder(AppSettingActivty.this)
                        .setMessage("确定要清除缓存吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                DataCleanManager.cleanInternalCache(getApplicationContext());
                                final ProgressDialog progressDialog = new ProgressDialog(AppSettingActivty.this);
                                progressDialog.setMessage("正在清除...");
                                progressDialog.show();
                                Thread t = new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                        try {
                                            Thread.sleep(10000);//让他显示10秒后，取消ProgressDialog
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                             progressDialog.dismiss();
                                        }
                                    });
                                t.start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.exit_login :
                new AlertDialog.Builder(AppSettingActivty.this)
                        .setMessage("确定要注销吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                DataCleanManager.cleanInternalCache(getApplicationContext());
                                final ProgressDialog progressDialog = new ProgressDialog(AppSettingActivty.this);
                                SharedPreferences sp = getSharedPreferences("first_pref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();
                                SharedPreferences spp = getSharedPreferences("uid_pref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editors = spp.edit();
                                editors.clear();
                                editors.commit();
                                Intent intent = new Intent(AppSettingActivty.this, ChooseLoginActivty.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//别忘了这行，否则退出不起作用
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.t_free :
                Intent intent_free = new Intent(AppSettingActivty.this, FreeAndVersionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "free");
                intent_free.putExtras(bundle);
                startActivity(intent_free);
//                finish();
                break;
        }
    }


}
