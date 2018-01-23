package com.shows.allactivty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

public class CommentActivity extends BaseActivity {
    TextView comm_text,comm_title;
    ImageView comm_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);

        // 参数传值
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int flag = bundle.getInt("isflag");
        comm_title = (TextView) findViewById(R.id.comm_title);
        comm_text= (TextView) findViewById(R.id.comm_text);
        //传值判断
        if(flag ==1){
            comm_title.setText("关于安全");
            comm_text.setText(R.string.safe_http);
        }else if(flag ==2){
            comm_title.setText("用户协议");
            comm_text.setText(R.string.about_http_text);
        } else if(flag ==3){
            comm_title.setText("开发者信息");
            comm_text.setVisibility(View.INVISIBLE);
            LinearLayout ll= (LinearLayout) findViewById(R.id.aboutdeveloper);
            ll.setVisibility(View.VISIBLE);
        }

        //返回的方法
        comm_back = (ImageView) findViewById(R.id.comm_back);
        comm_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
