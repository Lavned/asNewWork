package com.com.shows.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.shows.bean.VersionInfo;
import com.yixinke.shows.MainActivity;

import java.net.URL;


/**
 * 检查更新
 */
public class UpdateManager {
	
//	private static CommonDialog dialog;
	/**
	 * 检测软件更新
	 */
	public static void checkUpdate(Context context, VersionInfo bean) {
		if (isUpdate(context, bean)) {
			// 显示提示对话框
			updateApk(context, bean);
		}
	}
	
	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	public static boolean isUpdate(Context context, VersionInfo bean) {
		// 获取当前软件版本
		int versionCode = getVersionCode(context);
		//服务器上的版本信息
		int serviceCode = bean.getVersionCode();

		Log.i("kkkkkkkkk",versionCode+"ddd"+serviceCode);
		// 版本判断
		if (serviceCode > versionCode) {
			return true;
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return versionCode
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	/**
	 * 获取软件版本名
	 * 
	 * @param context
	 * @return versionName
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "v"+versionName;
	}

	/**
	 * 提示更新
	 */
	public synchronized static void updateApk(final Context context, final VersionInfo bean) {
		// 构造软件下载对话框
		
//		dialog = DialogUtils.showTipsDialog(context, "版本更新", "现在更新", "以后再说", bean.getContent(), false, new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				switch (v.getId())
//				{
//				case R.id.dialog_button_ok:
//					String apkName = "heiai_" + bean.getVersionCode()+ ".apk";
//					Intent intent = new Intent(context,UpdateService.class);
//		            intent.putExtra("Key_App_Name", apkName);
//		            intent.putExtra("Key_Down_Url",bean.getDownUrl());
//		            context.startService(intent);
//				case R.id.dialog_button_cancel:
//					dialog.dismiss();
//					break;
//				}
//			}
//		});

		new AlertDialog.Builder(context)
				.setMessage("检测到有新版本，是否更新？")
				.setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int which) {
						Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.yixinke.shows");
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						context.startActivity(it);
					}
				})
				.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences uid_preferences = context.getSharedPreferences(
								"update_pref", context.MODE_PRIVATE);
						SharedPreferences.Editor uid_editor = uid_preferences.edit();
						uid_editor.putString("isaftertime", "true") ;
						uid_editor.commit();
					}
				})
				.show();
	}
}