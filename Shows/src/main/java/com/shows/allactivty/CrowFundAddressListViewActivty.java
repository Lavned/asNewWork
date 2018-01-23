package com.shows.allactivty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.DataCleanManager;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.shows.view.BaseActivity;
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
import java.util.UUID;

public class CrowFundAddressListViewActivty extends BaseActivity {

    ListView address_lv;
    TextView add_button;
    ImageView back;

    //存取电话号码相关
    private String phonenumn;
    SharedPreferences mPreferences;

    /**
     * 解析相关
     */
    TextView address_name,address_tel,addresss_detail;
    List<Map<String, String>> addressUrlsLists ;
    List<Map<String, String>> addressUrls ;
    JSONObject json;

    //更新UI相关
    AHhandler addresshandler;
    private Bundle bundle;
    private String goodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address_list_view_activty);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenumn = mPreferences.getString("telephone", "");

        Intent intent = getIntent();
        bundle = intent.getExtras();
        goodId = bundle.getString("goodId");
        Log.i("phonenumn", phonenumn+"num"+goodId);

        addresshandler = new AHhandler();

        address_lv = (ListView) findViewById(R.id.address_list);
        add_button = (TextView) findViewById(R.id.add_address_button);
        back = (ImageView) findViewById(R.id.address_lv_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrowFundAddressListViewActivty.this, AddAddressActivty.class));
            }
        });

    }

    @Override
    protected void onResume() {
        newDataAsAddress();
        super.onResume();
    }

    /**
     * 获取网络数据
     */
    private void newDataAsAddress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                addressUrls = viewSetText();
                Log.i("eee", addressUrls.size() + "ghegheh");
                Message msg = new Message();
//                if(addressUrls.size() ==0){
//                    msg.arg2 =1;
//                }
                msg.arg1 = 1;
                addresshandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }
        }).start();
    }

    class AHhandler extends Handler {
        public AHhandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("Addresshandler", "Addresshandler......");
            try{
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    address_lv.setAdapter(new ListViewAdapter());
                }
                if(msg.what ==1){
                    TextView weiqu = (TextView) findViewById(R.id.wawa_weiqu);
                    weiqu.setVisibility(View.VISIBLE);
                    weiqu.setText("还没有地址！");
                }
                if(msg.arg2 ==1){
                    Log.i("eeee", "eee");
                    new AlertDialog.Builder(CrowFundAddressListViewActivty.this)
                            .setTitle("温馨提示")
                            .setMessage("地址添加成功，请保持电话畅通，我们会安排快递员给您配送！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    SharedPreferences preferences = getSharedPreferences(
                                            "address_ok", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("address_state", "yes");
                                    editor.putString("goodId", goodId);
                                    editor.commit();
                                    finish();
                                }
                            })
                            .show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addressUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return addressUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(CrowFundAddressListViewActivty.this).inflate(R.layout.address_listview_item, null);
            }
            final Map<String, String> map = addressUrls.get(position);
            address_name = (TextView) convertView.findViewById(R.id.address_username);
            address_tel = (TextView) convertView.findViewById(R.id.address_tel);
            addresss_detail = (TextView) convertView.findViewById(R.id.address_detail);
            address_name.setText(map.get("username"));
            address_tel.setText(map.get("recipientstel"));
            addresss_detail.setText(map.get("addresss"));

            address_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int iid, long id) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String ends =   subimtAddress(addressUrls.get(iid).get("ID"));
                            if(ends.contains("true")){
                                Message ms = new Message();
                                ms.arg2 = 1;
                                addresshandler.sendMessage(ms);
                            }
                        }
                    }).start();
                }
            });
            return convertView;
        }
    }
    private  String subimtAddress(String aid){
        String resultData ="";
        URL url = null;
        String  BOUNDARY =  UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP + "/CrowdFunding/updateLuckyAddress.do");
            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
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
            String content = "tel=" + URLEncoder.encode(phonenumn, "UTF-8");
            content +="&goodId=" + URLEncoder.encode(goodId, "UTF-8");
            content +="&addressId=" + URLEncoder.encode(aid, "UTF-8");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null))
            {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if ( resultData != null ){
                Log.i("resultData",resultData);
            }else{
                Log.i("resultData","读取的内容为NULL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }




    /**
     * 获取网络数据
     */
    private String getAddressList() {
        String url = HttpAddress.ADDRESSHTTP+"/address/getAddress.do";
        String result = HttpUtils.postHttp(url,phonenumn);
        Log.i("reslut", result + "sss");
        return  result;
    }

    /**
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = getAddressList();
        addressUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            Log.i("resposese",respon+";;;");
            if(respon.contains("false")){
                Message msg = new Message();
                msg.what = 1;
                addresshandler.sendMessage(msg);
            }
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("Address");
            for (int i =0;i <jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("username", jsonArray1.getJSONObject(i).getString("username"));
                params.put("recipientstel", jsonArray1.getJSONObject(i).getString("recipientstel"));
                params.put("ID", jsonArray1.getJSONObject(i).getString("id"));
                params.put("addresss", jsonArray1.getJSONObject(i).getString("address"));
                addressUrlsLists.add(params);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  addressUrlsLists;
    }

}
