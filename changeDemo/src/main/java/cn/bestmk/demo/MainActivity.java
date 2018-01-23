package cn.bestmk.demo;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private FragmentOne one;
    private FragmentTwo two;
    private FragmentThree three;
    private FragmentFour four;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImmersionBar.with(this).init();

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        one=new FragmentOne();
        two=new FragmentTwo();
        three=new FragmentThree();
        four=new FragmentFour();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm. beginTransaction();
        transaction.replace(R.id.view, one);
        transaction.commit();
    }


    @Override
    public void onClick(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm. beginTransaction();
        switch (view.getId()) {
            case R.id.button1:
                transaction.replace(R.id.view, one);
                transaction.commit();
                break;
            case R.id.button2:
                transaction.replace(R.id.view, two);
                transaction.commit();
                break;
            case R.id.button3:
                transaction.replace(R.id.view, three);
                transaction.commit();
                break;
            case R.id.button4:
                transaction.replace(R.id.view, four);
                transaction.commit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy(); //释放沉浸式Title,必须调用该方法，防止内存泄漏
    }
}
