package com.yixinke.shows;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.allactivty.SurfaceViewActivity;
import com.shows.view.BaseActivity;
import com.shows.view.MyGridView;
import com.shows.zxing.test.ZxingMainActivity;

public class VideoInfoDetailActivity extends BaseActivity implements View.OnClickListener{

    private TextView smx_title;
    private RelativeLayout videoplayclick;
    private Intent intent;
    private Bundle bundle;
    Bundle bundles;
    private  TextView tv_title,tv_detail,tv_tel,tv_address,tv_traffic,tv_http_address;
    private ImageView iv_code;
    // 小图标
    private ImageView im_telephone,im_message,im_back,im_savetel;
    private ImageView videoimages;
    private String recode;

    //图片加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_info_detail);
        smx_title = (TextView) findViewById(R.id.smx_title);
        smx_title.setText("广告详情");

        Intent intes = getIntent();
        bundles = intes.getExtras();
        recode = bundles.getString("code");
        Log.i("xxxxxxxx",recode+"kk");

        ImageLoadInfo();

        initView();

    }


    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(VideoInfoDetailActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.czhanweitu)
                .showImageForEmptyUri(R.mipmap.czhanweitu)
                .showImageOnFail(R.mipmap.czhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 初始化
     */
    private void initView() {
        videoimages = (ImageView) findViewById(R.id.videoimages);
        im_message = (ImageView) findViewById(R.id.im_message);
        im_telephone = (ImageView) findViewById(R.id.im_telephone);
        im_savetel = (ImageView) findViewById(R.id.im_savetel);
        im_savetel.setOnClickListener(this);
        im_message.setOnClickListener(this);
        im_telephone.setOnClickListener(this);

        iv_code = (ImageView) findViewById(R.id.im_adv_recode);
        iv_code.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_adv_name);
        tv_detail = (TextView) findViewById(R.id.tv_adv_detail);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_traffic = (TextView) findViewById(R.id.tv_traffic);
        tv_tel = (TextView) findViewById(R.id.tv_telephone);
        tv_http_address = (TextView) findViewById(R.id.tv_http_address);
        tv_http_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_http_address.getText().toString().contains("暂无网址")) {
                    return;
                } else {
                    Uri uri = Uri.parse(tv_http_address.getText().toString());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }
            }
        });
        tv_title.setText(bundles.getString("title"));
        tv_detail.setText(bundles.getString("content"));
        tv_address.setText(bundles.getString("address"));
        tv_traffic.setText(bundles.getString("route"));
        tv_tel.setText(bundles.getString("tel"));
        tv_http_address.setText(bundles.getString("httpaddress"));


        imageLoader.displayImage(bundles.getString("head"), videoimages, options);

        Glide.with(VideoInfoDetailActivity.this)
                .load(HttpAddress.ADDRESSHTTP + bundles.getString("code"))
                .placeholder(R.mipmap.czhanweitu)
                .error(R.mipmap.czhanweitu)
                .centerCrop()
                .crossFade()
                .into(iv_code);

        videoplayclick = (RelativeLayout) findViewById(R.id.videoplayclick);
        videoplayclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(VideoInfoDetailActivity.this, SurfaceViewActivity.class);
                bundle = new Bundle();
                bundle.putString("videourl",bundles.getString("videourl"));
                bundle.putString("aid",bundles.getString("aid"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    /**
     * exit this activity
     * @param view
     */
    public void finisThis(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.im_adv_recode :
                if(ClickUtils.isFastDoubleClick()){
                    return;
                }else {
                    Intent intents = new Intent(new Intent(VideoInfoDetailActivity.this, ZxingMainActivity.class));
                    Bundle bundles = new Bundle();
                    bundles.putString("codes",HttpAddress.ADDRESSHTTP + recode);
                    intents.putExtras(bundles);
                    startActivity(intents);
                }

                break;
            case R.id.im_savetel :
                Intent it = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
                        Uri.parse("content://com.android.contacts"), "contacts"));
                it.setType("vnd.android.cursor.dir/person");

                // 联系人姓名
                it.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, "");
                // 公司
                it.putExtra(android.provider.ContactsContract.Intents.Insert.COMPANY,
                        bundles.getString("title"));
                // email
//                it.putExtra(android.provider.ContactsContract.Intents.Insert.EMAIL,
//                        "123456@qq.com");
                // 手机号码
                // 手机号码
                it.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,
                        bundles.getString("tel"));
                // 单位电话
//                it.putExtra(
//                        android.provider.ContactsContract.Intents.Insert.SECONDARY_PHONE,
//                        "18600001111");
                // 住宅电话
//                it.putExtra(
//                        android.provider.ContactsContract.Intents.Insert.TERTIARY_PHONE,
//                        "010-7654321");
                // 备注信息
                it.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE,
                        "名片");
                startActivity(it);
                break;
            case R.id.im_telephone :
               Intent intents = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bundles.getString("tel")));
                startActivity(intents);
//                Intent intent=new Intent(Intent.ACTION_EDIT,Uri.parse("content://com.android.contacts/contacts/"+"1"));
//                startActivity(intent);
                break;
            case R.id.im_message :
                Uri smsToUri = Uri.parse("smsto:"+ bundles.getString("tel"));
                Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );
                mIntent.putExtra("sms_body", "");
                startActivity(mIntent);
                break;
        }
    }
}
