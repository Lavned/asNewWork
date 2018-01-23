package com.show.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.shows.bean.BussRecommend;
import com.shows.bean.Picture;
import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.bumptech.glide.Glide;
import com.com.shows.utils.CacheUtils;
import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.Utilty;
import com.com.shows.utils.ViewPagerAdapter;
import com.yixinke.shows.InfoDetailsActivity;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.allactivty.BussRecommendActivity;
import com.shows.allactivty.CollectionActiviity;
import com.shows.allactivty.RankingActivity;
import com.shows.allactivty.WeWeatherActivity;
import com.shows.allactivty.XiuCommActivity;
import com.shows.imageload.AbsListViewBaseActivity;
import com.shows.notifacation.NocatifactionActivty;
import com.yixinke.shows.VideoInfoDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class FragmentS extends AbsListViewBaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {

    public static final String PictherUrl = HttpAddress.ADDRESSHTTP + "/advert/advertShow.do";
    private View v;

    private DisplayImageOptions options;
    private ImageView iv_pic;// 显示图片的设置
    private  TextView tv_title,tv_detail,main_title;
    private JSONObject json;
    private List<Picture> newsLists;
    private List<Picture> topLists;
    private List<BussRecommend> RecommenJsonsLists ;
    private List<BussRecommend> RecommenLists ;
    /**
     * 视频播放
     */
    private ListView videoListView;
    private ImageView videoplay,videoback;
    private List<Picture> videoLists;
    private ProgressDialog mprogressDialog;

    private int width;
    private int newWidth;
    private int padding;
    //图片轮播相关
    private ViewPager mViewpager;
    private ViewPagerAdapter mViewPagerAdp;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private LinearLayout layoutDots;
    private final int AUTO = 0;
    private final long delay = 3 * 1000;

    private ArrayList<String>   topImageList = new ArrayList<>();
    MyHandler myHandler;
    NOHandler noHandler;
    private  ListViewAdpater lv;
    private VideoListViewAdpater viewLv;


    //八个菜单初始化
    private TextView s_ranking;
    private TextView s_share;
    static TextView s_message;
    private TextView s_collection;
    private  TextView s_weather;
    private TextView s_publicgao,s_publicgy;

    private  SharedPreferences mPreferences;

    @Override
    public void onResume() {
        super.onResume();
    }

    private String pushInfo;

    private  TextView gonggao;
    private TextView gongyi;
    private LinearLayout comment1,comment2;

    private  ScrollView myScrollView;
    private  String topInfo;

    private  int widths,height;

    private ImageView infotips,gongyitips;
    private  TextView gonggaoText,gongyiText;
    private TextView s_pictheradv,s_viewadv;

    private  TextView  buss_text_title, buss_text_detail;
    private ImageView buss_image_icon;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment01, container, false);

        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        widths = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        myScrollView = (ScrollView) v.findViewById(R.id.scrollView);
        myScrollView.smoothScrollTo(0, 20);

        //请求
        ApiStoreSDK.init(getActivity().getApplicationContext(), "01cacd48ac45b99dc89b10d12cf815ef");
        //分享初始化
        ShareSDK.initSDK(getActivity(), "1151a7fd141ef");
        ImageLoadInfo();

        listView = (ListView) v.findViewById(R.id.listview);
        videoListView = (ListView) v.findViewById(R.id.viewlistview);
        lv = new ListViewAdpater();
        viewLv = new VideoListViewAdpater();
        mprogressDialog = new ProgressDialog(getActivity());
        setViewOfRecommend();


        myHandler = new MyHandler();
        noHandler = new NOHandler(getActivity());

        width = getResources().getDisplayMetrics().widthPixels;
        newWidth = (int) (divideWidth(width, 1080, 6) * 17);
        padding = (int) (divideWidth(width, 1080, 6) * 9);
        // registration view
        mViewpager = (ViewPager) v.findViewById(R.id.show_viewPager);
        layoutDots = (LinearLayout) v.findViewById(R.id.showLayout);
        // set listener
        mViewpager.setOnPageChangeListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                topInfo = initTopAdvert();
