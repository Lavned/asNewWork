package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyCodeHistoryActivity extends BaseActivity {

    private String goodID;
    private ListView his_lv;
    private ImageView back;
    private TextView weiqu;
    private ProgressDialog progressDialog;
    private JSONObject json;
    private List<Map<String, String>> buyJsonsLists ;
    private List<Map<String, String>> buyLists ;

    private HisHandler hisHandler;
    private TextView name,stime;
    private ImageView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buy_code_history);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        goodID = bundle.getString("gid");

        initUI();

        hisHandler = new HisHandler();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中.....");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                buyLists = JsonTextSetView();
                if(buyLists.size() == 0){
                    msg.arg1 = 1;
                }
                msg.arg2 = 2 ;
                hisHandler.sendMessage(msg);
            }
        }).start();
    }


    /**
     * 异步更新UI
     */
    class HisHandler extends Handler {
        public HisHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                progressDialog.dismiss();
                weiqu = (TextView) findViewById(R.id.wawa_weiqu);
                if(msg.arg1 == 1){
                    weiqu.setVisibility(View.VISIBLE);
                    weiqu.setText("还没有购买历史记录！");
                }
                if(msg.arg2  == 2 ){
                    // 此处可以更新UI
                    try{
                       his_lv.setAdapter(new HisListViewAdapter());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /***
     * 适配器
     */
    public class HisListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return buyLists.size();
        }

        @Override
        public Object getItem(int position) {
            return buyLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(BuyCodeHistoryActivity.this).inflate(R.layout.history_list_item, null);
            }
            Map<String, String> map = buyLists.get(position);
            name = (TextView) convertView.findViewById(R.id.buy_name);
            stime = (TextView) convertView.findViewById(R.id.buy_stime);
            head = (ImageView) convertView.findViewById(R.id.buy_head);

            name.setText(map.get("username"));
            stime.setText("购买时间:"+map.get("creatime"));
            Glide.with(BuyCodeHistoryActivity.this)
                    .load(map.get("userhead"))
                    .placeholder(R.mipmap.zhanweitu)
                    .error(R.mipmap.zhanweitu)
                    .centerCrop()
                    .crossFade()
                    .into(head);
            return convertView;
        }
    }

    /**
     * 解析JSON
     * @param
     * @return
     */
    public List<Map<String, String>> JsonTextSetView(){
        String respon = getJsonData();
        buyJsonsLists = new ArrayList<>();
        HashMap<String,String> params;
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("Purchaserecord");
            for (int i =0;i<jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("username",jsonArray1.getJSONObject(i).getString("username"));
                params.put("userhead", jsonArray1.getJSONObject(i).getString("userhead"));
                params.put("creatime",jsonArray1.getJSONObject(i).getString("creatime"));
                buyJsonsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  buyJsonsLists;
    }



    /**
     * 获取网络数据
     * @return
     */
   private String getJsonData(){
       String requestStr = HttpAddress.ADDRESSHTTP + "/good/getGoodsRecord.do?id="+goodID;
       String end = HttpUtils.getHttp(requestStr);
       Log.i("endsensdsd",end+"sddd");
       return  end;
    }

    /**
     * UI初始化
     */
    private void initUI() {
        his_lv = (ListView) findViewById(R.id.history_lv);
        weiqu = (TextView) findViewById(R.id.wawa_weiqu);
        back = (ImageView) findViewById(R.id.history_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
