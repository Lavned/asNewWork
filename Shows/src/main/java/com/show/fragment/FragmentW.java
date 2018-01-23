package com.show.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.CircleImageView;
import com.com.shows.utils.ClickUtils;
import com.shows.allactivty.MyCrowFundingActivity;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.allactivty.AboutSHowsActivity;
import com.shows.allactivty.AppSettingActivty;
import com.shows.allactivty.FeedbackActivity;
import com.shows.allactivty.LoginActivity;
import com.shows.allactivty.MyMoneyActiviity;
import com.shows.allactivty.MyOrderActiviity;
import com.shows.allactivty.MyReleaseActiviity;
import com.shows.allactivty.RedigerActivity;
import com.shows.allactivty.UserDetailActivity;
import com.shows.photos.UploadFileTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class FragmentW extends Fragment {

    private View v;

    //gridview相关变量
    private String texts[] = null;
    private int images[] = null;
    private int updates[] = null;
    private int noupdates[] = null;

    CircleImageView photo;

    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    TextView name;
    Bitmap bitmap;

    private  String updateTag;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragmentmy, null, false);

        reFreshInitViewData();

        return v;
    }

    /**
     * 刷新数据
     */
    private void reFreshInitViewData() {
        ShareSDK.initSDK(getActivity(), "1151a7fd141ef");

        Asynphoto();

        photo = (CircleImageView) v.findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                jumpNextPage();
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserDetailActivity.class);
                startActivity(intent);
            }
        });

        hPreferences =getActivity().getSharedPreferences("uid_pref", getActivity().MODE_PRIVATE);


        name= (TextView) v.findViewById(R.id.name);
        name.setText(hPreferences.getString("name", ""));
        Log.i("namename", hPreferences.getString("name", "") + "name");

        initGridView();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            //界面显示
            reFreshInitViewData();
        }
        else
        {
            //yingc

        }
    }

    /**
     * huouq tuouxiang
     */
    private void getBitmapFromSharedPreferences(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("uid_pref", Context.MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString=sharedPreferences.getString("head", "");
        //第二步:利用Base64将字符串转换为ByteArrayInputStream
        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        //第三步:利用ByteArrayInputStream生成Bitmap
        bitmap=BitmapFactory.decodeStream(byteArrayInputStream);
        photo.setImageBitmap(bitmap);
    }

    //异步加载图片
    private void Asynphoto() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.wawatwo)
                .showImageForEmptyUri(R.mipmap.wawatwo)
                .showImageOnFail(R.mipmap.wawatwo)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }


    @Override
    public void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
//            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        super.onDestroy();
    }

