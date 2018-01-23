package com.shows.allactivty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

public class OrderInfoDetailActivity extends BaseActivity {

    TextView detail_name,detail_price,detail_getname,detail_gettel,detail_getaddress,detail_remark,detail_yesget;
    ImageView order_detail_back;

    String state,id,result;

    private ProgressDialog progressDialog;
    OrderInfoHandler orderInfoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_info_detail);
        initOrderDetail();

        progressDialog = new ProgressDialog(this);
        orderInfoHandler = new OrderInfoHandler();

        Intent intent = getIntent();
        if(intent !=null){
            Bundle bundle = intent.getExtras();
            detail_name.setText(bundle.getString("name"));
            detail_price.setText("￥"+bundle.getString("price"));
            detail_getname.setText(bundle.getString("username"));
            detail_gettel.setText(bundle.getString("recipientstel"));
            detail_getaddress.setText("甘肃省兰州市"+bundle.getString("address"));
            detail_remark.setText(bundle.getString("remark"));
            state = bundle.getString("state");
            id = bundle.getString("gid");


        }

        if(state.equals("正在发货")){
            detail_yesget.setText("确认收货");
            detail_yesget.setClickable(true);
            confimGoods();
        }else if(state.equals("交易成功")){
            detail_yesget.setText("已收货");
            detail_yesget.setClickable(false);
        }
        order_detail_back = (ImageView) findViewById(R.id.order_detail_back);
        order_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 异步更新UI
     */
    class OrderInfoHandler extends Handler {
        public OrderInfoHandler() {
        }
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("orderHandler", "handleMessage......");
            try{
                if(msg.arg2 ==1){
                    progressDialog.dismiss();
                    // 此处可以更新UI
                    detail_yesget.setText("已收货");
                    detail_yesget.setClickable(false);
                }
                if(msg.arg1 ==1 ){
                    progressDialog.dismiss();
                    Toast.makeText(OrderInfoDetailActivity.this, "服务异常，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 修改状态
     */
    private void confimGoods() {
        detail_yesget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OrderInfoDetailActivity.this)
                        .setMessage("请您在确认收到包裹后收货，如有包裹遗失，本平台概不负责！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("正在加载...");
                                progressDialog.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        result = confirmGoodInfo();
                                        Message msg = new Message();
                                        if(result.contains("false")){
                                            msg.arg1  =1;
                                        }
                                        msg.arg2 = 1;
                                        orderInfoHandler.sendMessage(msg);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }


    /***
     *商品信息接口
     */
    private String  confirmGoodInfo() {
        String argname="purchasehistoryID";
        String requestUrl= HttpAddress.ADDRESSHTTP+"/purchasehistory/take.do";
        String result = HttpUtils.postHttp2(requestUrl, argname,id);
        Log.i("result", result);
        return  result;
    }

    /**
     * 初始化
     */
    private void initOrderDetail() {
        detail_name = (TextView) findViewById(R.id.order_detail_name);
        detail_price = (TextView) findViewById(R.id.order_detail_price);
        detail_getname = (TextView) findViewById(R.id.order_detail_getname);
        detail_gettel = (TextView) findViewById(R.id.order_detail_gettel);
        detail_getaddress = (TextView) findViewById(R.id.order_details_get_address);
        detail_remark = (TextView) findViewById(R.id.order_detail_remark);
        detail_yesget = (TextView) findViewById(R.id.order_detail_yes_get);
    }
}
