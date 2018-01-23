package com.shows.notifacation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

public class NocatifactionDetail extends BaseActivity {

    TextView noca_detail_title,noca_detail_content,noca_detail_stime;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nocatifaction_detail);


        initData();

    }

    private void initData() {
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            noca_detail_title= (TextView) findViewById(R.id.noca_detail_title);
            noca_detail_content= (TextView) findViewById(R.id.noca_detail_content);
            noca_detail_stime= (TextView) findViewById(R.id.noca_detail_stime);

            noca_detail_title.setText(bundle.getString("title"));
            noca_detail_content.setText(bundle.getString("content"));
            noca_detail_stime.setText(bundle.getString("stime"));

            back = (ImageView) findViewById(R.id.noca_detail_back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