//    @Override
//    public void onStop() {
//        if (bitmap != null && !bitmap.isRecycled()) {
//            // 回收并且置为null
//            bitmap.recycle();
//            bitmap = null;
//        }
//        System.gc();
//        Log.i("wwww", "hhh");
//        super.onStop();
//    }

    @Override
    public void onResume() {
        name.setText(hPreferences.getString("name", ""));
        if(hPreferences.getString("head","").equals("")){
            photo.setBackgroundResource(R.mipmap.wawatwo);
        }
        if(hPreferences.getString("head","").contains("http")){
            Log.i("touxiang",hPreferences.getString("head",""));
            imageLoader.displayImage(hPreferences.getString("head",""), photo, options);
        }else{
//            Log.i("touxiang22",hPreferences.getString("head",""));
            getBitmapFromSharedPreferences();
        }
        super.onResume();
//        getBitmapFromSharedPreferences();
    }


    /**
     * 跳转下一个页面
     */
    //登录相关
    SharedPreferences mPreferences,hPreferences,updatePreferences;
    Boolean isLogin;
    private void jumpNextPage() {
        // 判断之前有没有
        Log.e(">>>>>>>>>>",isLogin+"");
        mPreferences =getActivity().getSharedPreferences("first_pref", getActivity().MODE_PRIVATE);
        isLogin = mPreferences.getBoolean("isFirstIn", true);
        if(isLogin) {
            Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(getActivity(), "登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getActivity(), RedigerActivity.class);
            startActivity(intent);
        }

    }
    /**
     * 菜单
     */
    private void initGridView() {
        images=new int[]{R.drawable.w_myorder,
                R.drawable.w_wallet, R.drawable.w_ssue,R.drawable.w_crowfund,
                R.drawable.w_aboutprep,
                R.drawable.w_share, R.drawable.w_feedback,R.drawable.w_set};
        updates=new int[]{R.drawable.bdd,
                R.drawable.bdd, R.drawable.bdd,
                R.drawable.bdd,
                R.drawable.bdd,
                R.drawable.bdd,
                R.drawable.bdd, R.drawable.hdd};
        noupdates=new int[]{R.drawable.bdd,
                R.drawable.bdd, R.drawable.bdd,
                R.drawable.bdd, R.drawable.bdd,
                R.drawable.bdd,
                R.drawable.bdd, R.drawable.bdd};
        texts = new String[]{ "我的订单", "我的钱包",
                "我的发布","我的夺宝记录","关于首媒秀","邀请好友一起玩", "问题反馈",
                "设置"};

        /**
         * 判断是否有更新
         */
        updatePreferences = getActivity().getSharedPreferences("update_pref",getActivity().MODE_PRIVATE);
        Log.i("update_pref",updatePreferences.getString("isupdate","")+"isupdate");

        ListView gridview = (ListView) v.findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 8; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", images[i]);
            map.put("itemText", texts[i]);

            if(updatePreferences.getString("isupdate","").contains("true")){
                map.put("itemupdate",updates[i]);
                updateTag ="true";
            }else{
                map.put("itemupdate",noupdates[i]);
                updateTag = "false";
            }

            lstImageItem.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
                lstImageItem,// 数据源
                R.layout.grideview_item,// 显示布局
                new String[] { "itemImage", "itemText" ,"itemupdate"},
                new int[] { R.id.itemImage, R.id.itemText ,R.id.itemupdate});
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(new ItemClickListener());
    }
    class ItemClickListener implements AdapterView.OnItemClickListener {
        /**
         * 点击项时触发事件
         * @param parent   发生点击动作的AdapterView
         * @param view     在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
         * @param position 视图在adapter中的位置。
         * @param rowid    被点击元素的行id。
         */
        @SuppressWarnings("deprecation")
        public void onItemClick(AdapterView<?> parent, View view, int position, long rowid) {
            if(ClickUtils.isFastDoubleClick()){
                return;
            }else {
                HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
                //获取数据源的属性值
                String itemText = (String) item.get("itemText");
                Object object = item.get("itemImage");
                //根据图片进行相应的跳转
                Intent intent;
                switch (texts[position]) {
                    case "问题反馈":
                        //问题反馈
                        startActivity(new Intent(getActivity(), FeedbackActivity.class));
                        break;
                    case "设置":

                        Intent in = new Intent(getActivity(), AppSettingActivty.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("tag",updateTag);
                        in.putExtras(bundle);
                        startActivity(in);
                        saveUpdateInfo();
                        initGridView();
                        break;
                    case "关于首媒秀":
                        startActivity(new Intent(getActivity(), AboutSHowsActivity.class));
                        break;
                    case "我的钱包":
                        startActivity(new Intent(getActivity(), MyMoneyActiviity.class));
                        break;
                    case "我的夺宝记录":
                        startActivity(new Intent(getActivity(), MyCrowFundingActivity.class));
                        break;
                    case "我的订单":
                        startActivity(new Intent(getActivity(), MyOrderActiviity.class));
                        break;
                    case "我的发布":
                        startActivity(new Intent(getActivity(), MyReleaseActiviity.class));
                        break;
                    case "邀请好友一起玩" :
                        showShare();
                }
            }
        }
    }


    /**
     *保存信息并提交
     */
    private void saveUpdateInfo() {
        SharedPreferences uid_preferences = getActivity().getSharedPreferences(
                "update_pref", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor uid_editor = uid_preferences.edit();
        uid_editor.putString("isupdate", "false") ;
        uid_editor.commit();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        ShareSDK.initSDK(getActivity());

        String ShareUrl ="http://a.app.qq.com/o/simple.jsp?pkgname=com.yixinke.shows";

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("首媒秀");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(ShareUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("首媒秀下载地址   "+ ShareUrl);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/Download/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://test2p-10000002.image.myqcloud.com/0de0dcd0-9838-43c3-a5b4-41f8174907ee");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("首媒秀");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(ShareUrl);

        // 启动分享GUI
        oks.show(getActivity());
    }

}
