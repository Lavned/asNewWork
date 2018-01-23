package com.shows.allactivty;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yixinke.shows.R;
import com.shows.view.BaseActivity;
import com.shows.view.HeiaiApplication;

public class ChooseLoginActivty extends BaseActivity {

    TextView choose_Login,choose_rediger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_login_activty);

        initChoose();

    }

    /**
     *选择登陆方式
     */
    private void initChoose() {
        choose_Login = (TextView) findViewById(R.id.choose_login);
        choose_rediger = (TextView) findViewById(R.id.choose_rediger);
        //登录
        choose_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChooseLoginActivty.this, PhoneAndPassLogiActivtyn.class);
                startActivity(intent);
                finish();
            }
        });
        //注册
        choose_rediger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChooseLoginActivty.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("111", "111");
//            android.os.Process.killProcess(android.os.Process.myPid());
////            System.exit(0);
//
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(startMain);
//            System.exit(0);//退出程序
//             finish();
            ((HeiaiApplication) getApplicationContext()).finishStack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //自定义一个广播接收器,用来接收应用程序退出广播.
    public class ExitAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (context != null) {

                if (context instanceof Activity) {

                    ((Activity) context).finish();
                } else if (context instanceof FragmentActivity) {

                    ((FragmentActivity) context).finish();
                } else if (context instanceof Service) {

                    ((Service) context).stopSelf();
                }
            }
        }
    }
}
