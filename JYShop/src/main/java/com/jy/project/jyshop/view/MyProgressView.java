package com.jy.project.jyshop.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.jy.project.jyshop.R;


public class MyProgressView extends Dialog {


	private static  Dialog progressDialog;

	public MyProgressView(@NonNull Context context) {
		super(context);

	}



	/**
	 * @Description: TODO 固定加载提示内容
	 * @param context
	 */
	public static void buildProgressDialog(Activity context, String message) {
		if (progressDialog == null) {
			progressDialog = new Dialog(context, R.style.progress_dialog);
		}
		progressDialog.setContentView(R.layout.dialog_my);
		progressDialog.setCancelable(true);
		progressDialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView msg =  progressDialog.findViewById(R.id.id_tv_loadingmsg);
		msg.setText(message);
		if(!context.isFinishing() && context != null){
			progressDialog.show();
		}
	}

	/**
	 * @Description: TODO 取消加载框
	 */
	public static  void cancelProgressDialog(Activity activity) {
		if(!activity.isFinishing() && activity != null){
			progressDialog.dismiss();
		}

	}

}
