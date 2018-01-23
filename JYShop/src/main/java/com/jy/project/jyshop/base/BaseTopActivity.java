package com.jy.project.jyshop.base;


import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


public class BaseTopActivity extends BaseFragmentActivity {
	
	protected LinearLayout llTopBack;
	protected TextView tvTopTitle;
	protected Button btnTopRight1;
	protected Button btnTopRight2;
	protected CheckBox btnTopRight3;
	
	protected void initTopBar(String title) {
//		llTopBack = (LinearLayout) findViewById(R.id.llTopBack);
//		tvTopTitle = (TextView) findViewById(R.id.tvTopTitle);

		tvTopTitle.setText(title);
		
//		llTopBack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
	}
}
