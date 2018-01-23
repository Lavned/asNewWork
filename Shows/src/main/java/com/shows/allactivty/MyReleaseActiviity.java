package com.shows.allactivty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.shows.typeinfoallcativity.CommendsListActivity;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.bean.InfoDeatail;
import com.shows.view.BaseActivity;
import com.shows.view.GalleryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyReleaseActiviity extends BaseActivity {

    String req;
    String userPhone;
    TextView tv_title,tv_detail,tv_infotime,tv_del,info_like,info_commend;
    GridView gridview;
    ListView listview;
    ImageView back;

    private ProgressDialog progressDialog;

    JSONObject json;
    List<InfoDeatail> myReslesea ;
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    int width;
    ListViewAdpater lvs;


    //更新UI
    MyHandler myHandler;
    private SharedPreferences mPreferences;
    private String cid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_release_activiity);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        cid = mPreferences.getString("uid", "");
        Log.i("cid", cid + "kkkk");

        WindowManager wm = (WindowManager) MyReleaseActiviity.this
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();

        progressDialog =new ProgressDialog(this);
        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        Log.i("userpnheo", userPhone);

        req = HttpAddress.ADDRESSHTTP+"/talk/getByTel.do";
        listview = (ListView) findViewById(R.id.myrelease_lv);
        //图片异步加载
        ImageLoaderList();
        //不在主线程更新UI
        myHandler = new MyHandler();
        lvs = new ListViewAdpater();

        progressDialog.setMessage("正在请求......");
        progressDialog.show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                myReslesea = getMyReleaseJsonInfo();
