package com.shows.photos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.com.shows.utils.ActionSheetDialog;
import com.com.shows.utils.HttpAddress;
import com.myqcloud.filecloud.FileCloudSign;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONObject;


public class PublishActivity extends BaseActivity
{
	private GridView mGridView;
	private ImagePublishAdapter mAdapter;
	private TextView sendTv;
	public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
	EditText shuodian;
	TextView chooseType;
	String infoType,telephone,talkStr;

	String uuid = UUID.randomUUID().toString();

	private String resultEnd;

	//图片上传相关
	PhotoUploadTask imgUploadTask = null;
	private FileInfo mCurrPhotoInfo = null;
	TextView text;
	String bucket = null;
	String signUrl = "http://203.195.194.28/php/getsignv2.php";
	String result = null;
	String appid = null;
	String sign = null;
	String signOne = null;
	String persistenceId = null;
	UploadManager mFileUploadManager = null;
	List<String> listUrl = new ArrayList<String>();
	private static final String APP_ID = "10045379";
	private static final String SECRET_ID = "AKIDEPSAHkOIBpnigMnVhNgY43d9OCunAZXw";
	private static final String SECRET_KEY = "OlD9zfW63ylYg2EapNK4QAj1hiekCV2L";
	private String userid = "123456";
	private StringBuffer mySign;


	private  ProgressDialog progressDialog,progressDialogHttp;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_publish);
		shuodian = (EditText) findViewById(R.id.shuodian);

		progressDialog = new ProgressDialog(this);
		progressDialogHttp =new ProgressDialog(this);
		//获取电话号码
		SharedPreferences mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
		telephone = mPreferences.getString("telephone", "");
		Log.i("pub", telephone + "oo");

		SharedPreferences talkPreferences =getSharedPreferences("talk_pref", MODE_PRIVATE);
		talkStr = talkPreferences.getString("talk", "");
		Log.i("talkStr", talkStr + "talkStr");

		// 去用户的业务务器获取签名
		getUploadImageSign(signUrl);
		// 上传需要到的参数
		long expired = System.currentTimeMillis() / 1000 + 2592000;
//		//生成一个时效性签名
		mySign = new StringBuffer("");
		FileCloudSign.appSign(APP_ID, SECRET_ID, SECRET_KEY, expired, userid, mySign);
		System.out.println("demo mysign: " + mySign.toString());

		// 上传需要到的参数
		appid = "10045379";
		bucket = "shoumeixiu";
		persistenceId = "persistenceId";
		// 1，创建一个上传容器 需要1.appid 2.上传文件类型3.上传缓存（类型字符串，要全局唯一否则）
		mFileUploadManager = new UploadManager(this, appid, Const.FileType.Photo,persistenceId);

		initData();
		initView();
		shuodian.setText(talkStr);
		Intent intent = getIntent();
		if(intent != null){
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				String talkIs = bundle.getString("talkIs");
				if(talkIs != null){
					if(talkIs.equals("talkIs")){
						shuodian.setText("");
					}
				}
			}
		}
	}


	// 获取app 的签名
	private void getUploadImageSign(final String s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(s);
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					InputStreamReader in = new InputStreamReader(urlConnection
							.getInputStream());
					BufferedReader buffer = new BufferedReader(in);
					String inpuLine = null;
					while ((inpuLine = buffer.readLine()) != null) {
						result = inpuLine + "\n";
					}
					JSONObject jsonData = new JSONObject(result);
//					sign = jsonData.getString("sign");
					sign = mySign.toString();
				} catch (Exception e) {
				}
			}
		}).start();

	}

	protected void onPause() {
		Log.i("stopsss","jinlailepause");
		SharedPreferences uid_preferences = getSharedPreferences(
				"talk_pref", MODE_PRIVATE);
		SharedPreferences.Editor uid_editor = uid_preferences.edit();
		uid_editor.putString("talk", shuodian.getText().toString()) ;
		uid_editor.commit();

		saveTempToPref();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		saveTempToPref();
	}

	private void saveTempToPref()
	{
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = JSON.toJSONString(mDataList);
		sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

	}

	private void getTempFromPref()
	{
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
		if (!TextUtils.isEmpty(prefStr))
		{
			List<ImageItem> tempImages = JSON.parseArray(prefStr,
					ImageItem.class);
			mDataList = tempImages;
		}
	}

	private void removeTempFromPref()
	{
		SharedPreferences sp = getSharedPreferences(
				CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
		sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
	}

	@SuppressWarnings("unchecked")
	private void initData()
	{
		getTempFromPref();
		List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
				.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
		if (incomingDataList != null)
		{
			mDataList.addAll(incomingDataList);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		notifyDataChanged(); //当在ImageZoomActivity中删除图片时，返回这里需要刷新
	}


	private  void initTypeChoose(){
		new ActionSheetDialog(PublishActivity.this).builder()
				.setTitle("请选择类型")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem("房产信息", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("房产信息");
								infoType = "0";
							}
						})
				.addSheetItem("二手交易", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("二手交易");
								infoType = "1";
							}
						})
				.addSheetItem("招聘信息", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("招聘信息");
								infoType = "2";
							}
						})
				.addSheetItem("求职信息", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("求职信息");
								infoType = "3";
							}
						})
				.addSheetItem("餐饮娱乐", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("餐饮娱乐");
								infoType = "4";
							}
						})
				.addSheetItem("相亲交友", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("相亲交友");
								infoType = "5";
							}
						})
				.addSheetItem("寻人寻物", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("寻人寻物");
								infoType = "6";
							}
						})
				.addSheetItem("教育培训", ActionSheetDialog.SheetItemColor.Red,
						new ActionSheetDialog.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								chooseType.setText("教育培训");
								infoType = "7";
							}
						})
				.show();
	}

	public void initView()
	{

		chooseType = (TextView) findViewById(R.id.choose_type);
		chooseType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initTypeChoose();
			}
		});
