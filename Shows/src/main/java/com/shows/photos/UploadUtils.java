package com.shows.photos;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.UploadUtil;

/**
 * 
 * 实现文件上传的工具类
 * @Title: 
 * @Description: 实现TODO
 * @Copyright:Copyright (c) 2011
 * @Date:2012-7-2
 * @version 1.0
 */
public class UploadUtils {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 10000000;   //超时时间
	private static final String CHARSET = "utf-8"; //设置编码
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";


	Context context;
	String uuid = UUID.randomUUID().toString();


	public UploadUtils(Context context){
		this.context = context;
//		SharedPreferences uuid_preferences = context.getSharedPreferences(
//				"uuid_pref", Context.MODE_PRIVATE);
//		SharedPreferences.Editor uuid_editor = uuid_preferences.edit();
//		uuid_editor.putString("uuid", uuid);
//		uuid_editor.commit();
	}





	static  String resultData="";

	/**
	 * 上传信息到服务器
	 */
	public  void uploadtext(String name,String type,String write,String uuid) {
		SharedPreferences pPreferences;

		URL url = null;
		String  BOUNDARY =  UUID.randomUUID().toString();
		try {
			url = new URL(HttpAddress.ADDRESSHTTP+"/Talk/UploadText.do");
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
			String content = "content=" + URLEncoder.encode(name, "UTF-8");
			content +="&writer=" + URLEncoder.encode(write, "UTF-8");
			content +="&type=" + URLEncoder.encode(type, "UTF-8");
//			pPreferences = context.getSharedPreferences("uuid_pref", context.MODE_PRIVATE);
			Log.i("uuuu2",uuid);
			content +="&uuid=" + URLEncoder.encode(uuid, "UTF-8");
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
				Log.i("ceshi",resultData);
			}
			else
			{
				Log.i("ceshi","读取的内容为NULL");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





	/**
	 * android上传文件到服务器
	 * @param file  需要上传的文件
	 * @param RequestURL  请求的rul
	 * @return  返回响应的内容
	 */
	public  String uploadFile(File file,String RequestURL,String uudi)
	{
//		String uuid = UUID.randomUUID().toString();
		Log.i("uid",uuid);
		String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		String PREFIX = "--" , LINE_END = "\r\n"; 
		String CONTENT_TYPE = "multipart/form-data";   //内容类型
		
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);  //允许输入流
			conn.setDoOutput(true); //允许输出流
			conn.setUseCaches(false);  //不允许使用缓存
			conn.setRequestMethod("POST");  //请求方式
			conn.setRequestProperty("Charset", CHARSET);  //设置编码
			conn.setRequestProperty("connection", "keep-alive");   
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			if(file!=null)
			{
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				OutputStream outputSteam=conn.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意：
				 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的   比如:abc.png  
				 */
				Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 可以方便地修改日期格式

				String hehe = dateFormat.format(now);
				String name = file.getName();
				Log.i(">>>>>",PublishActivity.mDataList.size()+"ge");
				name=hehe+".jpg";
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+uudi+"_"+name+"\""+LINE_END);
				Log.i("uuuu1",uudi+"_"+name);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while((len=is.read(bytes))!=-1)
				{
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码  200=成功
				 * 当响应成功，获取响应的流  
				 */
				int res = conn.getResponseCode();  
				Log.e(TAG, "response code:"+res);
				if(res==200)
				{
			     return SUCCESS;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}

}