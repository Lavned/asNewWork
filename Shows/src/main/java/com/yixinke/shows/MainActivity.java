package com.yixinke.shows;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.com.shows.utils.ExampleUtil;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.LocationService;
import com.com.shows.utils.UpdateManager;
import com.com.shows.utils.Utilty;
import com.show.fragment.FragmentH;
import com.show.fragment.FragmentO;
import com.show.fragment.FragmentS;
import com.show.fragment.FragmentW;
import com.shows.bean.Picture;
import com.shows.bean.VersionInfo;
import com.shows.view.HeiaiApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MainActivity extends FragmentActivity implements View.OnClickListener{


    private Context mContext;// 上下文

    private FragmentManager fragmentManager;

    private ImageView tS_image;
    private ImageView tH_image;
    private ImageView tO_image;
    private ImageView tW_image;
    ImageView s_image_01;

    TextView text_s,text_h,text_o,text_w;
    private RelativeLayout re_s,re_h,re_o,re_w;

    private LocationService locationService;
    public Vibrator mVibrator;
    public static boolean isForeground = false;

    private static final String TAG = "JPush";
    private static final int MSG_SET_ALIAS = 1001;

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

        setContentView(R.layout.activity_main);
        mContext = this;

        initView();

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        tel = mPreferences.getString("telephone", "");
        Log.i("tel", tel);


        updatePrrferences = getSharedPreferences("update_pref", MODE_PRIVATE);
        isaftertime = updatePrrferences.getString("isaftertime", "");
        Log.i("isaftertime", isaftertime + "isaftertime");

        ((HeiaiApplication) getApplication()).addActivity(this);

        JPushInterface.setDebugMode(true); 	//
        // 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());
        init();
        registerMessageReceiver();
        setAlias();

        myHandler = new MyHandler();



        initFragment();
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());



        fragmentManager = getSupportFragmentManager();
        changeFragment(fragments[0], fragmentTag[0]);
       /* FragmentS fragmentS = new FragmentS();
        fragmentManager.beginTransaction().replace(R.id.myframeLayout, fragmentS).commit();*/

        locationService.start();



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

        if(isaftertime.equals("true")){
            version_update.setVisibility(View.VISIBLE);
            saveUpdateInfo();
        }else{
            version_update.setVisibility(View.GONE);
        }

    }

    class MyHandler extends Handler {
        public MyHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("MyHandler", "handleMessage......");
            try{
                if(msg.what ==1){
                    // 此处可以更新UI
                    int versionCode = UpdateManager.getVersionCode(MainActivity.this);
                    if(versionCode > versionInfo.getVersionCode()){
                        SharedPreferences sp = getSharedPreferences("update_pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.commit();
                    }
                    UpdateManager.checkUpdate(MainActivity.this,versionInfo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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



    @Override
    public void finish() {
        ((HeiaiApplication) getApplication()).removeActivty(this);
        super.finish();
    }

    private void setAlias(){

        String alias = tel;
        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
            //打印别名
//            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(String str) {
        try {
//            if (LocationResult != null)
//                LocationResult.setText(str);
//            Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this,"您现在的位置处于兰州西固区康乐路",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
//        startLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
//                    locationService.start();// 定位SDK
//                    // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
//                    startLocation.setText(getString(R.string.stoplocation));
//                } else {
//                    locationService.stop();
//                    startLocation.setText(getString(R.string.startlocation));
//                }
//            }
//        });
    }


    /*****
     * @see
     * ，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                logMsg(sb.toString());
            }
        }

    };


//
//    public void getLocationInfo() {
//       if(locationManager == null){
//           locationManager = (LocationManager)
//                    getSystemService(Context.LOCATION_SERVICE);
//           }
//       Criteria criteria = new Criteria();
//       criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置为最大精度
//       criteria.setAltitudeRequired(false); // 不要求海拔信息
//       criteria.setCostAllowed(true);//是否允许付费
//       criteria.setPowerRequirement(Criteria.POWER_LOW); // 对电量的要求
//       criteria.setBearingRequired(false); // 不要求Bearing信息
//       String bestProvider = locationManager.getBestProvider(criteria, true);
//       Log.i("dinwefgi", "bestProvider=" + bestProvider);
//       Location location = locationManager.getLastKnownLocation(bestProvider);
//       updateWithNewLocation(location);
//       locationManager.requestLocationUpdates(bestProvider, 1000, 2,mLocationListener);//1秒，2米
//       }
//   LocationListener mLocationListener = new LocationListener() {
//       @Override
//       public void onLocationChanged(Location location) {
//           if(locationManager != null){
//               locationManager.removeUpdates(mLocationListener);//我这里，只需要定位一次就可以了
//               }
//           updateWithNewLocation(location);
//           }
//       @Override
//       public void onStatusChanged(String provider, int status, Bundle extras)
//        {
//           Log.i("onStatusChanged", "onStatusChanged");
//           }
//       @Override
//       public void onProviderEnabled(String provider) {
//           Log.i("onProviderEnabled", "onProviderEnabled");
//           }
//       @Override
//       public void onProviderDisabled(String provider) {
//           Log.i("onProviderDisabled", "onProviderDisabled");
//           }
//       };
//   private void updateWithNewLocation(Location location){
//       if (location != null) {
//           double latitude = location.getLatitude(); // 经度
//           double longitude = location.getLongitude(); // 纬度
//           Log.i("DINGWEIXIN", "latitude " + latitude + " longitude:" + longitude+"位置"+location.getProvider());
//           Toast.makeText(MainActivity.this,"latitude " + latitude + " longitude:" + longitude+"位置"
//                   +location.getAltitude(),Toast.LENGTH_SHORT).show();
//           ///UtilWidget.showToast(this, "Latitude :" +
////            location.getLatitude()+""+"Longitude :" + location.getLatitude();
//        }else{
//           Log.v("JJJ", "don't know location info");
//           Toast.makeText(MainActivity.this,"wufahuoqu'",Toast.LENGTH_SHORT).show();
//       }
//       }
//
    
    
    /**
     * 推送相关、、、、推送初始化
     */
    private void init(){
        JPushInterface.init(getApplicationContext());
    }
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    //注册推送信息
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                String title = intent.getStringExtra(KEY_TITLE);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    Log.i("showMsg",showMsg.toString());
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
//        unregisterReceiver(mMessageReceiver);
        System.exit(0);
        super.onDestroy();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化
     */
    private void initView() {
        version_update = (ImageView) findViewById(R.id.version_update);

        re_s = (RelativeLayout) findViewById(R.id.re_s);
        re_h = (RelativeLayout) findViewById(R.id.re_h);
        re_o = (RelativeLayout) findViewById(R.id.re_o);
        re_w = (RelativeLayout) findViewById(R.id.re_w);


        tS_image = (ImageView) findViewById(R.id.s_image);
        tH_image = (ImageView) findViewById(R.id.h_image);
        tO_image = (ImageView) findViewById(R.id.o_image);
        tW_image = (ImageView) findViewById(R.id.w_image);

//        s_image_01 = (ImageView) findViewById(R.id.s_image01);

        text_s = (TextView) findViewById(R.id.text_s);
        text_h = (TextView) findViewById(R.id.text_h);
        text_o = (TextView) findViewById(R.id.text_o);
        text_w = (TextView) findViewById(R.id.text_w);

        re_s.setOnClickListener(this);
        re_h.setOnClickListener(this);
        re_o.setOnClickListener(this);
        re_w.setOnClickListener(this);
    }
    FragmentS fragment1;
    FragmentH fragment2;
    FragmentO fragment3;
    FragmentW fragment4;

    private Fragment[] fragments;
    private String[] fragmentTag = { "1", "2", "3", "4"};
    private void initFragment()
    {
        fragments = new Fragment[5];
        fragments[0] = new FragmentS();
        fragments[1] = new FragmentH();
        fragments[2] = new FragmentO();
        fragments[3] = new FragmentW();
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     * @param tag
     */
    private void changeFragment(Fragment fragment, String tag)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAll(transaction);

        Fragment fragment1 = fragmentManager.findFragmentByTag(tag);
        if (fragment1 == null)
        {
            fragment1 = fragment;
            transaction.add(R.id.myframeLayout, fragment1, tag);
        }
        else
        {
            transaction.show(fragment1);
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏所有Fragment
     *
     * @param transaction
     */
    private void hideAll(FragmentTransaction transaction)
    {
        for (int i = 0; i < fragmentTag.length; i++)
        {
            Fragment fragment = fragmentManager
                    .findFragmentByTag(fragmentTag[i]);
            if (fragment != null)
            {
                transaction.hide(fragment);
            }
        }
    }
    @Override
    public void onClick(View view) {
        clearBackground();
        switch (view.getId()) {
            case R.id.re_s:
                tS_image.setImageResource(R.mipmap.xstu);
                text_s.setTextColor(Color.parseColor("#0093DD"));
                changeFragment(fragments[0], fragmentTag[0]);
                /*if(fragment1 == null)
                    fragment1 = new FragmentS();
                fragmentManager.beginTransaction().replace(R.id.myframeLayout, fragment1).commit();*/
                break;
            case R.id.re_h:
                tH_image.setImageResource(R.mipmap.xhtu);
                text_h.setTextColor(Color.parseColor("#0093DD"));
               /* if(fragment2 == null)
                    fragment2 = new FragmentH();
                fragmentManager.beginTransaction().replace(R.id.myframeLayout, fragment2).commit();*/
                changeFragment(fragments[1], fragmentTag[1]);
                break;
            case R.id.re_o:
                tO_image.setImageResource(R.mipmap.xotu);
                text_o.setTextColor(Color.parseColor("#0093DD"));
                changeFragment(fragments[2], fragmentTag[2]);
                /*if(fragment3 == null)
                    fragment3 = new FragmentO();
                fragmentManager.beginTransaction().replace(R.id.myframeLayout, fragment3).commit();*/
                break;
            case R.id.re_w:
                tW_image.setImageResource(R.mipmap.xwtu);
                text_w.setTextColor(Color.parseColor("#0093DD"));
                /*if(fragment4 == null) fragment4 = new FragmentW();
                fragmentManager.beginTransaction().replace(R.id.myframeLayout, fragment4).commit();*/
                changeFragment(fragments[3], fragmentTag[3]);
//                saveUpdateInfo();
                version_update.setVisibility(View.GONE);
                break;
        }
    }

    private String initCheckUpdate() {
        String requestUrl= HttpAddress.ADDRESSHTTP+"/version/getVersion.do";
        String checkresultStr = HttpUtils.getHttp(requestUrl);
        Log.i("checkresultStr", checkresultStr);
        return checkresultStr;
    }


    /**
     *保存信息并提交
     */
    public void saveUpdateInfo() {
       SharedPreferences uid_preferences = getApplicationContext().getSharedPreferences(
                "update_pref", MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("isupdate", "true") ;
        uid_editor.commit();
    }

    /**
     * 清除背景色
     */

    private void clearBackground() {
        tS_image.setImageResource(R.mipmap.stu);
        text_s.setTextColor(Color.parseColor("#80000000"));
        tH_image.setImageResource(R.mipmap.htu);
        text_h.setTextColor(Color.parseColor("#80000000"));
        tO_image.setImageResource(R.mipmap.otu);
        text_o.setTextColor(Color.parseColor("#80000000"));
        tW_image.setImageResource(R.mipmap.wtu);
        text_w.setTextColor(Color.parseColor("#80000000"));
    }
}
