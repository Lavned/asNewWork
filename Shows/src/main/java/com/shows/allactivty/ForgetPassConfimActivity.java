package com.shows.allactivty;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.HttpUtils;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class ForgetPassConfimActivity extends BaseActivity implements OnClickListener {

    // 手机号输入框
    private EditText inputPhoneEt;

    // 验证码输入框
    private EditText inputCodeEt;

    // 获取验证码按钮
    private Button requestCodeBtn;

    // 注册按钮
    private Button commitBtn;
    String isBoolean;

    private TextView pass_title;
    private Button buttonyy;

    //
    int i = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    /**
     * 初始化控件
     */
    private void init() {
        buttonyy = (Button) findViewById(R.id.yuyin);
        buttonyy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPhoneEt.getText().toString().trim().equals("")) {
                    Toast.makeText(ForgetPassConfimActivity.this,"请先输入电话号码",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SMSSDK.getVoiceVerifyCode("86", inputPhoneEt.getText().toString());
                    Message msg = new Message();
                    //                SMSSDK.getVerificationCode("86", phoneNums);
                    // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (; i > 0; i--) {
                                handler.sendEmptyMessage(-9);
                                if (i <= 0) {
                                    break;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(-8);
                        }
                    }).start();
                }
            }
        });
        pass_title = (TextView) findViewById(R.id.pass_title);
        pass_title.setText("找回密码");
        inputPhoneEt = (EditText) findViewById(R.id.login_input_phone_et);
        inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
        requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
        commitBtn = (Button) findViewById(R.id.login_commit_btn);
        commitBtn.setText("开始验证");
        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        // 启动短信验证sdk
        SMSSDK.initSDK(this, "113a9b4570438", "ffaf0abc75cb863e377e6f0ec02fe3ef");
        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }
    String ends ="";
    private String isRegider(){
        final String requestUrl = HttpAddress.ADDRESSHTTP+"/custom/judgeReg.do";
        ends = HttpUtils.postHttp(requestUrl,inputPhoneEt.getText().toString());
        Log.i("ends", ends);
        return  ends;
    }

    @Override
    public void onClick(View v) {
        final String phoneNums = inputPhoneEt.getText().toString();
        switch (v.getId()) {
            case R.id.login_request_code_btn:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNums)) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        isBoolean = isRegider();
                        Log.i("isBoolean", isBoolean+"isBoolean");
                        if(isBoolean.contains("false")){
                            msg.arg2 =1;
                            handler.sendMessage(msg);
                        }else{
                            SMSSDK.getVerificationCode("86", phoneNums);
                            // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                            msg.arg1 =1;
                            handler.sendMessage(msg);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (; i > 0; i--) {
                                        handler.sendEmptyMessage(-9);
                                        if (i <= 0) {
                                            break;
                                        }
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    handler.sendEmptyMessage(-8);
                                }
                            }).start();
                        }
                    }
                }).start();

                break;

            case R.id.login_commit_btn:
                SMSSDK.submitVerificationCode("86", phoneNums, inputCodeEt
                        .getText().toString());
                createProgressBar();
                break;
        }
    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            if(msg.arg1 ==1){
                requestCodeBtn.setClickable(false);
                requestCodeBtn.setText("重新发送(" + i + ")");
            }
            if(msg.arg2 == 1){
                Toast.makeText(ForgetPassConfimActivity.this, "该手机号还没注册！", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == -9) {
                requestCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setClickable(true);
                i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    try{
                        // 短信注册成功后，返回MainActivity,然后提示
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                            Toast.makeText(getApplicationContext(), "提交验证码成功",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgetPassConfimActivity.this,
                                    ForgetPassActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("tel",inputPhoneEt.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            Toast.makeText(getApplicationContext(), "验证码已经发送",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ((Throwable) data).printStackTrace();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    };


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * progressbar
     */
    private void createProgressBar() {
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ProgressBar mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ForgetPassConfimActivity.this, ChooseLoginActivty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//别忘了这行，否则退出不起作用
            startActivity(intent);
            finish();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
