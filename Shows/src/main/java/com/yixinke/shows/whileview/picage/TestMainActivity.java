package com.yixinke.shows.whileview.picage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yixinke.shows.R;

public class TestMainActivity extends Activity {

	private TextView mBirth;
	private TextView mAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		mBirth = (TextView) findViewById(R.id.tv_birth);
//		mAddress = (TextView) findViewById(R.id.tv_address);

		mBirth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChangeBirthDialog mChangeBirthDialog = new ChangeBirthDialog(
						TestMainActivity.this);
				mChangeBirthDialog.setDate(2016, 03);
				mChangeBirthDialog.show();
				mChangeBirthDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {

					@Override
					public void onClick(String year, String month, String day) {
						// TODO Auto-generated method stub
						Toast.makeText(TestMainActivity.this,
								year + "-" + month ,
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});

//		mAddress.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(
//						TestMainActivity.this);
//				mChangeAddressDialog.setAddress("四川", "自贡");
//				mChangeAddressDialog.show();
//				mChangeAddressDialog
//						.setAddresskListener(new ChangeAddressDialog.OnAddressCListener() {
//
//							@Override
//							public void onClick(String province, String city) {
//								// TODO Auto-generated method stub
//								Toast.makeText(TestMainActivity.this,
//										province + "-" + city,
//										Toast.LENGTH_LONG).show();
//							}
//						});
//			}
//		});
	}
}
