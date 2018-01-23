package com.shows.typeinfoallcativity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.CircleImageView;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.com.shows.utils.StringFormatUtil;
import com.shows.view.BaseActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yixinke.shows.R;


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

public class CommendsListActivity extends BaseActivity {

    private ListView commendLv;
    private Button submitComm;
    private EditText editComm;
    private CommendHandler commendHandler;
    private ProgressDialog progressDialog;

    private List<Map<String, String>> commendJsonsLists ;
    private List<Map<String, String>> commendLists ;
    private JSONObject json;
    private TextView comm_name,comm_text;
    private String talkId;
    private SharedPreferences mPreferences;
    private String userPhone;
    private ImageView commend_back;

    private final OkHttpClient client = new OkHttpClient();
    private     CommListViewAdapater lv = new CommListViewAdapater();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_commends_list);
        initView();

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        userPhone = mPreferences.getString("telephone", "");
        Log.i("userpnheo", userPhone);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        talkId = bundle.getString("talkId");
        Log.i("idid",talkId);

        commendHandler = new CommendHandler();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();


        ListViewSet();




    }

    private void ListViewSet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                commendLists =viewSetText();
                Message msg = new Message();
                msg.what = 1 ;
                commendHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     *请求网络数据
     */
    private String httpCommend() {
        String url= HttpAddress.ADDRESSHTTP + "/comment/getCommentByTalkId.do";
        String end = HttpUtils.postHttp2(url,"talkId",talkId);
        return  end;
    }

    /**
     * 异步更新UI
     */
    class CommendHandler extends Handler {
        public CommendHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{

                progressDialog.dismiss();
                if (msg.what == 1 ){
                    lv.notifyDataSetChanged();
                    commendLv.setAdapter(lv);
                }
                if (msg.arg1 == 1 ){
                    TextView tv = (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("还没有评论呢！");
                }
                if (msg.arg2 == 1 ){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            commendLists =viewSetText();
                            Message msg = new Message();
                            msg.arg1 = 5 ;
                            commendHandler.sendMessage(msg);
                        }
                    }).start();


                } if(msg.arg2 == 2){
                    Toast.makeText(CommendsListActivity.this,"评论失败！",Toast.LENGTH_SHORT).show();
                }
                if(msg.arg1 == 5){
                    lv.notifyDataSetChanged();
                    commendLv.setAdapter(lv);
                    editComm.getText().clear();
                    Toast.makeText(CommendsListActivity.this,"评论成功！",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    /**
     * 给文本框设置
     * @param
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = httpCommend();
        Log.i("repose",respon+"repose");
        if(respon.contains("false")){
            Message mss = new Message();
            mss.arg1 = 1;
            commendHandler.sendMessage(mss);
        }
        commendJsonsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("comment");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("name", jsonArray1.getJSONObject(i).getString("name"));
                params.put("text",jsonArray1.getJSONObject(i).getString("content"));
                commendJsonsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  commendJsonsLists;
    }



    class CommListViewAdapater extends BaseAdapter {

        /**
         * 适配器
         */
        @Override
        public int getCount() {
            return commendLists.size();
        }

        @Override
        public Object getItem(int position) {
            return commendLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(CommendsListActivity.this).inflate(R.layout.commeds_listview_item, null);
            Map<String, String> map = commendLists.get(position);
            comm_name = (TextView) convertView.findViewById(R.id.comm_name);
            comm_text = (TextView) convertView.findViewById(R.id.comm_text);

            String text = map.get("name")+"："+map.get("text");
            StringFormatUtil spanStr = new StringFormatUtil(CommendsListActivity.this, text,
                    map.get("name"), R.color.blue).fillColor();

            comm_name.setText(spanStr.getResult());
//            comm_text.setText(map.get("text"));
            return convertView;
        }
    }

    /**
     * 评论列表的显示初始化
     */
    private void initView() {
        commend_back = (ImageView) findViewById(R.id.commend_back);
        commend_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commendLv = (ListView) findViewById(R.id.commend_listview);
        submitComm = (Button) findViewById(R.id.commend_submit);
        editComm = (EditText) findViewById(R.id.comm_edit);
        submitComm = (Button) findViewById(R.id.commend_submit);
        submitComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editComm.getText().toString().trim().equals("")){
                    progressDialog.setMessage("正在提交...");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String ends = submitCommends();
                            Message message = new Message();
                            if(ends.contains("true")){
                                message.arg2 = 1;
                            }else{
                                message.arg2 = 2;
                            }
                            commendHandler.sendMessage(message);
                        }
                    }).start();
                }else{
                    Toast.makeText(CommendsListActivity.this,"说点什么吧！",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     *提交评论
     */
    private String submitCommends() {
        String requestUrl = HttpAddress.ADDRESSHTTP + "/comment/addComment.do";
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
            String content = "mTel=" + URLEncoder.encode(userPhone, "UTF-8");
            content +="&content=" + URLEncoder.encode(editComm.getText().toString().trim(), "UTF-8");
            content +="&talkId=" + URLEncoder.encode(talkId, "UTF-8");
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
                Log.i("posttt", resultData);
            } else {
                Log.i("posttt", "读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }


}
