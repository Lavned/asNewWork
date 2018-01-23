package com.com.shows.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.com.shows.utils.PrefUtils;
import com.yixinke.shows.MainActivity;
import com.shows.allactivty.ChooseLoginActivty;
import com.shows.view.BaseActivity;
import com.yixinke.shows.R;


/**
 * 闪屏页
 *
 */
public class SplashActivity extends BaseActivity {

	RelativeLayout rlRoot;

	//登录相关
	SharedPreferences mPreferences;
	Boolean isLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

		startAnim();

		//LibUtils.doSomething();
		//rlRoot.setBackgroundResource(R.drawable.newscenter_press);
	}

	/**
	 * 开启动画
	 */
	private void startAnim() {

		// 动画集合
		AnimationSet set = new AnimationSet(false);

		// 旋转动画
		RotateAnimation rotate = new RotateAnimation(0, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(1000);// 动画时间
		rotate.setFillAfter(true);// 保持动画状态

		// 缩放动画
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(1000);// 动画时间
		scale.setFillAfter(true);// 保持动画状态

		// 渐变动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(1000);// 动画时间
		alpha.setFillAfter(true);// 保持动画状态

		set.addAnimation(rotate);
		set.addAnimation(scale);
		set.addAnimation(alpha);

		// 设置动画监听
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			// 动画执行结束
			@Override
			public void onAnimationEnd(Animation animation) {
				jumpNextPage();
			}
		});

		rlRoot.startAnimation(set);
	}

	/**
	 * 跳转下一个页面
	 */
	private void jumpNextPage() {
		// 判断之前有没有显示过新手引导
		boolean userGuide = PrefUtils.getBoolean(this, "is_user_guide_showed",
				false);

		if (!userGuide) {
			// 跳转到新手引导页
			startActivity(new Intent(SplashActivity.this, GuideActivity.class));
		} else {
			mPreferences =getSharedPreferences("first_pref", MODE_PRIVATE);
			isLogin = mPreferences.getBoolean("isFirstIn", true);
			if(isLogin) {
				Toast.makeText(SplashActivity.this, "未登录", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, ChooseLoginActivty.class);
				startActivity(intent);
			}else{
				Toast.makeText(SplashActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
			}
		}
		finish();
	}

}
