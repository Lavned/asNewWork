package com.show.fragment;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.CacheUtils;
import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.ViewPagerAdapter;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.allactivty.ExchangeGoodActivity;
import com.shows.allactivty.LimitedActivity;
import com.shows.allactivty.ShoperInfoActivity;
import com.shows.imageload.AbsListViewBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class FragmentH extends Fragment implements View.OnClickListener {

    private View v;
    TextView tv_detail, ex_price;
    ImageView im_icon, flashImage;
    private RelativeLayout limited_click;

//    private int width;
//    private int newWidth;
//    private int padding;
//
//    //图片轮播相关
//    private ViewPager mViewpager;
//    private ViewPagerAdapter mViewPagerAdp;
//    private ImageView[][] mImageViews;
//    private ImageView[] mDots;
//    private LinearLayout layoutDots;
//    private final int AUTO = 0;
//    private final long delay = 3 * 1000;

    List<Map<String, String>> hhUrlsLists;
    List<Map<String, String>> hhUrls;

    List<Map<String, String>> leftUrlsLists;
    List<Map<String, String>> leftUrls;//左侧菜单

    HHHandler gghandler;
    JSONObject json;

    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
//    ArrayList<String> lunbo = new ArrayList<>();

    private GridView rightGridView;
    private ListView leftListView;
    private ListView shop_list;

    TextView xiuxiushangcheng, xiuxiushangjia;
    LinearLayout shangjialin, shangchenglin;
    LinearLayout listlin;
    int height, width;

    private ProgressDialog progressDialog;
    LeftAdapter leftAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment02, null, false);

        refreshDate();

        return v;
    }

    /**
     * 刷新数据
     */
    private void refreshDate() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        progressDialog = new ProgressDialog(getActivity());
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        ImageLoadInfo();

        flashImage= (ImageView) v.findViewById(R.id.flashImage);
        // 渐变动画
//        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.qianggou);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);// 动画时间
        animation.setFillAfter(true);// 保持动画状态
        animation.setRepeatCount(Integer.MAX_VALUE);
        animation.setRepeatMode(Animation.REVERSE);
        flashImage.setAnimation(animation);
        flashImage.startAnimation(animation);

        initView();
        leftAdapter = new LeftAdapter(getActivity());

        rightGridView = (GridView) v.findViewById(R.id.category_right);
        leftListView = (ListView) v.findViewById(R.id.category_left);

//        leftListView.setAdapter(leftAdapter);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                leftAdapter.setSelectItem(position);
                leftAdapter.notifyDataSetInvalidated();
                progressDialog.setMessage("加载中...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        int popo = position + 1;
                        String respon = initGoods(popo + "");
                        if (respon.contains("null")) {
                            msg.arg2 = 1;
                        }else{
                            hhUrls = viewSetText(respon);
                            msg.arg2 = 2;
                        }
                        gghandler.sendMessage(msg);
                    }
                }).start();
            }
        });


        gghandler = new HHHandler();
        progressDialog.setMessage("加载中......");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String leftMenu = initLeftMenu();
                leftUrls = leftMenuSetView(leftMenu);
                if(leftUrls.size() < 1){
                    msg.arg1 = 2;
                }
                String respon = initGoods("1");
                hhUrls = viewSetText(respon);

                msg.arg1 = 1;
                gghandler.sendMessage(msg);
            }
        }).start();


//        shop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(getActivity(), ShoperInfoActivity.class));
//            }
//        });

        limited_click = (RelativeLayout) v.findViewById(R.id.limited_click);
        limited_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LimitedActivity.class));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            //界面显示
            refreshDate();
        }
        else
        {
            //yingc

        }
    }


    /**
     * zuocecaidan
     * @return
     */
    private String initLeftMenu() {
        String requestUrl = HttpAddress.ADDRESSHTTP + "/good/getGoodType.do";
        String ends = HttpUtils.getHttp(requestUrl);
        if(ends == null || ends.equals("")){
            Message mess = new Message();
            mess.what = 10;
            gghandler.sendMessage(mess);
        }
        Log.i("enedddddd",ends);
        return  ends;
    }


    public final class ViewHolder {
        public TextView titleText;
    }


    /**
     * 左侧菜单解析
     * @return
     */
    public List<Map<String, String>> leftMenuSetView(String respon){
//        respon = initGoods();
        leftUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("jsonStr");
            for(int i =0 ;i < jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("gid", jsonArray1.getJSONObject(i).getString("tid"));
                params.put("gname",jsonArray1.getJSONObject(i).getString("tname"));
                leftUrlsLists.add(params);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  leftUrlsLists;
    }


    /**
     * 左侧菜单的适配器
     */
    class LeftAdapter extends BaseAdapter {
        private Context context;//用于接收传递过来的Context对象

        private LayoutInflater mInflater;

        public LeftAdapter(Context context) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return leftUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.leftview, null);
                holder.titleText = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleText.setText(leftUrls.get(position).get("gname"));

            if (position == selectItem) {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            //convertView.getBackground().setAlpha(80);
            return convertView;
        }

        public void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }

        private int selectItem = -1;
    }

    /**
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewSetText(String respon){
//        respon = initGoods();
        hhUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("Goods");
            for(int i =0 ;i < jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("ID", jsonArray1.getJSONObject(i).getString("id"));
                params.put("head",HttpAddress.ADDRESSHTTP+jsonArray1.getJSONObject(i).getString("head"));
                params.put("name", jsonArray1.getJSONObject(i).getString("name"));
                params.put("price", jsonArray1.getJSONObject(i).getString("price"));
                hhUrlsLists.add(params);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  hhUrlsLists;
    }

    private String initGoods(String id) {
        String requestUrl=HttpAddress.ADDRESSHTTP+"/good/goodList.do?typeId=" +id;
        String resultStr = HttpUtils.getHttp(requestUrl);
        Log.i("resultStr",resultStr);
        return resultStr;

        //
    }

    /**
     * 初始化界面
     */