//                topLists = viewSetText(topInfo);
                try {
                    CacheUtils.setCache(PictherUrl,
                            topInfo, getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                RecommenLists = getBussJsonText(httpBussResult());
                if(RecommenLists != null && RecommenLists.size()> 0){
                    msg.arg2 = 2;
                }
                myHandler.sendMessage(msg);
            }
        }).start();




        apiTest();

        initEightMenu();
//        initView();
        ClickAndSaveCache();
        ClickItemListView();
        mPreferences =getActivity().getSharedPreferences("push_pref", getActivity().MODE_PRIVATE);
        pushInfo = mPreferences.getString("pushstr", "");
        if(pushInfo.equals("no")){
            s_message.setBackgroundResource(R.mipmap.s_messagedian);
        }else{
            s_message.setBackgroundResource(R.mipmap.s_nomessage);
        }
        initAdv();

        return v;
    }


    /**
     *推荐商家模块
     */
    private void setViewOfRecommend() {
        buss_image_icon  = (ImageView) v.findViewById(R.id.buss_image_icon);
        buss_text_detail  = (TextView) v.findViewById(R.id.buss_text_detail);
        buss_text_title  = (TextView) v.findViewById(R.id.buss_text_title);
    }

    /**
     * 图文视频分类
     */
    private void initAdv() {
        TextView textetxtetx = (TextView) v.findViewById(R.id.textetxtetx);
        textetxtetx.setFocusableInTouchMode(true);
        textetxtetx.setFocusable(true);
        textetxtetx.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textetxtetx.setMarqueeRepeatLimit(-1);
        s_pictheradv = (TextView) v.findViewById(R.id.s_pictheradv);
        s_viewadv = (TextView) v.findViewById(R.id.s_videoadv);
        LinearLayout.LayoutParams lp1s = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1s.width=widths/3+120;
        s_pictheradv.setLayoutParams(lp1s);
        s_viewadv.setLayoutParams(lp1s);

        s_pictheradv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_pictheradv.setBackgroundColor(Color.parseColor("#53B2F2"));
                s_pictheradv.setTextColor(Color.parseColor("#ffffff"));
                s_viewadv.setBackgroundColor(Color.parseColor("#ffffff"));
                s_viewadv.setTextColor(Color.parseColor("#53B2F2"));
                listView.setVisibility(View.VISIBLE);
                videoListView.setVisibility(View.GONE);
                ClickAndSaveCache();

            }
        });
        s_viewadv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_viewadv.setBackgroundColor(Color.parseColor("#53B2F2"));
                s_viewadv.setTextColor(Color.parseColor("#ffffff"));
                s_pictheradv.setBackgroundColor(Color.parseColor("#ffffff"));
                s_pictheradv.setTextColor(Color.parseColor("#53B2F2"));
                listView.setVisibility(View.GONE);
                videoListView.setVisibility(View.VISIBLE);
                mprogressDialog.setMessage("加载中...");
                mprogressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        videoLists = viewSetVideoText(getHttpGetDate());
                        Message msg = new Message();
                        msg.what = 3 ;
                        myHandler.sendMessage(msg);
                    }
                }).start();

                videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent =  new Intent(getActivity(),VideoInfoDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("aid",videoLists.get(position).getId());
                        bundle.putString("address",videoLists.get(position).getAddress());
                        bundle.putString("code",videoLists.get(position).getCode());
                        bundle.putString("route",videoLists.get(position).getRoute());
                        bundle.putString("tel",videoLists.get(position).getTel());
                        bundle.putString("title",videoLists.get(position).getTitle());
                        bundle.putString("head",videoLists.get(position).getHeader());
                        bundle.putString("content",videoLists.get(position).getContent());
                        bundle.putString("httpaddress",videoLists.get(position).getHttpAddress());
                        bundle.putString("videourl",videoLists.get(position).getVideoUrl());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            }
        });
    }


    public String getHttpGetDate(){
        String requstUrl = HttpAddress.ADDRESSHTTP + "/vAdvert/videoAdvert.do";
        String ends = HttpUtils.getHttp(requstUrl);
        return ends;
    }

    public List<Picture> viewSetVideoText(String respon){
        List<Picture> listPic=new ArrayList<Picture>();
        Picture picture=null;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("video");
            for (int i =0;i<jsonArray1.length();i++){
                picture = new Picture();
                picture.setTitle(jsonArray1.getJSONObject(i).getString("title"));
                picture.setContent(jsonArray1.getJSONObject(i).getString("content"));
                picture.setHeader(jsonArray1.getJSONObject(i).getString("head"));
                picture.setTel(jsonArray1.getJSONObject(i).getString("tel"));
                picture.setAddress(jsonArray1.getJSONObject(i).getString("address"));
                picture.setId(jsonArray1.getJSONObject(i).getString("id"));
                picture.setRoute(jsonArray1.getJSONObject(i).getString("route"));
                picture.setHttpAddress(jsonArray1.getJSONObject(i).getString("httpaddress"));
                picture.setCode(jsonArray1.getJSONObject(i).getString("code"));
                picture.setTextDetail(jsonArray1.getJSONObject(i).getString("introduce"));
                picture.setVideoUrl(jsonArray1.getJSONObject(i).getString("videourl"));
                listPic.add(picture);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  listPic;
    }

    /**
     * 公告公益
     */
    private void initPublic() {
        infotips = (ImageView) v.findViewById(R.id.infotips);
        gongyitips = (ImageView) v.findViewById(R.id.gongyitips);
        gonggaoText = (TextView) v.findViewById(R.id.gonggaotext);
        gongyiText = (TextView) v.findViewById(R.id.gongyitext);

        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(500);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        alpha.setRepeatCount(Integer.MAX_VALUE);
        alpha.setRepeatMode(Animation.REVERSE);
        infotips.setAnimation(alpha);
        infotips.startAnimation(alpha);
        gongyitips.setAnimation(alpha);
        gongyitips.startAnimation(alpha);


        comment1 = (LinearLayout) v.findViewById(R.id.commonttext1);
        comment2 = (LinearLayout) v.findViewById(R.id.commonttext2);
        gonggao = (TextView) v.findViewById(R.id.xiuxiugonggao);
        gongyi = (TextView) v.findViewById(R.id.xiuxiugongyi);
        LinearLayout.LayoutParams lp1s = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1s.width=widths/3+120;
        gongyi.setLayoutParams(lp1s);
        gonggao.setLayoutParams(lp1s);

        gonggao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gonggao.setFocusableInTouchMode(false);
//                myScrollView.smoothScrollTo(0, 20);
                gonggao.setBackgroundColor(Color.parseColor("#53B2F2"));
                gonggao.setTextColor(Color.parseColor("#ffffff"));
                gongyi.setBackgroundColor(Color.parseColor("#ffffff"));
                gongyi.setTextColor(Color.parseColor("#53B2F2"));

//                gonggaoText.setFocusableInTouchMode(true);
//                gonggaoText.setFocusable(true);
//                gonggaoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                gonggaoText.setMarqueeRepeatLimit(-1);



                comment2.setVisibility(View.GONE);
                comment1.setVisibility(View.VISIBLE);
            }
        });
        gongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gongyi.setFocusableInTouchMode(false);
