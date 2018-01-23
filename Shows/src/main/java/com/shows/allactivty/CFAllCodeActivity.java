package com.shows.allactivty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.CircleImageView;
import com.com.shows.utils.DataCleanManager;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;
import com.shows.view.RoundCornerImageView;
import com.yixinke.shows.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFAllCodeActivity extends BaseActivity {


    private  Bundle bundle;
    private TextView smx_title;
    private ListView code_lv;
    private TextView cf_add_num,cf_add_buy_num,cf_add_name,cf_add_stime;
    private CircleImageView cf_add_head;
    private ProgressDialog progressDialog;
    private CodeHandler codeHandler;
    //解析相关
    List<Map<String, String>> codeUrlsLists ;
    List<Map<String, String>> codeUrls ;
    JSONObject json;

    //图片加载相关
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cfall_code);

        Intent intent = getIntent();
        bundle = intent.getExtras();
        progressDialog = new ProgressDialog(this);
        codeHandler = new CodeHandler();

        smx_title = (TextView) findViewById(R.id.smx_title);
        smx_title.setText("所有记录");

        ImageLoadInfo();

        code_lv = (ListView) findViewById(R.id.allcode_lv);

        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                codeUrls = viewSetText();
                Message msg = new Message();
                if(codeUrls.size() == 0){
                    msg.arg1 = 1;
                }
                msg.arg2 = 1;
                codeHandler.sendMessage(msg);
            }
        }).start();

    }




    /**
     * 给文本框设置
     * @return
     */
    public List<Map<String, String>> viewSetText(){
        String respon = httpPostGetInfo();
        codeUrlsLists = new ArrayList<Map<String, String>>();
        HashMap<String,String> params;
        try {
            if(respon.contains("false")){
                Message mess = new Message();
                mess.what =3 ;
                codeHandler.sendMessage(mess);
            }
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("CrowdFunding");
            for (int i = 0 ; i < jsonArray1.length();i++){
                params = new HashMap<String, String>();
                params.put("id", jsonArray1.getJSONObject(i).getString("id"));
                params.put("fheadImg",jsonArray1.getJSONObject(i).getString("fheadImg"));
                params.put("fname", jsonArray1.getJSONObject(i).getString("fname"));
                params.put("fnum", jsonArray1.getJSONObject(i).getString("fnum"));
                params.put("ftime", jsonArray1.getJSONObject(i).getString("ftime"));
                params.put("tel", jsonArray1.getJSONObject(i).getString("ftel"));
                codeUrlsLists.add(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  codeUrlsLists;
    }


    /**
     * 数据适配器
     */
    class ListViewAdapter extends BaseAdapter {
        public ListViewAdapter() {
        }

        @Override
        public int getCount() {
            return codeUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return codeUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(CFAllCodeActivity.this).inflate(R.layout.code_cf_all_lv_item, null);
            }

            cf_add_buy_num = (TextView) convertView.findViewById(R.id.cf_add_buy_num);
            cf_add_name = (TextView) convertView .findViewById(R.id.cf_add_name);
            cf_add_num = (TextView) convertView.findViewById(R.id.cf_add_num);
            cf_add_head = (CircleImageView) convertView.findViewById(R.id.cf_add_head);
            cf_add_stime = (TextView) convertView.findViewById(R.id.cf_add_stime);

            final Map<String, String> map = codeUrls.get(position);
            cf_add_buy_num.setText("参与者电话:" +map.get("tel"));
            cf_add_name.setText("参与者:"+map.get("fname"));
            cf_add_num.setText("当次参与份数:" +map.get("fnum"));
            cf_add_stime.setText("参与时间:"+map.get("ftime"));
            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
            imageLoader.displayImage(map.get("fheadImg"), cf_add_head, options);

            return convertView;
        }
    }


    /**
     * 图片加载设置
     */
    private void ImageLoadInfo() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(CFAllCodeActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.fzhanweitu)
                .showImageForEmptyUri(R.mipmap.fzhanweitu)
                .showImageOnFail(R.mipmap.fzhanweitu)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    /**
     * 异步更新UI
     */
    class CodeHandler extends Handler {
        public CodeHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                progressDialog.dismiss();
                if(msg.arg2 ==1){
                    code_lv.setAdapter(new ListViewAdapter());
                }
                if(msg.what == 3 ){
                    progressDialog.dismiss();
                    TextView tv= (TextView) findViewById(R.id.wawa_weiqu);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("该宝贝还没有夺宝记录!");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }




    /**
     * exit this Acticvity
     * @param view
     */
    public void finisThis(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * get http data
     * @return
     */
    public String httpPostGetInfo(){
        String requltUrl= HttpAddress.ADDRESSHTTP + "/CrowdFunding/getCrowd.do";
        String result= HttpUtils.postHttp2(requltUrl,"goodId",bundle.getString("ID"));
        return result;
    }
}
