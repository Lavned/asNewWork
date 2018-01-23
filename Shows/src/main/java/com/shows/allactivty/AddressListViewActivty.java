package com.shows.allactivty;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressListViewActivty extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address_list_view_activty);

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        phonenumn = mPreferences.getString("telephone", "");
        Log.i("phonenumn", phonenumn);


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
                startActivity(new Intent(AddressListViewActivty.this, AddAddressActivty.class));
                finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                addressUrls = viewSetText();
                Log.i("eee",addressUrls.size()+"ghegheh");
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
//                if(msg.arg2 ==1){
//                    TextView weiqu = (TextView) findViewById(R.id.wawa_weiqu);
//                    weiqu.setVisibility(View.VISIBLE);
//                    weiqu.setText("哎哟出错了");
//                }
                if(msg.what ==1){
                    TextView weiqu = (TextView) findViewById(R.id.wawa_weiqu);
                    weiqu.setVisibility(View.VISIBLE);
                    weiqu.setText("还没有地址！");
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(AddressListViewActivty.this).inflate(R.layout.address_listview_item, null);
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
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
//                    Bundle bundle =new Bundle();
//                    bundle.putString("aid",addressUrls.get(position).get("ID"));
//                    intent.putExtras(bundle);
                    intent.setClass(AddressListViewActivty.this, ExchangeGoodDetailActivty.class);
                    SharedPreferences uid_preferences = getSharedPreferences(
                            "address_pref", MODE_PRIVATE);
                    SharedPreferences.Editor uid_editor = uid_preferences.edit();
                    uid_editor.putString("id", addressUrls.get(position).get("ID") );
                    uid_editor.putString("address", addressUrls.get(position).get("addresss") );
                    uid_editor.putString("recipientstel", addressUrls.get(position).get("recipientstel") );
                    uid_editor.putString("username", addressUrls.get(position).get("username") );
                    uid_editor.commit();
                    startActivity(intent);
                    finish();
                }
            });
            return convertView;
        }
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