//                Message msg = new Message();
//                msg.what =1;
//                myHandler.sendMessage(msg);
//            }
//        }).start();
        asyncHttpInfo();

        back = (ImageView) findViewById(R.id.my_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    protected void onResume() {
        myReslesea = getMyReleaseJsonInfo();
        lvs.notifyDataSetChanged();
        super.onResume();
    }

    /**
     * 删除
     * @param delId
     * @return
     */
    private String deleteTalk(String delId){
        String requestUrls = HttpAddress.ADDRESSHTTP +  "/talk/delTalk.do";
        String ends = HttpUtils.postHttp2(requestUrls,"talkId",delId);
        Log.i("endsend", ends + "jieguo");
        if(ends.contains("true")){
            Message  msg = new Message();
            msg.what = 2;
            myHandler.sendMessage(msg);
        }
        return  ends;
    }


    /**
     * 图片加载设置
     */
    private void ImageLoaderList() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(MyReleaseActiviity.this));

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
     * 更新UI
     */
    class MyHandler extends android.os.Handler {
        public MyHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{

                if(msg.what ==1){
                    // 此处可以更新UI
                    progressDialog.dismiss();
                    try{
                        if(myReslesea !=null){
                            listview.setAdapter(lvs);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(msg.what == 2){
                    // 此处可以更新UI
                    progressDialog.dismiss();
//                    Toast.makeText(MyReleaseActiviity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                    myReslesea.clear();
                    myReslesea = getMyReleaseJsonInfo();
                    lvs.notifyDataSetChanged();
                }
                if(msg.what == 5){
                    // 此处可以更新UI
//                    progressDialog.dismiss();
                    Toast.makeText(MyReleaseActiviity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                    listview.setAdapter(new ListViewAdpater());
                }
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    progressDialog.dismiss();
                    TextView tv = (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("亲还没有发布信息哦！");
                }
                if(msg.arg2 ==1){
                    // 此处可以更新UI
                    progressDialog.dismiss();
                    TextView tv = (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    Toast.makeText(MyReleaseActiviity.this, "服务异常，请稍后再试！", Toast.LENGTH_SHORT).show();
                }

                if(msg.arg1 == 5){
                    myReslesea = getMyReleaseJsonInfo();
                    lvs.notifyDataSetChanged();
                    Toast.makeText(MyReleaseActiviity.this,"点赞成功",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == 6){
                    myReslesea = getMyReleaseJsonInfo();
                    lvs.notifyDataSetChanged();
                    Toast.makeText(MyReleaseActiviity.this,"取消点赞",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步请求网络数据
     */
    private void asyncHttpInfo() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myReslesea = getMyReleaseJsonInfo();
                    Message msg = new Message();
                    msg.what =1;
                    myHandler.sendMessage(msg);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<InfoDeatail> getMyReleaseJsonInfo(){
        String  respon = HttpUtils.postHttp(req, userPhone);
        List<InfoDeatail> infoUrlLists=new ArrayList<InfoDeatail>();
        InfoDeatail infoD;
        try {
            Message message = new Message();
            if(respon == null){
                message.arg2 =1 ;
            }else if(respon.contains("null")){
                message.arg1 = 1;
                myHandler.sendMessage(message);
            }
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("talk");
            for (int i =0;i<jsonArray1.length();i++){
                infoD = new InfoDeatail();
                infoD.setContent(jsonArray1.getJSONObject(i).getString("content"));
                infoD.setWriter(jsonArray1.getJSONObject(i).getString("name"));
                infoD.setHeade(jsonArray1.getJSONObject(i).getString("head"));
                infoD.setTime(jsonArray1.getJSONObject(i).getString("stime"));
                infoD.setId(jsonArray1.getJSONObject(i).getString("id"));
                infoD.setLikedNum(jsonArray1.getJSONObject(i).getString("likedNum"));
                infoD.setCommentNum(jsonArray1.getJSONObject(i).getString("commentNum"));

                List<String> picass =new ArrayList<String>();
                if(jsonArray1.getJSONObject(i).getJSONArray("pics") != null){
                    JSONArray jsonArraytemp =  jsonArray1.getJSONObject(i).getJSONArray("pics");//
                    if(jsonArraytemp.length()>0){
                        for (int j = 0;j < jsonArraytemp.length();j++){
                            picass.add(jsonArraytemp.getString(j));
                        }
                    }
                }
                infoD.setPics(picass);
                infoUrlLists.add(infoD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  infoUrlLists;
    }

    //初始化图片容器
    private void initGridView(final int index) {
        gridview.setAdapter(new ImageAdapter(index));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyReleaseActiviity.this, GalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("positionUrl", myReslesea.get(index).getPics().get(position));
                bundle.putInt("position", myReslesea.get(index).getPics().size());
                bundle.putStringArrayList("listPi", (ArrayList<String>) myReslesea.get(index).getPics());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //baseAdpater适配器
    public class ImageAdapter extends BaseAdapter {
        int index;
        public ImageAdapter(int index){
            this.index= index;
        }
        @Override
        public int getCount() {
            return myReslesea.get(index).getPics().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = LayoutInflater.from(MyReleaseActiviity.this).inflate(R.layout.re_grideview_item, null);
            }
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.res_layout);
            imageView = (ImageView) convertView.findViewById(R.id.re_itemImage);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                   width/4, width/5);
            lp1.setMargins(5,5,5,5);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(lp1);

            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            if(myReslesea.get(index).getPics().get(position) != null){
                imageLoader.displayImage(myReslesea.get(index).getPics().get(position), imageView, options);
            }
            if (myReslesea.get(index).getPics().get(0).equals("")){
                LinearLayout.LayoutParams lp1s = new LinearLayout.LayoutParams(0,0);
                lp1s.width=0;
                lp1s.height =0;
                imageView.setLayoutParams(lp1s);
                layout.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


    /**
     * 适配器
     */
    class ListViewAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return myReslesea.size();
        }

        @Override
        public Object getItem(int position) {
            return myReslesea.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(MyReleaseActiviity.this).inflate(R.layout.reaealse_listview_item, null);
            }
            final InfoDeatail map = myReslesea.get(position);
            tv_title = (TextView) convertView.findViewById(R.id.re_name);
            tv_del = (TextView) convertView.findViewById(R.id.info_delete);
            tv_detail = (TextView) convertView.findViewById(R.id.re_detail);
            tv_infotime = (TextView) convertView.findViewById(R.id.info_stime);
            gridview = (GridView) convertView.findViewById(R.id.images_info);
            ImageView image_icon = (ImageView) convertView.findViewById(R.id.image_icon);

            tv_del.setVisibility(View.VISIBLE);
            final String  index= map.getId();
            tv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            deleteTalk(index);
                            myReslesea = getMyReleaseJsonInfo();
                            Message mmm = new Message();
                            mmm.what = 5;
                            myHandler.sendMessage(mmm);
                        }
                    }).start();

                }
            });
            tv_title.setText(map.getWriter());
            tv_detail.setText(map.getContent());
//            tv_infotime.setText(map.getTime());

            String subTime = map.getTime().substring(0,10);

            tv_infotime.setText(subTime);

            imageLoader.displayImage(map.getHeade(), image_icon, options);

            info_like = (TextView) convertView.findViewById(R.id.info_like);
            info_commend = (TextView) convertView.findViewById(R.id.info_commend);

            info_commend.setText(map.getCommentNum());
            info_like.setText(map.getLikedNum());


            info_commend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyReleaseActiviity.this, CommendsListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("talkId", map.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            info_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String likes = clickLike(map.getId());
                            Message ms = new Message();
                            if (likes.contains("true")) {
                                ms.arg1 = 5;
                            }
                            if (likes.contains("2")) {
                                ms.arg1 = 6;
                            }
                            myHandler.sendMessage(ms);
                        }
                    }).start();

                }
            });


            try {
                initGridView(position);
            }catch (Exception e){
                e.printStackTrace();
            }

            return convertView;
        }
    }

    /**
     * 点赞与取消赞
     */
    private String clickLike(String talkId) {
        String requestUrl = HttpAddress.ADDRESSHTTP +"/comment/talkLike.do";
        //POST请求
        String resultData="";
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            // 设置以POST方式
            urlConn.setRequestMethod("POST");
            // Post 请求不能使用缓存
            urlConn.setUseCaches(false);
            urlConn.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            urlConn.setRequestProperty("Content-type", "text/html");
            urlConn.setRequestProperty("contentType", "utf-8");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            urlConn.connect();
            //DataOutputStream流
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            //要上传的参数
            String content = "talkId=" + URLEncoder.encode(talkId, "UTF-8");
            content += "&cid=" + URLEncoder.encode(cid, "UTF-8");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("gongyongda", resultData);
            } else {
                Log.i("gongyongda", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }

}
