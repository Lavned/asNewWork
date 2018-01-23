package com.shows.allactivty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

public class AboutSHowsActivity extends BaseActivity implements View.OnClickListener{

    ImageView back;
    TextView about_security,about_http,about_developer;
    Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_shows);
        initView();
    }

    /**
     * c初始化三个点击文本
     */
    private void initView() {
        about_security = (TextView) findViewById(R.id.about_security);
        about_http = (TextView) findViewById(R.id.about_http);
        about_developer = (TextView) findViewById(R.id.about_eveloper);
        about_developer.setOnClickListener(this);
        about_http.setOnClickListener(this);
        about_security.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.about_back);
        back.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_http :
                intent= new Intent();
                bundle = new Bundle();
                bundle.putInt("isflag", 2);
                intent.putExtras(bundle);
                intent.setClass(this,CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.about_eveloper :
                intent= new Intent();
                bundle = new Bundle();
                bundle.putInt("isflag", 3);
                intent.putExtras(bundle);
                intent.setClass(this,CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.about_security :
                intent= new Intent();
                bundle = new Bundle();
                bundle.putInt("isflag", 1);
                intent.putExtras(bundle);
                intent.setClass(this,CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.about_back:
                finish();
                break;
        }

    }
}