//		chooseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				infoType = chooseType.getSelectedItem().toString();
//				long infoTypes =  chooseType.getSelectedItemId();
//				infoType = Long.toString(infoTypes);
//				Log.i("type",infoType);
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});

		TextView titleTv  = (TextView) findViewById(R.id.title);
		titleTv.setText("我要发布");
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ImagePublishAdapter(this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (position == getDataSize()) {
					new PopupWindows(PublishActivity.this, mGridView);
				} else {
					Intent intent = new Intent(PublishActivity.this,
							ImageZoomActivity.class);
					intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
							(Serializable) mDataList);
					intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
					startActivity(intent);
				}
			}
		});
		sendTv = (TextView) findViewById(R.id.action);
		sendTv.setText("发送");
		sendTv.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(chooseType.getText().toString().trim().equals("请选择分类")){
					Toast.makeText(PublishActivity.this,"请选择分类",Toast.LENGTH_SHORT).show();
				}else{
					Log.i("type",infoType);
					if(mDataList.size() == 0 && shuodian.getText().toString().trim().equals("")){
						Toast.makeText(PublishActivity.this,"不可以发送空信息",Toast.LENGTH_SHORT).show();
					}else{
						if(mDataList.size() == 0){
							new Thread(new Runnable() {
								@Override
								public void run() {
									resultEnd = initHttp();
									if(resultEnd.contains("true")) {
										mDataList.clear();
										finish();
									}
								}
							}).start();
						}else{
							try{
								upLoadImg();
							}catch (Exception e){
								e.printStackTrace();
							}
						}

					}
				}
			}
		});
		TextView back= (TextView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeTempFromPref();
				mDataList.clear();
				SharedPreferences clearP = getSharedPreferences("talk_pref", MODE_PRIVATE);
				SharedPreferences.Editor editors = clearP.edit();
				editors.remove("talk");
				editors.commit();
				finish();
//				System.exit(0);
			}
		});
	}


	/**
	 *
	 */
	private Handler mMainHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			if(msg.arg1 == 1){
				System.out.println("chandu"+listUrl.size()+"ge");
				progressDialogHttp.setMessage("请稍后......");
				progressDialogHttp.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						resultEnd = initHttp();
						if(resultEnd.contains("true")){
							mDataList.clear();
							finish();
						}else if(resultEnd.contains("false")){

						}
						Log.i("endend",resultEnd+"end");
					}
				}).start();

			}
		};
	};


	String resultData="";
	//發送請求
	private String initHttp() {
		URL url = null;
		String  BOUNDARY =  UUID.randomUUID().toString();
		try {
			url = new URL(HttpAddress.ADDRESSHTTP+"/talk/announce.do");
//            url = new URL("http://192.168.0.101:8080/servertest/JSONServer");
			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");
			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			urlConn.setInstanceFollowRedirects(true);
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Content-type", "text/html");
			urlConn.setRequestProperty("contentType", "utf-8");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			urlConn.connect();
			//DataOutputStream流
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
			//要上传的参数
			String content = "content=" + URLEncoder.encode(shuodian.getText().toString(), "UTF-8");
			content +="&writer=" + URLEncoder.encode(telephone, "UTF-8");
			content +="&type=" + URLEncoder.encode(infoType, "UTF-8");

			String listlist = listUrl.toString();
			String imageUrl =  listlist.substring(1,listlist.length()-1);

			Log.i("listlist",imageUrl);
			content +="&pictureaddress=" +imageUrl.replace(" ","");

			//将要上传的内容写入流中
			out.writeBytes(content);
			//刷新、关闭
			out.flush();
			out.close();
			//获取数据
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine = null;
			//使用循环来读取获得的数据
			while (((inputLine = reader.readLine()) != null))
			{
				//我们在每一行后面加上一个"\n"来换行
				resultData += inputLine + "\n";
			}
			reader.close();
			//关闭http连接
			urlConn.disconnect();
			//设置显示取得的内容
			if ( resultData != null )
			{
				Log.i("upload",resultData);
			}
			else
			{
				Log.i("upload","读取的内容为NULL");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i("upload","读取的内容为"+resultData);
		return resultData;
	}


	//上传图片
	private void upLoadImg() {
		progressDialog.setMessage("正在上传......");
		progressDialog.show();
		String filkepath = null ;
		for (int i = 0; i < mDataList.size(); i++) {
			filkepath = mDataList.get(i).sourcePath;
			// 构建要上传的任务
			imgUploadTask = new PhotoUploadTask(filkepath,
					new IUploadTaskListener() {
						@Override
						public void onUploadSucceed(final FileInfo result) {
							Log.e("jia", "upload succeed: " + result.url);
							mMainHandler.post(new Runnable() {
								@Override
								public void run() {
									mCurrPhotoInfo = result;
									listUrl.add(Html.fromHtml("<u>" + result.url + "</u>") + "");
									if (listUrl.size() == mDataList.size()) {
										System.out.println("listurl------"+listUrl);
										Message msg = new Message();
										msg.arg1 = 1;
										mMainHandler.sendMessage(msg);
									}
									System.out.println("chandu"+listUrl.size()+"ge"+listUrl.get(0));
								}
							});
						}

						@Override
						public void onUploadStateChange(ITask.TaskState state) {
						}

						@Override
						public void onUploadProgress(final long totalSize, final long sendSize) {
							long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
							mMainHandler.post(new Runnable() {
								@Override
								public void run() {
									long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
									progressDialog.setMessage("上传进度: " + p + "%");
									if(p == 100){
										progressDialog.dismiss();
									}
									progressDialogHttp.setMessage("请稍后......");
								}
							});
						}

						@Override
						public void onUploadFailed(final int errorCode,
												   final String errorMsg) {
							Log.i("Demo", "上传结果:失败! ret:" + errorCode + " msg:"+ errorMsg);
							mMainHandler.post(new Runnable() {
								@Override
								public void run() {
									Log.i("上传结果:失败! ret:", errorCode + " msg:" + errorMsg);
								}
							});
						}
					});
			imgUploadTask.setBucket(bucket);
			// task.setFileId("test_fileId_"); // 为图片自定义FileID(可选)
			imgUploadTask.setAuth(sign);
			mFileUploadManager.upload(imgUploadTask); // 开始上传

		}
	}


	public class UploadFileTasks extends AsyncTask<String, Integer, String> {
		public  final String requestURL= HttpAddress.ADDRESSHTTP +"/Talk/UploadImga.do";
		/**
		 *  可变长的输入参数，与AsyncTask.exucute()对应
		 */
		private ProgressDialog pdialog;
		private Activity context=null;
		public int  successfuly = 1;


		public UploadFileTasks(Activity ctx){
			this.context=ctx;
			pdialog=ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
		}
		@Override
		protected void onPostExecute(String result) {
			try{
				// 返回HTML页面的内容
				if(UploadUtils.SUCCESS.equalsIgnoreCase(result)){
					pdialog.dismiss();
					Toast.makeText(context, "上传成功!", Toast.LENGTH_LONG).show();
					PublishActivity.mDataList.clear();
					context.finish();
				}else{
					pdialog.dismiss();
					Toast.makeText(context, "上传失败!",Toast.LENGTH_LONG ).show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		@Override
		protected void onPreExecute() {
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		@Override
		protected String doInBackground(String... params) {
			File file=new File(params[0]);
			int i=0;
			for (i=10; i<=100; i+=10) {
				//发布一个更新,调用这个方法的同时 执行 onProgressUpdate
				publishProgress(i);
			}
			UploadUtils  uu =new UploadUtils(context);
			return uu.uploadFile(file, requestURL,uuid);
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			int vlaue = values[0];
			Log.i("xx",values[0]+"");
		}



	}

	private int getDataSize()
	{
		return mDataList == null ? 0 : mDataList.size();
	}

	private int getAvailableSize()
	{
		int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
		if (availSize >= 0)
		{
			return availSize;
		}
		return 0;
	}

	public String getString(String s)
	{
		String path = null;
		if (s == null) return "";
		for (int i = s.length() - 1; i > 0; i++)
		{
			s.charAt(i);
		}
		return path;
	}

	public class PopupWindows extends PopupWindow
	{

		public PopupWindows(Context mContext, View parent)
		{

			View view = View.inflate(mContext, R.layout.item_popupwindow, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					takePhoto();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Intent intent = new Intent(PublishActivity.this,
							ImageBucketChooseActivity.class);
					intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
							getAvailableSize());
					startActivity(intent);
					dismiss();
					finish();
				}
			});
			bt3.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void takePhoto()
	{
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		File vFile = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		if (!vFile.exists())
		{
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		}
		else
		{
			if (vFile.exists())
			{
				vFile.delete();
			}
		}
		path = vFile.getPath();
		Uri cameraUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case TAKE_PICTURE:
			if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
					&& resultCode == -1 && !TextUtils.isEmpty(path))
			{
				ImageItem item = new ImageItem();
				item.sourcePath = path;
				mDataList.add(item);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		mDataList.clear();
		super.onDestroy();
	}

	private void notifyDataChanged()
	{
		mAdapter.notifyDataSetChanged();
	}

}
