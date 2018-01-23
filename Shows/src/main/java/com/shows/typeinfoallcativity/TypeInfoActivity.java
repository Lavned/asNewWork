package com.shows.typeinfoallcativity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//九宫格点击进去的类
public class TypeInfoActivity extends BaseActivity {

    ListView ls_re;
    //gridview相关变量
    private String texts[] = null;
    private int images[] = null;
    TextView tv_title,tv_detail,tv_infotime,info_like,info_commend;
    GridView gridview;

    String type;
    String typeid,telephone;
    String inputLine = "";

    private JSONObject json;
    List<InfoDeatail> infoUrl ;


    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    //更新UI
    MyHandler myHandler;
    private ProgressDialog progressDialog;
    private  int width;

    private TextView weiqu;
    private String cid;
    private SharedPreferences mPreferences;
    private ListViewAdpater lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmento);


        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        progressDialog = new ProgressDialog(this);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        cid = mPreferences.getString("uid", "");
        Log.i("cid", cid+"kkkk");

        WindowManager wm = (WindowManager) TypeInfoActivity.this
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();

        //获取类型和ID
        Bundle bundle = getIntent().getExtras();
        type =bundle.getString("title");
        typeid = bundle.getString("typeid");
//        Log.i("talk",typeid+"oo");
        TextView tt = (TextView) findViewById(R.id.type_title);
        tt.setText(type);

        progressDialog.setMessage("正在加载......");
        progressDialog.show();
        asyncHttpInfo();

        lv= new ListViewAdpater();

    }

    @Override
    protected void onResume() {
        infoUrl = getInfoList();
        lv.notifyDataSetChanged();
        super.onResume();
    }

    /**
     * 异步请求网络数据
     */
    private void asyncHttpInfo() {
        try {
            //图片异步加载
            ImageLoaderList();
            ls_re = (ListView)findViewById(R.id.release_lv);
            //不在主线程更新UI
            myHandler = new MyHandler();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    infoUrl = getInfoList();
                    Message msg = new Message();
                    msg.what =1;
                    myHandler.sendMessage(msg);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                progressDialog.dismiss();
                weiqu = (TextView) findViewById(R.id.wawa_weiqu);
                if(msg.what ==1){
                    // 此处可以更新UI
                    Log.i("infourl", infoUrl.size() + "ggegs");
                    try{

                        if(infoUrl != null){
                            ls_re.setAdapter(lv);
                        }

                    }catch (Exception e){
                       Log.i("stringeseee",e.toString());
                        e.printStackTrace();
                    }
                }
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    try{
                        weiqu.setVisibility(View.VISIBLE);
                        weiqu.setText("还没有人发布该类别的信息呢");
//                         Toast.makeText(TypeInfoActivity.this,"还没有数据呢",Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        Log.i("stringeseee",e.toString());
                        e.printStackTrace();
                    }
                }
                if(msg.arg1 == 2){
                    // 此处可以更新UI
                    try{
                        weiqu.setVisibility(View.VISIBLE);
                        Toast.makeText(TypeInfoActivity.this,"服务器异常，请稍后再试！",Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(msg.arg1 == 5){
                    infoUrl = getInfoList();
                    lv.notifyDataSetChanged();
                    Toast.makeText(TypeInfoActivity.this,"点赞成功",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == 6){
                    infoUrl = getInfoList();
                    lv.notifyDataSetChanged();
                    Toast.makeText(TypeInfoActivity.this,"取消点赞成功",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 图片加载设置
     */
    private void ImageLoaderList() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(TypeInfoActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.fzhanweitu)
                .showImageForEmptyUri(R.mipmap.fzhanweitu)
                .showImageOnFail(R.mipmap.fzhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    private String initListViewDate() {
        String httpUrl = HttpAddress.ADDRESSHTTP +"/talk/getByType.do";
        String resulutcode = HttpUtils.postHttp2(httpUrl,"type",typeid);
//        Log.i("input",resulutcode+"resulutcode");
        return  resulutcode;

    }

    public List<InfoDeatail> getInfoList(){
        String respon = initListViewDate();
        List<InfoDeatail> infoUrlLists=new ArrayList<InfoDeatail>();
        Message msg  = new Message();
        if(respon.contains("null")){
            msg.arg1 = 1;

        }else if(respon.equals("")){
            msg.arg1 =2;
        }
        myHandler.sendMessage(msg);
        InfoDeatail infoD;
        try {
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
//                Log.i("shuliang", infoUrl.get(index).getPics().get(position) + ".." + infoUrl.get(index).getPics().size());
                Intent intent = new Intent(TypeInfoActivity.this, GalleryActivity.class);
                Bundle  bundle = new Bundle();
                bundle.putString("positionUrl", infoUrl.get(index).getPics().get(position));
                bundle.putInt("position", infoUrl.get(index).getPics().size());
                bundle.putStringArrayList("listPi", (ArrayList<String>) infoUrl.get(index).getPics());
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
            return infoUrl.get(index).getPics().size();
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
            ImageView imageView ;
            if (convertView == null) {
                convertView = LayoutInflater.from(TypeInfoActivity.this).inflate(R.layout.re_grideview_item, null);
            }
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.res_layout);
                imageView = (ImageView) convertView.findViewById(R.id.re_itemImage);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(  width/4, width/5);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(lp1);
                // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
                if(infoUrl.get(index).getPics().get(position) != null){
                    imageLoader.displayImage(infoUrl.get(index).getPics().get(position), imageView, options);
                }
                if (infoUrl.get(index).getPics().get(0).equals("")){
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
     * gridview的适配器
     */
    class ListViewAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return infoUrl.size();
        }

        @Override
        public Object getItem(int position) {
            return infoUrl.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(TypeInfoActivity.this).inflate(R.layout.reaealse_listview_item, null);
            }
//            }else{
                final InfoDeatail map = infoUrl.get(position);
                tv_title = (TextView) convertView.findViewById(R.id.re_name);
                tv_detail = (TextView) convertView.findViewById(R.id.re_detail);
                tv_infotime = (TextView) convertView.findViewById(R.id.info_stime);
                gridview = (GridView) convertView.findViewById(R.id.images_info);


                info_like = (TextView) convertView.findViewById(R.id.info_like);
                info_commend = (TextView) convertView.findViewById(R.id.info_commend);

                info_commend.setText(map.getCommentNum());
                info_like.setText(map.getLikedNum());

                ImageView image_icon = (ImageView) convertView.findViewById(R.id.image_icon);
                tv_title.setText(map.getWriter());
                tv_detail.setText(map.getContent());
                String subTime = map.getTime().substring(0,10);

                tv_infotime.setText(subTime);
                imageLoader.displayImage(map.getHeade(), image_icon, options);
//                 Log.i("size", map.getPics().size() + "kkk" + "tupian" + map.getPics());

                info_commend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TypeInfoActivity.this,CommendsListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("talkId",map.getId());
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
                               String likes =  clickLike(map.getId());
                                Message ms = new Message();
                                if(likes.contains("true")){
                                    ms.arg1 = 5;
                                }
                                if(likes.contains("2")){
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
