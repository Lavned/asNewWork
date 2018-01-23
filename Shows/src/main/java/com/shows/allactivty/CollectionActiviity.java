package com.shows.allactivty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shows.bean.Picture;
import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.com.shows.utils.CacheUtils;
import com.com.shows.utils.HttpAddress;
import com.yixinke.shows.InfoDetailsActivity;
import com.yixinke.shows.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shows.view.BaseActivity;
import com.shows.view.SwipeAdapter;
import com.shows.view.SwipeListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CollectionActiviity extends BaseActivity {

    SwipeListView listView;
    TextView tv_title,tv_detail;
    ImageView iv_pic;

    TextView item_right_txt;
    RelativeLayout item_right ;


    MyHandler myHandler;

    SharedPreferences mPreferences;
    String uid;

    List<Picture> collJsonsLists ;
    private JSONObject json;

    String advId;

    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    SwipeAdapter mAdapter;
    String inputLine = "";
    ImageView collection_back;

    TextView weiqu;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collection_activiity);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        uid = mPreferences.getString("uid", "");
        Log.i("userid", uid);

        progressDialog = new ProgressDialog(this);

        weiqu = (TextView) findViewById(R.id.wawa_weiqu);
        String cache = CacheUtils.getCache(HttpAddress.ADDRESSHTTP+"/custom/careList.do?cid="+uid, CollectionActiviity.this);
        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
            collJsonsLists = getJsonText(cache);
            System.out.println(collJsonsLists.size() + "VVVVs");
        }
        listView = (SwipeListView)findViewById(R.id.ls_collection);

        progressDialog.setMessage("加载中......");
        progressDialog.show();
        apiTest();
        ImageLoaderList();
        myHandler = new MyHandler();

        collection_back= (ImageView) findViewById(R.id.collection_back);
        collection_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    class MyHandler extends Handler {
        public MyHandler() {}
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            try{
                progressDialog.dismiss();
                if(msg.what ==1){
                    if(collJsonsLists.size() == 0){
                        weiqu.setVisibility(View.VISIBLE);
                        weiqu.setText("还没有收藏呢！");
                    }else{
                        progressDialog.dismiss();
                        // 此处可以更新UI
                       mAdapter = new SwipeAdapter(CollectionActiviity.this,collJsonsLists,listView.getRightViewWidth());
                        Log.i("'mAdapter", collJsonsLists.size() + "jj");
                        try{
                            mAdapter.setOnRightItemClickListener(new SwipeAdapter.onRightItemClickListener() {

                                @Override
                                public void onRightItemClick(View v, int position) {
                                        listView.deleteItem(listView.getChildAt(position));
                                        advId = collJsonsLists.get(position).getId();
                                        collJsonsLists.remove(position);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String unColl = unCollection();
                                                if(unColl.contains("true")){
                                                    Log.i("uncoll",unColl);
                                                }
                                            }
                                        }).start();
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(CollectionActiviity.this, "删除第  " + (position + 1) + " 条收藏成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            listView.setAdapter(mAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //                                Toast.makeText(CollectionActiviity.this, "click  " + (position + 1), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", collJsonsLists.get(position).getTitle());
                                    bundle.putString("content", collJsonsLists.get(position).getContent());
                                    bundle.putString("header", collJsonsLists.get(position).getHeader());
                                    bundle.putString("tel", collJsonsLists.get(position).getTel());
                                    bundle.putString("address", collJsonsLists.get(position).getAddress());
                                    bundle.putString("id", collJsonsLists.get(position).getId());
                                    bundle.putString("route", collJsonsLists.get(position).getRoute());
                                    bundle.putString("code", collJsonsLists.get(position).getCode());
                                    bundle.putString("httpaddress",collJsonsLists.get(position).getHttpAddress());
                                    bundle.putString("introduce", collJsonsLists.get(position).getTextDetail());
                                    bundle.putStringArrayList("picsss", (ArrayList<String>) collJsonsLists.get(position).getPics());
                                    bundle.putStringArrayList("imagesurl", (ArrayList<String>) collJsonsLists.get(position).getImages());
                                    intent.putExtras(bundle);
                                    intent.setClass(CollectionActiviity.this, InfoDetailsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }catch(Exception e){
                            e.printStackTrace();
                           Log.i("e'",e.toString());
                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    /**
     *  取消收藏
     */

    public String unCollection(){
        URL localURL = null;
        StringBuffer resultBuffer = null;
        try {
            localURL = new URL(HttpAddress.ADDRESSHTTP+"/advert//advertCel.do?aid="+advId+"&cid="+uid);
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            resultBuffer = new StringBuffer();
            if (httpURLConnection.getResponseCode() >= 500) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());}
            try {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                while ((inputLine = reader.readLine()) != null) {
                    resultBuffer.append(inputLine);
                    Log.i("Collectioncel",resultBuffer.toString());
                }
            } finally {
                if (reader != null) {
                    reader.close();}
                if (inputStreamReader != null) {
                    inputStreamReader.close();}
                if (inputStream != null) {
                    inputStream.close();}
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Collectioncel",resultBuffer.toString());
        return resultBuffer.toString();
    }


    /**
     * 图片加载设置
     */
    public void ImageLoaderList() {
        imageLoader.init(ImageLoaderConfiguration.createDefault(CollectionActiviity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.luncher)
                .showImageForEmptyUri(R.mipmap.luncher)
                .showImageOnFail(R.mipmap.luncher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
    }

    public List<Picture> getJsonText(String reponseStr){
        List<Picture> listPic=new ArrayList<Picture>();
        Picture picture=null;
        try {
            json = new JSONObject(reponseStr);
            JSONArray jsonArray1 = json.getJSONArray("advert");
            for (int i =0;i<jsonArray1.length();i++) {
                picture = new Picture();
                picture.setTitle(jsonArray1.getJSONObject(i).getString("title"));
                picture.setContent(jsonArray1.getJSONObject(i).getString("content"));
                picture.setHeader(HttpAddress.ADDRESSHTTP + jsonArray1.getJSONObject(i).getString("head"));
                picture.setTel(jsonArray1.getJSONObject(i).getString("tel"));
                picture.setAddress(jsonArray1.getJSONObject(i).getString("address"));
                picture.setId(jsonArray1.getJSONObject(i).getString("id"));
                picture.setRoute(jsonArray1.getJSONObject(i).getString("route"));
                picture.setHttpAddress(jsonArray1.getJSONObject(i).getString("httpaddress"));
                picture.setCode(jsonArray1.getJSONObject(i).getString("code"));
                picture.setTextDetail(jsonArray1.getJSONObject(i).getString("introduce"));

                JSONArray jsonArraytemp =  jsonArray1.getJSONObject(i).getJSONArray("pics");//
                List<String> picList=new ArrayList<String>();
                for (int j = 0;j < jsonArraytemp.length();j++){
                    String  pic  =jsonArraytemp.getString(j);
                    picList.add(HttpAddress.ADDRESSHTTP+pic);
                }

                JSONArray jsonArrayImages =  jsonArray1.getJSONObject(i).getJSONArray("pictures");
                List<String> iamgeList=new ArrayList<String>();
                for (int j = 0;j < jsonArrayImages.length();j++){
                    String  pic  =jsonArrayImages.getString(j);
                    iamgeList.add(HttpAddress.ADDRESSHTTP+ pic);
                }
                picture.setPics(picList);
                picture.setImages(iamgeList);
                listPic.add(picture);
                Log.i("xxx",listPic.size()+"");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listPic;
    }

    /**
     * 请求服务器
     */
    private void apiTest() {

        Parameters para = new Parameters();

        para.put("cid",uid+"");
        String urlStr=HttpAddress.ADDRESSHTTP+"/custom/careList.do";
        ApiStoreSDK.execute(urlStr,
                ApiStoreSDK.GET,
                para,
                new ApiCallBack() {

                    @Override
                    public void onSuccess(int status, String responseString) {
                        Log.i("sdkdemo", "onSuccess");
                        collJsonsLists = getJsonText(responseString);
                        Message msg = new Message();
                        msg.what =1;
                        msg.obj = collJsonsLists;
                        myHandler.sendMessage(msg);
                        CacheUtils.setCache(HttpAddress.ADDRESSHTTP+"/custom/careList.do?cid="+uid,
                                responseString, CollectionActiviity.this);
                        Log.i("memda", responseString);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("sdkdemo", "onComplete");
                    }

                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        weiqu.setVisibility(View.VISIBLE);
                        Toast.makeText(CollectionActiviity.this, "数据可能开小差了！！！", Toast.LENGTH_SHORT).show();
                        Log.i("sdkdemos", "onError, status: " + status);
                        Log.i("sdkdemos", "errMsg: " + (e == null ? "" : e.getMessage()));
                        Log.i("sdkdemosss", getStackTrace(e));
                    }
                });

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


    /**
     * 适配器
     */
//    class ListViewAdpater extends BaseAdapter {
//
//
//        private int mRightWidth = 0;
//
//        /**
//         * @param
//         */
//        public ListViewAdpater( int rightWidth) {
//            mRightWidth = rightWidth;
//        }
//
//        @Override
//        public int getCount() {
//            return collJsonsLists.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return collJsonsLists.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            if(convertView == null) {
//                convertView = LayoutInflater.from(CollectionActiviity.this).inflate(R.layout.listview_item, null);
//            }
//            Picture map = collJsonsLists.get(position);
//            tv_title = (TextView) convertView.findViewById(R.id.text_title);
//            tv_detail = (TextView) convertView.findViewById(R.id.text_detail);
//            iv_pic = (ImageView) convertView.findViewById(R.id.image_icons);
//
//            item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
//            item_right_txt = (TextView) convertView .findViewById(R.id.item_right_txt);
//            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//            item_right.setLayoutParams(lp2);
//
//            tv_title.setText(map.getTitle());
//            tv_detail.setText(map.getContent());
//            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
//            imageLoader.displayImage(map.getHeader(), iv_pic, options);
//
//            item_right.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListener != null) {
//                        mListener.onRightItemClick(v, position);
//                    }
//                }
//            });
//
//            return convertView;
//        }
//    }


}