//                myScrollView.smoothScrollTo(0, 20);
                gongyi.setBackgroundColor(Color.parseColor("#53B2F2"));
                gongyi.setTextColor(Color.parseColor("#ffffff"));

//                gongyiText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                gongyiText.setMarqueeRepeatLimit(-1);
////                gongyiText.setFocusableInTouchMode(true);
//                gongyiText.setFocusable(true);

                gonggao.setBackgroundColor(Color.parseColor("#ffffff"));
                gonggao.setTextColor(Color.parseColor("#53B2F2"));
                comment1.setVisibility(View.GONE);
                comment2.setVisibility(View.VISIBLE);
            }
        });
        comment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),XiuCommActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type","1");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        comment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),XiuCommActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "2");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    /**
     *菜单
     */
    private void initEightMenu() {
        s_ranking = (TextView) v.findViewById(R.id.s_ranking);
        s_ranking.setOnClickListener(this);
        s_publicgao = (TextView) v.findViewById(R.id.s_publicgao);
        s_publicgao.setOnClickListener(this);
        s_message = (TextView) v.findViewById(R.id.s_message);
        s_message.setOnClickListener(this);
        s_collection = (TextView) v.findViewById(R.id.s_collection);
        s_collection.setOnClickListener(this);
        s_publicgy = (TextView) v.findViewById(R.id.s_publicgy);
        s_publicgy.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.s_ranking :
                startActivity(new Intent(getActivity(), RankingActivity.class));
                break;
            case R.id.s_publicgao :
                if(ClickUtils.isFastDoubleClick()){
                    return;
                }else {
                    Intent intent = new Intent(getActivity(),XiuCommActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type","1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.s_message :
                startActivity(new Intent(getActivity(), NocatifactionActivty.class));
                s_message.setBackgroundResource(R.mipmap.s_nomessage);
                SharedPreferences sp = getActivity().getSharedPreferences("push_pref", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                break;
            case R.id.s_collection :
                startActivity(new Intent(getActivity(), CollectionActiviity.class));
                break;
            case R.id.s_publicgy :
                Intent intent = new Intent(getActivity(),XiuCommActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "2");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }

    }

    private void ClickItemListView() {
        /**
         * listview的点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(ClickUtils.isFastDoubleClick()){
                   return;
                }else{
                    IntentDetailString(i);
                }

            }
        });
    }

    /**
     * 跳转传值
     * @param i
     */
    private void IntentDetailString(int i) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("title", newsLists.get(i).getTitle());
        bundle.putString("content", newsLists.get(i).getContent());
        bundle.putString("header", newsLists.get(i).getHeader());
        bundle.putString("tel", newsLists.get(i).getTel());
        bundle.putString("address", newsLists.get(i).getAddress());
        bundle.putString("id", newsLists.get(i).getId());
        bundle.putString("route", newsLists.get(i).getRoute());
        bundle.putStringArrayList("picsss", (ArrayList<String>) newsLists.get(i).getPics());
        bundle.putString("code", newsLists.get(i).getCode());
        bundle.putString("httpaddress",newsLists.get(i).getHttpAddress());
        bundle.putString("introduce",newsLists.get(i).getTextDetail());
        bundle.putStringArrayList("imagesurl", (ArrayList<String>) newsLists.get(i).getImages());
        intent.putExtras(bundle);
        intent.setClass(getActivity(), InfoDetailsActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化ViewPager的底部小点
     * */
    private void initDots() {
        mDots = new ImageView[topImageList.size()];

        for (int i = 0; i < topImageList.size(); i++) {
            ImageView iv = new ImageView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newWidth, newWidth);
            lp.leftMargin = padding;
            lp.rightMargin = padding;
            lp.topMargin = padding;
            lp.bottomMargin = padding;
            iv.setLayoutParams(lp);
            iv.setImageResource(R.mipmap.dot_normal);
            layoutDots.addView(iv);
            mDots[i] = iv;
        }
        mDots[0].setImageResource(R.mipmap.dot_selected);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            //界面显示
        }
        else
        {
            //yingc

        }
    }

    /**
     *         初始化ViewPager
     * */
    private void initViewPager() {
//
        mImageViews = new ImageView[2][];
        mImageViews[0] = new ImageView[topImageList.size()];
        mImageViews[1] = new ImageView[topImageList.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            for (int j = 0; j < mImageViews[i].length; j++) {
                ImageView iv = new ImageView(getActivity());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getActivity())
                        .load(topImageList.get(j))
                        .placeholder(R.mipmap.czhanweitu)
                        .error(R.mipmap.czhanweitu)
                        .centerCrop()
                        .crossFade()
                        .into(iv);
                mImageViews[i][j]= iv;
                final int finalJ = j;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ClickUtils.isFastDoubleClick()){
                            return;
                        }else {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", topLists.get(finalJ).getTitle());
                            bundle.putString("content", topLists.get(finalJ).getContent());
                            bundle.putString("header", topLists.get(finalJ).getHeader());
                            bundle.putString("tel", topLists.get(finalJ).getTel());
                            bundle.putString("address", topLists.get(finalJ).getAddress());
                            bundle.putString("id", topLists.get(finalJ).getId());
                            bundle.putString("route", topLists.get(finalJ).getRoute());
                            bundle.putStringArrayList("picsss", (ArrayList<String>) topLists.get(finalJ).getPics());
                            bundle.putString("code", topLists.get(finalJ).getCode());
                            bundle.putString("httpaddress", topLists.get(finalJ).getHttpAddress());
                            bundle.putString("introduce", topLists.get(finalJ).getTextDetail());
                            bundle.putStringArrayList("imagesurl", (ArrayList<String>) topLists.get(finalJ).getImages());
                            intent.putExtras(bundle);
                            intent.setClass(getActivity(), InfoDetailsActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        mViewPagerAdp = new ViewPagerAdapter(getActivity(),mImageViews, topImageList);//topImageList 这个是图片url?enen
        mViewpager.setAdapter(mViewPagerAdp);
        mViewpager.setCurrentItem(topImageList.size() * 50);
        mHandler.sendEmptyMessageDelayed(AUTO, delay);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {

            switch (msg.what) {
                case AUTO:
                    int index = mViewpager.getCurrentItem();
                    mViewpager.setCurrentItem(index + 1);
                    mHandler.sendEmptyMessageDelayed(AUTO, delay);
                    break;
                default:
                    break;
            }
        };
    };


    private String initTopAdvert() {
        String requestUrl=HttpAddress.ADDRESSHTTP+"/advert/advertShow.do";
        String resultStr = HttpUtils.getHttp(requestUrl);
        Log.i("resultStr", resultStr);
        return resultStr;
    }


    /**
     * 给文本框设置
     * @param respon
     * @return
     */
    public List<Picture> viewSetText(String respon){
        List<Picture> listPic=new ArrayList<Picture>();
        Picture picture=null;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("advert");
            for (int i =0;i<jsonArray1.length();i++){
                picture = new Picture();
                picture.setTitle(jsonArray1.getJSONObject(i).getString("title"));
                picture.setContent(jsonArray1.getJSONObject(i).getString("content"));
                picture.setHeader(HttpAddress.ADDRESSHTTP + jsonArray1.getJSONObject(i).getString("head"));
                picture.setTel(jsonArray1.getJSONObject(i).getString("tel"));
                picture.setAddress(jsonArray1.getJSONObject(i).getString("address"));
                picture.setId(jsonArray1.getJSONObject(i).getString("id"));
                picture.setRoute(jsonArray1.getJSONObject(i).getString("route"));
                picture.setHttpAddress(jsonArray1.getJSONObject(i).getString("httpaddress"));
                picture.setCode(jsonArray1.getJSONObject(i).getString("code"));
                picture.setTextDetail(jsonArray1.getJSONObject(i).getString("introduce"));
                JSONArray jsonArrayImages =  jsonArray1.getJSONObject(i).getJSONArray("pictures");
                List<String> iamgeList=new ArrayList<String>();
                for (int j = 0;j < jsonArrayImages.length();j++){
                    String  pic  =jsonArrayImages.getString(j);
                    iamgeList.add(HttpAddress.ADDRESSHTTP+ pic);
                }
                JSONArray jsonArraytemp =  jsonArray1.getJSONObject(i).getJSONArray("pics");//
                List<String> picList=new ArrayList<String>();
                for (int j = 0;j < jsonArraytemp.length();j++){
                    String  pic  =jsonArraytemp.getString(j);
                    picList.add(HttpAddress.ADDRESSHTTP + pic);
                }
                picture.setPics(picList);
                picture.setImages(iamgeList);
                listPic.add(picture);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  listPic;
    }

    /**
     * 初始化界面
     */
    private void initView() {
        String cache = CacheUtils.getCache(HttpAddress.ADDRESSHTTP +"/advert/advertShow.do",
                getActivity());
        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
            topLists = viewSetText(cache);
            System.out.println(topLists.size() + "cccc");
            initDots();
            initViewPager();
        }
    }

    /**
     * 缓存功能
     */
    private void ClickAndSaveCache() {
        String cache = CacheUtils.getCache(HttpAddress.ADDRESSHTTP+"/advert/advertList.do",
                getActivity());
        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
            newsLists = viewSetText(cache);
            System.out.println(newsLists.size() + "VVVV");
            listView.setAdapter(lv);
            Utilty.setListViewHeightBasedOnChildren(listView);
        }
    }

    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.zhanweitu)
                .showImageForEmptyUri(R.mipmap.zhanweitu)
                .showImageOnFail(R.mipmap.zhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 设置ViewPager当前的底部小点
     * */
    private void setCurrentDot(int currentPosition) {
        for (int i = 0; i < mDots.length; i++) {
            if (i == currentPosition) {
                mDots[i].setImageResource(R.mipmap.dot_selected);
            } else {
                mDots[i].setImageResource(R.mipmap.dot_normal);
            }
        }
    }

    /**
     * @author SheXiaoHeng
     * @param screenWidth
     *            手机屏幕的宽度
     * @param picWidth
     *            原始图片所用分辨率的宽度
     * @param retainValue
     *            保留小数位
     * @return 手机屏幕分辨率与原始图片分辨率的宽度比
     *
     * */
    public double divideWidth(int screenWidth, int picWidth, int retainValue) {
        BigDecimal screenBD = new BigDecimal(Double.toString(screenWidth));
        BigDecimal picBD = new BigDecimal(Double.toString(picWidth));
        return screenBD.divide(picBD, retainValue, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }



    /**
     * 获得元数据 并初始化mDatas
     * @param
     */


    /**
     * 适配器
     */
    class ListViewAdpater extends BaseAdapter {
        @Override
        public int getCount() {
            return newsLists.size();
        }

        @Override
        public Object getItem(int position) {
            return newsLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_item, null);
            }
            Picture map = newsLists.get(position);
            tv_title = (TextView) convertView.findViewById(R.id.text_title);
            tv_detail = (TextView) convertView.findViewById(R.id.text_detail);
            iv_pic = (ImageView) convertView.findViewById(R.id.image_icons);
            tv_title.setText(map.getTitle());
            tv_detail.setText(map.getContent());

            tv_title.setVisibility(View.GONE);
            tv_detail.setVisibility(View.GONE);
            iv_pic.setVisibility(View.GONE);
            iv_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            // 将图片显示任务增加到执行池,图片将被显示到ImageView当轮到此ImageView
            ImageView listback = (ImageView) convertView.findViewById(R.id.listback);
            listback.setVisibility(View.VISIBLE);
            imageLoader.displayImage(map.getHeader(), listback, options);
            return convertView;
        }
    }

    /**
     * 适配器
     */
    class VideoListViewAdpater extends BaseAdapter {
        @Override
        public int getCount() {
            return videoLists.size();
        }

        @Override
        public Object getItem(int position) {
            return videoLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.videolistview_item, null);
            }
            Picture map = videoLists.get(position);
            videoback = (ImageView) convertView.findViewById(R.id.videobackground);

            // 将图片显示任务增加到执行池,图片将被显示到ImageView当轮到此ImageView
            imageLoader.displayImage(map.getHeader(), videoback, options);
            return convertView;
        }
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position % topImageList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class NOHandler extends Handler {
        private Context context;

        public NOHandler() {
        }
        public NOHandler(Context context) {
            this.context = context;
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("xx", "xx......");
            try{
                if(msg.what ==1){
                    // 此处可以更新UI
                    s_message.setBackgroundResource(R.mipmap.s_messagedian);

                    SharedPreferences preferences = context.getSharedPreferences(
                            "push_pref", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pushstr", "no");
                    editor.commit();
                }
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    s_message.setBackgroundResource(R.mipmap.s_nomessage);

                    SharedPreferences preferences = context.getSharedPreferences(
                            "push_pref", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pushstr", "yes");
                    editor.commit();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

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
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    listView.setAdapter(lv);
                    Utilty.setListViewHeightBasedOnChildren(listView);
                }
                if(msg.what ==3){
                    mprogressDialog.dismiss();
                    videoListView.setAdapter(viewLv);
                    Utilty.setListViewHeightBasedOnChildren(videoListView);
                }
                if(msg.what ==1){
                    // 此处可以更新UI
                    topLists = viewSetText(topInfo);
                    for(int i = 0;i<topLists.size();i++){
                        topImageList.add(topLists.get(i).getPics().get(0));
                    }
                    initView();
                }

                if(msg.arg2 == 2){
                    buss_text_title.setText(RecommenLists.get(0).getTitle());
                    buss_text_detail.setText(RecommenLists.get(0).getContent());
                    buss_image_icon.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageLoader.displayImage(RecommenLists.get(0).getHeadimage(), buss_image_icon, options);
                    LinearLayout recommend = (LinearLayout) v.findViewById(R.id.buss_recommend);
                    recommend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ClickUtils.isFastDoubleClick()){
                                return;
                            }else {
                                IntentRecommendString(RecommenLists);
                            }

                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 跳转传值
     * @param
     */
    private void IntentRecommendString(List<BussRecommend> bussList) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("title", bussList.get(0).getTitle());
        bundle.putString("content", bussList.get(0).getContent());
        bundle.putString("httpaddress", bussList.get(0).getHttpaddress());
        bundle.putString("tel", bussList.get(0).getTel());
        bundle.putString("address", bussList.get(0).getAddress());
        bundle.putString("id", bussList.get(0).getId());
        bundle.putString("birfe", bussList.get(0).getBirfe());
        bundle.putStringArrayList("bussList", (ArrayList<String>) bussList.get(0).getPics());
        bundle.putString("headimage", bussList.get(0).getHeadimage());
        bundle.putString("info",bussList.get(0).getInfo());
        bundle.putString("linkman", bussList.get(0).getLinkman());
        bundle.putString("feature", bussList.get(0).getFeature());
        intent.putExtras(bundle);
        intent.setClass(getActivity(), BussRecommendActivity.class);
        startActivity(intent);
    }
    
    /**
     * 请求服务器
     */
    private void apiTest() {

        Parameters para = new Parameters();

        para.put("", "");
        ApiStoreSDK.execute(HttpAddress.ADDRESSHTTP+"/advert/advertList.do",
                ApiStoreSDK.GET,
                para,
                new ApiCallBack() {

                    @Override
                    public void onSuccess(int status, String responseString) {
                        Log.i("sdkdemo", "onSuccess");
                        newsLists = viewSetText(responseString);
                        try {
                            CacheUtils.setCache(HttpAddress.ADDRESSHTTP+"/advert/advertList.do",
                                    responseString, getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.arg1 = 1;
                        myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
                        Log.i("memda", responseString);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("sdkdemo", "onComplete");
                    }

                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        try{
                            Toast.makeText(getActivity(), "数据可能开小差了!稍后再试！", Toast.LENGTH_SHORT).show();
                            Log.i("sdkdemo", "onError, status: " + status);
                            Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                            Log.i("sdkdemo", getStackTrace(e));
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });

    }


    /**
     * 请求服务器
     */
    private void apiTestBuss() {

        Parameters para = new Parameters();

        para.put("", "");
        ApiStoreSDK.execute(HttpAddress.ADDRESSHTTP+"/recommend/getRecommend.do",
                ApiStoreSDK.GET,
                para,
                new ApiCallBack() {

                    @Override
                    public void onSuccess(int status, String responseString) {
                        Log.i("sdkdemo", "onSuccess");
                        newsLists = viewSetText(responseString);
                        try {
                            CacheUtils.setCache(HttpAddress.ADDRESSHTTP+"/advert/advertList.do",
                                    responseString, getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.arg1 = 1;
                        myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
                        Log.i("memda", responseString);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("sdkdemo", "onComplete");
                    }

                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        try{
                            Toast.makeText(getActivity(), "数据可能开小差了!稍后再试！", Toast.LENGTH_SHORT).show();
                            Log.i("sdkdemo", "onError, status: " + status);
                            Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                            Log.i("sdkdemo", getStackTrace(e));
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });

    }

    String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(e.getMessage()).append("\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            str.append(e.getStackTrace()[i]).append("\n");
        }
        return str.toString();
    }


    /**
     * 分享
     */
    public void showShare() {
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


    /**
     * json解析
     */
    //请求解析JSON
    public List<BussRecommend> getBussJsonText(String respon){
        RecommenJsonsLists = new ArrayList<BussRecommend>();
        BussRecommend bussRecommend;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("recommend");
            for (int i =0;i<jsonArray1.length();i++){
                bussRecommend = new BussRecommend();
                bussRecommend.setAddress(jsonArray1.getJSONObject(i).getString("address"));
                bussRecommend.setBirfe(jsonArray1.getJSONObject(i).getString("brief"));
                bussRecommend.setContent(jsonArray1.getJSONObject(i).getString("content"));
                bussRecommend.setFeature(jsonArray1.getJSONObject(i).getString("feature"));
                bussRecommend.setHttpaddress(jsonArray1.getJSONObject(i).getString("httpaddress"));
                bussRecommend.setHeadimage(HttpAddress.ADDRESSHTTP + jsonArray1.getJSONObject(i).getString("headimage"));
                bussRecommend.setId(jsonArray1.getJSONObject(i).getString("id"));
                bussRecommend.setInfo(jsonArray1.getJSONObject(i).getString("info"));
                bussRecommend.setLinkman(jsonArray1.getJSONObject(i).getString("linkman"));

                JSONArray jsonArrayImages =  jsonArray1.getJSONObject(i).getJSONArray("pics");
                List<String> iamgeList=new ArrayList<String>();
                for (int j = 0;j < jsonArrayImages.length();j++){
                    String  pic  =jsonArrayImages.getString(j);
                    iamgeList.add(HttpAddress.ADDRESSHTTP+ pic);
                }

                bussRecommend.setPics(iamgeList);
                bussRecommend.setTel(jsonArray1.getJSONObject(i).getString("tel"));
                bussRecommend.setTitle(jsonArray1.getJSONObject(i).getString("title"));
                RecommenJsonsLists.add(bussRecommend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  RecommenJsonsLists;
    }
    /**
     * 请求信息
     * @return
     */
    private  String httpBussResult(){
        String httpUrl = HttpAddress.ADDRESSHTTP + "/recommend/getRecommend.do";
        String end = HttpUtils.getHttp(httpUrl);
//        Log.i("endsends",end);
        return  end;
    }


}
