package com.shows.allactivty;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.com.shows.utils.CircleImageView;
import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;

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

public class RankingActivity extends BaseActivity {

    //异步加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    List<Map<String, String>> rankJsonsLists ;
    List<Map<String, String>> rankLists ;
    private JSONObject json;
    ListView listview;
    ListViewAdpater lv;

    TextView tv_name,tv_money;
    ImageView iv_jiangebi;
    CircleImageView iv_pic;
    ImageView ranking_back;

    String head,name,money;

    private  RankHandler rankHandler;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ranking);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        progressDialog = new ProgressDialog(this);
        rankHandler = new RankHandler();
        Asynheadphoto();
        listview = (ListView) findViewById(R.id.randking_listview);
        lv = new ListViewAdpater();

        progressDialog.setMessage("正在加载.....");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                rankLists = viewSetText();
                Message msg = new Message();
                msg.arg1 = 1;
                rankHandler.sendMessage(msg);
            }
        }).start();
        ranking_back= (ImageView) findViewById(R.id.ranking_back);
        ranking_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    //异步加载图片
    private void Asynheadphoto() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.wawatwo)
                .showImageForEmptyUri(R.mipmap.wawatwo)
                .showImageOnFail(R.mipmap.wawatwo)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 给文本框设置
     * @param
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = initRanking();
        Log.i("repose",respon+"repose");
        rankJsonsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("rank");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                head = jsonArray1.getJSONObject(i).getString("head");
                name = jsonArray1.getJSONObject(i).getString("name");
                money = jsonArray1.getJSONObject(i).getString("money");
                params.put("head",head);
                params.put("name", name);
                params.put("money",money);
                rankJsonsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  rankJsonsLists;
    }


    /**
     * 请求网络数据
     * @return
     */
    private String initRanking() {
//        String requestUrl= "http://192.168.0.80:8888/shoumeixiu/custom/rank.do";
        String requestUrl=  HttpAddress.ADDRESSHTTP + "/custom/rank.do";
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
            String content = "number=" + URLEncoder.encode("15", "UTF-8");
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
                resultData += inputLine + "";
                Log.d("inputLine", inputLine);
            }

            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("memememem", resultData);
            } else {
                Log.i("memememem", "读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  resultData;
    }


    class RankHandler extends Handler {
        public RankHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("RankHandler", "RankHandler......");
            try{
                progressDialog.dismiss();
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    listview.setAdapter(lv);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 适配器
     */
    class ListViewAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return rankLists.size();
        }

        @Override
        public Object getItem(int position) {
            return rankLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(RankingActivity.this).inflate(R.layout.ranking_listview_item, null);
            Map<String, String> map = rankLists.get(position);
            tv_name = (TextView) convertView.findViewById(R.id.text_name);
            tv_money = (TextView) convertView.findViewById(R.id.text_money);
            iv_pic = (CircleImageView) convertView.findViewById(R.id.image_head);
            iv_jiangebi = (ImageView) convertView.findViewById(R.id.image_jiangbei);

            tv_name.setText(map.get("name"));
            tv_money.setText(map.get("money")+"元");
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            if(map.get("head")!= null){
                imageLoader.displayImage(map.get("head"), iv_pic, options);
            }
            int images [] = new int[]{R.mipmap.rankone,R.mipmap.ranktwo,R.mipmap.rankthree,R.mipmap.rankfour,R.mipmap.rankfive,R.mipmap.ranksix,
            R.mipmap.rankseven,R.mipmap.rankeight,R.mipmap.ranknine,R.mipmap.rankten,
                    R.mipmap.rankeleven,R.mipmap.rankeleven,R.mipmap.rankthirteen,R.mipmap.rankfourteen,R.mipmap.rankfifteen};

            for(int i = 0;i< map.size();i++){
                iv_jiangebi.setImageResource(images[position]);
            }

            return convertView;
        }
    }
}



