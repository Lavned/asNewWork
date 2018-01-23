package com.jy.project.jyshop.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jy.project.jyshop.R;
import com.jy.project.jyshop.base.BaseTopActivity;
import com.jy.project.jyshop.bean.GoodDetail;
import com.jy.project.jyshop.request.GetListRequest;
import com.jy.project.jyshop.utils.T;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseTopActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {


    //右侧
    private PullToRefreshListView gv_right;
    private RightAdapter rightAdapter;

    int pageIndex = 0;
    int pageSize = 10;

    TextView buttomIm;
    List<GoodDetail> listas = new ArrayList<>();

    @ViewInject(R.id.end)
    RelativeLayout end;

    @ViewInject(R.id.xxx)
    TextView helloworld;

    @Event(value = R.id.xxx, type = View.OnClickListener.class)
    private void testInjectOnClick(View v){
        T.showShort("login");
        startActivity(new Intent(MainActivity.this,CreateCodeActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);//注解绑定
        helloworld.setText("hha");
        gv_right = getView(R.id.gv_right);
        gv_right.setMode(PullToRefreshBase.Mode.BOTH);
        gv_right.setOnRefreshListener(this);
        gv_right.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING){
                    startAlphaAnimation(end,2);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    Log.d("ListView", "##### 滚动到顶部 #####");
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = gv_right.getChildAt(gv_right.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == gv_right.getHeight()) {
                        Log.d("ListView", "##### 滚动到底部 ######" +pageIndex );
                        if(pageIndex > 1 && gv_right.getMode() == PullToRefreshBase.Mode.PULL_FROM_START){
                            startAlphaAnimation(end,1);
                        }
                    }
                    else {
                        startAlphaAnimation(end,2);
                    }
                }}
        });
        postDataFromWeb();
    }

    // get请求方式
    public static void getDataFromWeb( String url) {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    // post请求方式
    public void postDataFromWeb() {
        RequestParams params = new RequestParams("http://purchase.jinyoufarm.cn/api/AppService/v1/GetGoodsList");
        GetListRequest req = new GetListRequest();
        req.userid = "c5400b2b-5de3-46e2-8228-acf4341666d1";
        req.pageindex = pageIndex;
        req.pagesize = pageSize;
        req.categoryid = "";
        req.status = "1";
        req.kword = "";
        String json = JSON.toJSONString(req);
        params.setBodyContent(json);
        params.setAsJsonContent(true);
        params.setHeader("Content-Type", "application/json; charset=utf-8");
//        params.addBodyParameter("", json, "application/json");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("tetetettete" , "kksss" + ex.getMessage() +"jj"+ ex.toString());
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                gv_right.onRefreshComplete();
                JSONObject jsonObject = null;
                Gson gson = new Gson();
                try {
                    jsonObject = new JSONObject(result.toString());
                    List<GoodDetail> ss = gson.fromJson(jsonObject.getJSONArray("data").toString(),
                            new TypeToken<List<GoodDetail>>() {
                            }.getType());
                    updateView(ss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void startAlphaAnimation(View view,int type){
        /**
         * @param fromAlpha 开始的透明度，取值是0.0f~1.0f，0.0f表示完全透明， 1.0f表示和原来一样
         * @param toAlpha 结束的透明度，同上
         */
//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
//        //设置动画持续时长
//        alphaAnimation.setDuration(1000);
//        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
//        alphaAnimation.setFillAfter(true);
//        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
//        alphaAnimation.setFillBefore(true);
////        设置动画的重复模式：反转REVERSE和重新开始RESTART
//        alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
////        设置动画播放次数
//        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        //开始动画

        TranslateAnimation down = new TranslateAnimation(0, 0, -100, 0);//位移动画，从button的上方300像素位置开始
        down.setFillAfter(true);
        down.setInterpolator(new BounceInterpolator());//弹跳动画,要其它效果的当然也可以设置为其它的值
        down.setDuration(1000);//持续时间

        if(type ==1){
            view.setVisibility(View.VISIBLE);
            view.startAnimation(down);//设置按钮运行该动画效果
        }else if(type == 2){
            view.setVisibility(View.GONE);
            //清除动画
            view.clearAnimation();
            //同样cancel()也能取消掉动画
            down.cancel();
        }

    }



    /**
     * 下拉刷新
     * @param data
     */

    List<GoodDetail> dataList;
    protected void updateView(List<GoodDetail> data) {
        dataList = data;
        if(pageIndex == 1) {
            rightAdapter = new RightAdapter(MainActivity.this  , data);
            gv_right.setAdapter(rightAdapter);
        } else {
            rightAdapter.getmData().addAll(data);
            rightAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        gv_right.setMode(PullToRefreshBase.Mode.BOTH);
        pageIndex = 1;
        postDataFromWeb();
        startAlphaAnimation(end,2);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        T.showShort("xia");
        pageIndex++;
        postDataFromWeb();
        if(dataList != null){
            if(dataList.size()== 0 && pageIndex > 1){
                gv_right.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                T.showShort("暂无最新数据");
                startAlphaAnimation(end,1);
            }
        }

    }


    private void postJson() {
        GetListRequest req = new GetListRequest();
        req.userid = "5400b2b-5de3-46e2-8228-acf4341666d1";
        req.pageindex = pageIndex;
        req.pagesize = pageSize;
        req.categoryid = "";
        req.status = "1";
        req.kword = "";
//        Log.i("ttttttt" ,"参数" + pageIndex + pageSize + "id" +categoryid + "status" + status + "gj" + kword);
        //申明给服务端传递一个json串
        //创建一个OkHttpClient对象
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, req.toJson());
        //创建一个请求对象
        Request request = new Request.Builder()
                .url("http://purchase.jinyoufarm.cn/api/AppService/v1/GetGoodsList")
                .post(requestBody)
                .build();
        //发送请求获取响应
        try {
            Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        gv_right.onRefreshComplete();
                    }
                }).start();

                Log.i("tttttt",response.body().string());


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 右侧商品的适配器
     */
    class RightAdapter extends BaseAdapter {
        private Context context;//用于接收传递过来的Context对象
        private LayoutInflater mInflater;
        public List<GoodDetail> list;

        public RightAdapter(Context context,List<GoodDetail> lists) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = lists;
        }
//
        public List<GoodDetail> getmData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.test, null);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            return convertView;
        }

        public final class ViewHolder {
            public TextView titleText;
        }
    }
}