//    private void initCacheView(String id) {
//        String cache = CacheUtils.getCache(HttpAddress.ADDRESSHTTP+"/good/goodList.do?typeId=" + id,
//                getActivity());
//        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
//            hhUrls = viewSetText(cache);
//            rightGridView.setAdapter(new goodRIghtAdpater());
//        }
//    }

    class HHHandler extends Handler {
        public HHHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("hhHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    rightGridView.setAdapter(new goodRIghtAdpater());
                    leftListView.setAdapter(leftAdapter);
                    leftAdapter.setSelectItem(0);
                }
                if(msg.arg2 == 2){
                    rightGridView.setAdapter(new goodRIghtAdpater());
                }
                if(msg.arg2 == 1){
                    Toast.makeText(getActivity(),"还没有该类别的商品",Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 10){
                    Toast.makeText(getActivity(),"服务异常，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    
    /**
     * 初始化上边菜单
     */
    private void initView() {
        shop_list = (ListView) v.findViewById(R.id.shangjia_listview);
        xiuxiushangcheng = (TextView) v.findViewById(R.id.xiuxiushangcheng);
        shangjialin = (LinearLayout) v.findViewById(R.id.shangjialin);
        shangchenglin = (LinearLayout) v.findViewById(R.id.shangchenglin);
        xiuxiushangjia = (TextView) v.findViewById(R.id.xiuxuishangjia);
        xiuxiushangcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiuxiushangcheng.setBackgroundColor(Color.parseColor("#30B0EE"));
                xiuxiushangcheng.setTextColor(Color.parseColor("#ffffff"));
                xiuxiushangjia.setBackgroundColor(Color.parseColor("#ffffff"));
                xiuxiushangjia.setTextColor(Color.parseColor("#30B0EE"));
                shangjialin.setVisibility(View.GONE);
                shangchenglin.setVisibility(View.VISIBLE);
            }
        });
        xiuxiushangjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xiuxiushangjia.setBackgroundColor(Color.parseColor("#30B0EE"));
                xiuxiushangjia.setTextColor(Color.parseColor("#ffffff"));
                xiuxiushangcheng.setBackgroundColor(Color.parseColor("#ffffff"));
                xiuxiushangcheng.setTextColor(Color.parseColor("#30B0EE"));
                shangjialin.setVisibility(View.VISIBLE);
                shangchenglin.setVisibility(View.GONE);
                shop_list.setAdapter(new ShopListViewAdpater());

            }
        });
    }

    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.fzhanweitu)
                .showImageForEmptyUri(R.mipmap.fzhanweitu)
                .showImageOnFail(R.mipmap.fzhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    /*商家模块*/
    class goodRIghtAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return hhUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return hhUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.exchange_listview_item, null);
                listlin = (LinearLayout) convertView.findViewById(R.id.listlin);
//                LinearLayout.LayoutParams paras = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//获取按钮的布局
//                paras.width=5;//修改宽度
//                View view = convertView.findViewById(R.id.view);
//                view.setLayoutParams(paras);
                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//获取按钮的布局

                int widths = width - 130;
                para.width=widths / 2 ;//修改宽度
                listlin.setPadding(8,8,8,8);
                para.setMargins(5,5,5,5);
                listlin.setLayoutParams(para);
                final Map<String, String> map = hhUrls.get(position);
                tv_detail = (TextView) convertView.findViewById(R.id.ex_content);
                ex_price = (TextView) convertView.findViewById(R.id.ex_price);
                im_icon = (ImageView) convertView.findViewById(R.id.ex_icon);

                tv_detail.setText(map.get("name"));
                ex_price.setText("￥"+map.get("price"));
                im_icon.setScaleType(ImageView.ScaleType.FIT_XY);
                // 将图片显示任务增加到执行池,图片将被显示到ImageView当轮到此ImageView
                imageLoader.displayImage(map.get("head"), im_icon, options);
//                Log.i("xxx", map.get("head") + "");

                rightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (ClickUtils.isFastDoubleClick()) {
                            return;
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), ExchangeGoodActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ID", hhUrls.get(i).get("ID"));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
            return convertView;
        }
    }


    //商家的
    class ShopListViewAdpater extends BaseAdapter {

        /**
         * @param
         */
        public ShopListViewAdpater() {
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.shoper_listview_item, null);
            }

            tv_detail = (TextView) convertView.findViewById(R.id.show_detail);

            tv_detail.setText("哈哈哈哈哈");
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
//            imageLoader.displayImage(map.getHeader(), iv_pic, options);


            return convertView;
        }
    }

}
