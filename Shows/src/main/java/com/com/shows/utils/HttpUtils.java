package com.com.shows.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by Administrator on 2016/5/3 0003.
 */
public class HttpUtils {

    static String inputLine = "";
    public static String  getHttp(String reqUrl) {
        try {
            URL localURL =  new URL(reqUrl);
            StringBuffer resultBuffer = new StringBuffer();
            URLConnection connection = localURL.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            InputStream inputStream = httpURLConnection.getInputStream();;
            InputStreamReader inputStreamReader = = new InputStreamReader(inputStream);;
            BufferedReader reader =  new BufferedReader(inputStreamReader);;
                while ((inputLine = reader.readLine()) != null) {
                    resultBuffer.append(inputLine);
                    Log.i("TESTTEST", resultBuffer.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                reader.close();}
            if (inputStreamReader != null) {
                inputStreamReader.close();}
            if (inputStream != null) {
                inputStream.close();}
        }
        return resultBuffer.toString();
    }


    //POST请求
    public static String postHttp(String requestUrl,String args) {
        String resultData="";
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
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
            String content = "tel=" + URLEncoder.encode(args, "UTF-8");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("gongyongda", resultData);
            } else {
                Log.i("gongyongda", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }

    //POST请求
    public static String postHttp2(String requestUrl,String argname,String args) {
        String resultData="";
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
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
            String content = argname+"=" + URLEncoder.encode(args, "UTF-8");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("posttt", resultData);
            } else {
                Log.i("posttt", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }


    //POST请求
    public static String postHttpMoney(String requestUrl,String args,String type) {
        String resultData="";
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
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
            String content = "tel=" + URLEncoder.encode(args, "UTF-8");
            content += "&type=" + URLEncoder.encode(type, "UTF-8");
            //将要上传的内容写入流中
            out.writeBytes(content);
            //刷新、关闭
            out.flush();
            out.close();
            //获取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine = null;
            //使用循环来读取获得的数据
            while (((inputLine = reader.readLine()) != null)) {
                //我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("gongyongda", resultData);
            } else {
                Log.i("gongyongda", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }

}
