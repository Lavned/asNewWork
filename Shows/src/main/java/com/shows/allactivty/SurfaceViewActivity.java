
package com.shows.allactivty;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.com.shows.utils.DataCleanManager;
import com.com.shows.utils.HttpAddress;
import com.com.shows.utils.VideoConstants;
import com.shows.view.BaseActivity;
import com.yixinke.shows.R;

public class SurfaceViewActivity extends BaseActivity implements OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, OnBufferingUpdateListener,
        OnClickListener {

    /**
     *
     */
    private SurfaceView surfaceView;

    /**
     * surfaceView播放控制
     */
    private SurfaceHolder surfaceHolder;

    /**
     * 播放控制条
     */
    private SeekBar seekBar;

    /**
     * 暂停播放按钮
     */
    private Button playButton;

    /**
     * 重新播放按钮
     */
    private Button replayButton;

    /**
     * 截图按钮
     */
    private Button screenShotButton;

    /**
     * 改变视频大小button
     */
    private Button videoSizeButton;

    /**
     * 加载进度显示条
     */
    private ProgressBar progressBar;

    /**
     * 播放视频
     */
    private MediaPlayer mediaPlayer;

    /**
     * 记录当前播放的位置
     */
    private int playPosition = -1;

    /**
     * seekBar是否自动拖动
     */
    private boolean seekBarAutoFlag = false;

    /**
     * 视频时间显示
     */
    private TextView vedioTiemTextView;

    /**
     * 播放总时间
     */
    private String videoTimeString;

    private long videoTimeLong;

    /**
     * 播放路径
     */
    private String pathString;

    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;
    private String videourl,aid;
    private ProgressDialog progressDialog;
    private SharedPreferences mPreferences;
    private String phone,cid;
    private GetHandler getHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        mPreferences =getSharedPreferences("uid_pref", MODE_PRIVATE);
        cid = mPreferences.getString("uid", "");
        phone = mPreferences.getString("telephone", "");
        Log.i("uid", cid+".."+phone);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        progressDialog.show();
        getHandler =new GetHandler();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videourl =bundle.getString("videourl");
        aid= bundle.getString("aid");
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        initViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                screenHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(layoutParams);
        videoSizeButton.setText("窗口");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    public void initViews() {
        String path = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 存在获取外部文件路径
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            // 不存在获取内部存储
            path = Environment.getDataDirectory().getPath();
        }
        pathString = path + "/shen/x0200hkt1cg.p202.1.mp4";
        // 初始化控件

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        playButton = (Button) findViewById(R.id.button_play);
        replayButton = (Button) findViewById(R.id.button_replay);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);
        screenShotButton = (Button) findViewById(R.id.button_screenShot);
        videoSizeButton = (Button) findViewById(R.id.button_videoSize);
        // 设置surfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置surface回调
        surfaceHolder.addCallback(new SurfaceCallback());

    }

    // SurfaceView的callBack
    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建
            // 设置播放资源
            playVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }

    }


    /**
     * 请求网络数据
     */
    String resultData="";
    private String getMoneyForVideo() {
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString();
        try {
            url = new URL(HttpAddress.ADDRESSHTTP+"/mrecord/videoAdvByGetMoney.do");
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
            String content = "tel=" + URLEncoder.encode(phone, "UTF-8");
            content += "&aid=" + URLEncoder.encode(aid, "UTF-8");
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
                Log.i("hhh",resultData);
            }
            reader.close();
            //关闭http连接
            urlConn.disconnect();
            //设置显示取得的内容
            if (resultData != null) {
                Log.i("hhhh", resultData);
            } else {
                Log.i("hhh", "读取的内容为NULL");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  resultData;
    }

    /**
     * 异步更新UI
     */
    class GetHandler extends Handler {
        public GetHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            Log.i("mymymy", "mymymy......");
            try{
                if(msg.arg1 ==1){
                    // 此处可以更新UI
                    Toast.makeText(SurfaceViewActivity.this,"成功领取0.1元",Toast.LENGTH_SHORT).show();
                    finish();
                }else if(msg.arg1 ==2 ){
                    Toast.makeText(SurfaceViewActivity.this,"你已经领过了",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    /**
     * 播放视频
     */
    public void playVideo() {
        // 初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
                new AlertDialog.Builder(SurfaceViewActivity.this)
                        .setMessage("领取该视频奖励")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String moneyState = getMoneyForVideo();
                                        Message msg = new Message();
                                        if (moneyState.contains("true")){
                                            msg.arg1 = 1;
                                        }else{
                                            msg.arg1 = 2;
                                        }
                                        getHandler.sendMessage(msg);
                                    }
                                }).start();
                            }
                        })
                        .show();
			}
		});
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
        Uri uri = Uri.parse(videourl);
        
        
        try {
//        	mediaPlayer.prepare();
//             mediaPlayer.start();
//            mediaPlayer.se/tDataSource(pathString);
             mediaPlayer.setDataSource(this, uri);
//             mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
        }

    
    }

    /**
     * 视频加载完毕监听
     * 
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // 当视频加载完毕以后，隐藏加载进度条
        progressBar.setVisibility(View.GONE);
        progressDialog.dismiss();
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (VideoConstants.playPosition >= 0) {
            mediaPlayer.seekTo(VideoConstants.playPosition);
            VideoConstants.playPosition = -1;
            // surfaceHolder.unlockCanvasAndPost(VideoConstants.getCanvas());
        }
        seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax(mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        // 设置拖动监听事件
//        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        // 设置按钮监听事件
        // 重新播放
        replayButton.setOnClickListener(SurfaceViewActivity.this);
        // 暂停和播放
        playButton.setOnClickListener(SurfaceViewActivity.this);
        // 截图按钮
        screenShotButton.setOnClickListener(SurfaceViewActivity.this);
        // 视频大小
        videoSizeButton.setOnClickListener(SurfaceViewActivity.this);
        // 播放视频
        mediaPlayer.start();
        // 设置显示到屏幕
        mediaPlayer.setDisplay(surfaceHolder);
        // 开启线程 刷新进度条
        new Thread(runnable).start();
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }

    /**
     * 滑动条变化线程
     */
    private Runnable runnable = new Runnable() {

        public void run() {
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */

                    if (null != SurfaceViewActivity.this.mediaPlayer
                            && SurfaceViewActivity.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * seekBar拖动监听类
     * 
     * @author shenxiaolei
     */
    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {


        }

    }

    /**
     * 按钮点击事件监听
     */
    public void onClick(View v) {
        // 重新播放
        if (v == replayButton) {
            // mediaPlayer不空，则直接跳转
            if (null != mediaPlayer) {
                // MediaPlayer和进度条都跳转到开始位置
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                // 如果不处于播放状态，则开始播放
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else {
                // 为空则重新设置mediaPlayer
                playVideo();
            }

        }
        // 播放、暂停按钮
        if (v == playButton) {
            if (null != mediaPlayer) {
                // 正在播放
                if (mediaPlayer.isPlaying()) {
                    VideoConstants.playPosition = mediaPlayer.getCurrentPosition();
                    // seekBarAutoFlag = false;
                    mediaPlayer.pause();
                    playButton.setText("播放");
                } else {
                    if (VideoConstants.playPosition >= 0) {
                        // seekBarAutoFlag = true;
                        mediaPlayer.seekTo(VideoConstants.playPosition);
                        mediaPlayer.start();
                        playButton.setText("暂停");
                        VideoConstants.playPosition = -1;
                    }
                }

            }
        }

        if (v == videoSizeButton) {
            // 调用改变大小的方法
            changeVideoSize();
        }
    }

    /**
     * 播放完毕监听
     * 
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 设置seeKbar跳转到最后位置
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置播放标记为false
        seekBarAutoFlag = false;
    }

    /**
     * 视频缓存大小监听,当视频播放以后 在started状态会调用
     */
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
        Log.e("text", "onBufferingUpdate-->" + percent);
        // 可以根据大小的变化来
    }

    /**
     * 错误监听
     * 
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        super.onResume();
        // 判断播放位置
        if (VideoConstants.playPosition >= 0) {

            if (null != mediaPlayer) {
                seekBarAutoFlag = true;
                mediaPlayer.seekTo(VideoConstants.playPosition);
                mediaPlayer.start();
            } else {
                playVideo();
            }

        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                VideoConstants.playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                seekBarAutoFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生屏幕旋转时调用
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            VideoConstants.playPosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * 屏幕旋转完成时调用
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
}
    /**
     * 屏幕销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
        try {
            if (null != SurfaceViewActivity.this.mediaPlayer) {
                // 提前标志为false,防止在视频停止时，线程仍在运行。
                seekBarAutoFlag = false;
                // 如果正在播放，则停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                VideoConstants.playPosition = -1;
                // 释放mediaPlayer
                SurfaceViewActivity.this.mediaPlayer.release();
                SurfaceViewActivity.this.mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换播放时间
     * 
     * @param milliseconds 传入毫秒值
     * @return 返回 hh:mm:ss或mm:ss格式的数据
     */
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 改变视频的显示大小，全屏，窗口，内容
     */
    public void changeVideoSize() {
        // 改变视频大小
        String videoSizeString = videoSizeButton.getText().toString();
        // 获取视频的宽度和高度
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        // 如果按钮文字为窗口则设置为窗口模式
        if ("窗口".equals(videoSizeString)) {
            /*
             * 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
             * 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
             */
            if (width > screenWidth || height > screenHeight) {
                // 计算出宽高的倍数
                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;
                // 获取最大的倍数值，按大数值进行缩放
                float max = Math.max(vWidth, vHeight);
                // 计算出缩放大小,取接近的正值
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("全屏");
        } else if ("全屏".equals(videoSizeString)) {
            // 设置全屏
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                    screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("窗口");
        }
    }
}
