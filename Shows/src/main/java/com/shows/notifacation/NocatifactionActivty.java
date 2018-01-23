package com.shows.notifacation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.com.shows.utils.ClickUtils;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.MainActivity;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NocatifactionActivty extends BaseActivity {

    private EditText msgText;
    public static boolean isForeground = false;

    private ListView listview,userListView;
    private TextView deleteUserInfo;

    private JSONObject json;

    private  String title,stime,content;

    private List<Map<String, String>> nocaJsonsLists ;
    private List<Map<String, String>> nocaLists ;

    private List<Map<String, String>> UserInfoJsonsLists ;
    private List<Map<String, String>> UserInfoLists ;

    private TextView tv_title,tv_detail,tv_stime;
    private ImageView noca_back;
    private ProgressDialog progressDialog;
    private TextView systemInfo,userInfo;
    private SharedPreferences mPreferences;
    private String  phonenum;
    private DeleteHandler deleteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nocatifaction_activty);
        //请求
        ApiStoreSDK.init(getApplicationContext(), "01cacd48ac45b99dc89b10d12cf815ef");

        progressDialog = new ProgressDialog(this);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenum = mPreferences.getString("telephone", "");
        Log.i("userphone", phonenum);

        deleteHandler = new DeleteHandler();

        listview = (ListView) findViewById(R.id.nocati_lv);
        userListView  = (ListView) findViewById(R.id.usernocati_lv);

        systemInfo = (TextView) findViewById(R.id.systeminfo);
        userInfo =  (TextView) findViewById(R.id.userinfo);
        deleteUserInfo = (TextView)  findViewById(R.id.deleuserinfo);

        progressDialog.setMessage("加载中......");
        progressDialog.show();
        getAllNoca();
        noca_back = (ImageView) findViewById(R.id.noca_back);
        noca_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtils.isFastDoubleClick()) {
                    return;
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", nocaLists.get(position).get("title"));
                    bundle.putString("content", nocaLists.get(position).get("content"));
                    bundle.putString("stime", nocaLists.get(position).get("stime"));
                    intent.putExtras(bundle);
                    intent.setClass(NocatifactionActivty.this, NocatifactionDetail.class);
                    startActivity(intent);
                }
            }
        });
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtils.isFastDoubleClick()) {
                    return;
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", UserInfoLists.get(position).get("title"));
                    bundle.putString("content", UserInfoLists.get(position).get("content"));
                    bundle.putString("stime", UserInfoLists.get(position).get("stime"));
                    intent.putExtras(bundle);
                    intent.setClass(NocatifactionActivty.this, NocatifactionDetail.class);
                    startActivity(intent);
                }
            }
        });

        systemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemInfo.setTextColor(Color.parseColor("#ffffff"));
                systemInfo.setBackgroundColor(Color.parseColor("#80000000"));
                userInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                userInfo.setTextColor(Color.parseColor("#80000000"));
                deleteUserInfo.setVisibility(View.GONE);
                userListView.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                getAllNoca();
            }
        });
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.setTextColor(Color.parseColor("#ffffff"));
                userInfo.setBackgroundColor(Color.parseColor("#80000000"));
                systemInfo.setBackgroundColor(Color.parseColor("#ffffff"));
                systemInfo.setTextColor(Color.parseColor("#80000000"));
                deleteUserInfo.setVisibility(View.VISIBLE);
                listview.setVisibility(View.GONE);
                userListView.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String resultResponse = getUserInfo();
                        UserInfoLists = getNocaJsonText(resultResponse);
                        Message msg = new Message();
                        msg.arg2 = 3;
                        deleteHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
        deleteUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String resultEnd = httpDeleteUserInfo();
                        if(resultEnd.contains("true")){
                            Message msg = new Message();
                            msg.arg2 = 1;
                            deleteHandler.sendMessage(msg);
                        }
                    }
                }).start();

            }
        });


    }


    /**
     *删除信息
     */
    class DeleteHandler extends Handler {
        public DeleteHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("MyHandler", "handleMessage......");
            try{
                progressDialog.dismiss();
                if(msg.arg2 == 1){
                    // 此处可以更新UI
                    UserInfoLists.clear();
                    ListViewAdpater lsv =  new ListViewAdpater(UserInfoLists);
                    lsv.notifyDataSetChanged();
                    userListView.setAdapter(lsv);
                }
                if(msg.arg2 == 3){
                    // 此处可以更新UI
                    ListViewAdpater lsv =  new ListViewAdpater(UserInfoLists);
                    lsv.notifyDataSetChanged();
                    userListView.setAdapter(lsv);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }



    //请求解析JSON
    public List<Map<String, String>> getNocaJsonText(String respon){
        nocaJsonsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("smxJpush");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                title = jsonArray1.getJSONObject(i).getString("title");
                content = jsonArray1.getJSONObject(i).getString("content");
                stime = jsonArray1.getJSONObject(i).getString("stime");
                String formatTime= stime.substring(5,11);
                params.put("title",jsonArray1.getJSONObject(i).getString("title"));
                params.put("content", jsonArray1.getJSONObject(i).getString("content"));
                params.put("stime",stime);
                params.put("formatTime",formatTime);

                nocaJsonsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  nocaJsonsLists;
    }

    /**
     * 删除信息请求服务
     */

    private String httpDeleteUserInfo() {
        String httpUrl = HttpAddress.ADDRESSHTTP+"/jpush/delByTel.do";
        String end = HttpUtils.postHttp(httpUrl,phonenum);
        Log.i("endsss",end+"end");
        return  end;
    }


    /**
     * 系统消息请求服务器
     */
    private void getAllNoca() {

        Parameters para = new Parameters();

        para.put("","");
       String urlStr= HttpAddress.ADDRESSHTTP+"/jpush/pushAll.do";
        ApiStoreSDK.execute(urlStr,
                ApiStoreSDK.GET,
                para,
                new ApiCallBack() {

                    @Override
                    public void onSuccess(int status, String responseString) {
                        Log.i("sdkdemo", "onSuccess");
                        progressDialog.dismiss();
                        try {
                            nocaLists = getNocaJsonText(responseString);
                            listview.setAdapter(new ListViewAdpater(nocaLists));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.i("sdkdemo", "onComplete");
                    }

                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        Toast.makeText(NocatifactionActivty.this, "服务异常！", Toast.LENGTH_SHORT).show();
                        Log.i("sdkdemo", "onError, status: " + status);
                        Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                        Log.i("sdkdemo", getStackTrace(e));
                    }
                });

    }



    /**
     *
     * @return
     */
    private String  getUserInfo(){
        String httpUrl= HttpAddress.ADDRESSHTTP+"/jpush/getPushByPersonal.do";
        String requestStr = HttpUtils.postHttp(httpUrl,phonenum);
        Log.i("requestStr", requestStr);
        return requestStr;
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



    public class ListViewAdpater extends BaseAdapter{

        private List<Map<String, String>> Lists;
        public  ListViewAdpater(List<Map<String, String>> Listsss){
            this.Lists = Listsss;
        }


        @Override
        public int getCount() {
            return Lists.size();
        }

        @Override
        public Object getItem(int position) {
            return Lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(NocatifactionActivty.this).inflate(R.layout.nocatifacation_list_item, null);
            }
            Map<String, String> map = Lists.get(position);
            tv_title = (TextView) convertView.findViewById(R.id.noca_title);
            tv_detail = (TextView) convertView.findViewById(R.id.noca_content);
            tv_stime = (TextView) convertView.findViewById(R.id.noca_stime);
            tv_title.setText(map.get("title"));
            tv_detail.setText(map.get("content"));
            tv_stime.setText(map.get("formatTime"));
            return convertView;
        }
    }

    private MainActivity.MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

}
