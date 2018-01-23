package com.shows.allactivty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.com.shows.utils.CacheUtils;
import com.com.shows.utils.ChoosePic;
import com.com.shows.utils.Utilty;
import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeWeatherActivity extends BaseActivity {

    TextView mTextView;
    Button test;
    Context con;
    private JSONObject json;

    LinearLayout l_temp;
    private double wdmax = 50.0;
    private double wdmin = 0.0;
    private static int width, height;


    String[] titles;
    List<double[]> x;
    List<double[]> values;

    TextView t_wea_index_gm,t_wea_index_xc,t_wea_index_ly,t_wea_index_cy,t_wea_index_ls,t_wea_index_zwx;//指数
    TextView t_current_temp,t_current_hum,t_current_wind,t_current_detail,t_current_aqi,t_current_date,t_current_week;//当前的数据
    TextView t_week_01,t_week_02,t_week_03,t_week_04,t_week_05,t_week_06;//星期的转换
    TextView i_current_icon;//中间图标
    ChoosePic choose = new ChoosePic();
    TextView t_detail_01,t_detail_02,t_detail_03,t_detail_04,t_detail_05,t_detail_06;//一周天气
    TextView t_temp_01,t_temp_02,t_temp_03,t_temp_04,t_temp_05,t_temp_06;//一周温度
    ImageView i_icon_01,i_icon_02,i_icon_03,i_icon_04,i_icon_05,i_icon_06;
    List<Integer> yvalues,yvaluesmin;

    private  LinearLayout linearBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.we_weather_acitivty);
        ApiStoreSDK.init(getApplicationContext(), "01cacd48ac45b99dc89b10d12cf815ef");
        initView();
        l_temp = (LinearLayout) findViewById(R.id.wea_chart);
        getwAndh();
        apiTest();
        getWeek();
        String cache = CacheUtils.getCache("http://apis.baidu.com/heweather/weather/free",
                WeWeatherActivity.this);
        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
            viewSetText(cache);
        }

    }

    /**
     * 初始化
     */
    private void initView() {
        linearBack  = (LinearLayout) findViewById(R.id.linback);
        //指数
        t_wea_index_gm = (TextView) findViewById(R.id.index_ganmao);
        t_wea_index_xc = (TextView) findViewById(R.id.index_xiche);
        t_wea_index_ly = (TextView) findViewById(R.id.index_lvyou);
        t_wea_index_cy = (TextView) findViewById(R.id.index_chuanyi);
        t_wea_index_ls = (TextView) findViewById(R.id.index_liangshai);
        t_wea_index_zwx = (TextView) findViewById(R.id.index_ziwaixian);
        //当前预报
        t_current_temp = (TextView) findViewById(R.id.wea_indo_temp_current);
        t_current_hum =(TextView) findViewById(R.id.wea_info_shidu_current);
        t_current_wind = (TextView) findViewById(R.id.wea_info_fengx_current);
        t_current_detail = (TextView) findViewById(R.id.wea_info_detail_current);
        t_current_aqi = (TextView) findViewById(R.id.wea_info_aqi_current);
        t_current_date = (TextView) findViewById(R.id.wea_datatime_current);
        t_current_week = (TextView) findViewById(R.id.wea_week_current);
        //一周周数
        t_week_01 = (TextView) findViewById(R.id.wea_week_01);
        t_week_02 = (TextView) findViewById(R.id.wea_week_02);
        t_week_03 = (TextView) findViewById(R.id.wea_week_03);
        t_week_04 = (TextView) findViewById(R.id.wea_week_04);
        t_week_05 = (TextView) findViewById(R.id.wea_week_05);
        t_week_06 = (TextView) findViewById(R.id.wea_week_06);
        i_current_icon = (TextView) findViewById(R.id.wea_info_icon_current);
        //一周天气
        t_detail_01 = (TextView) findViewById(R.id.wea_detail_01);
        t_detail_02 = (TextView) findViewById(R.id.wea_detail_02);
        t_detail_03 = (TextView) findViewById(R.id.wea_detail_03);
        t_detail_04 = (TextView) findViewById(R.id.wea_detail_04);
        t_detail_05 = (TextView) findViewById(R.id.wea_detail_05);
        t_detail_06 = (TextView) findViewById(R.id.wea_detail_06);
        //一周温度
        t_temp_01 = (TextView) findViewById(R.id.wea_temp_01);
        t_temp_02 = (TextView) findViewById(R.id.wea_temp_02);
        t_temp_03 = (TextView) findViewById(R.id.wea_temp_03);
        t_temp_04 = (TextView) findViewById(R.id.wea_temp_04);
        t_temp_05 = (TextView) findViewById(R.id.wea_temp_05);
        t_temp_06 = (TextView) findViewById(R.id.wea_temp_06);
        //一周天气图标
        i_icon_01 = (ImageView) findViewById(R.id.wea_icon_01);
        i_icon_02 = (ImageView) findViewById(R.id.wea_icon_02);
        i_icon_03 = (ImageView) findViewById(R.id.wea_icon_03);
        i_icon_04 = (ImageView) findViewById(R.id.wea_icon_04);
        i_icon_05 = (ImageView) findViewById(R.id.wea_icon_05);
        i_icon_06 = (ImageView) findViewById(R.id.wea_icon_06);

    }

    public void drawLines() {
        setTitles();
        setXvalue();
        setText();
        setPointstytle();
    }

    private void setPointstytle() {
        // TODO Auto-generated method stub
        int[] colors = new int[] {getResources().getColor(R.color.line_temp_high), Color.YELLOW };
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,PointStyle.DIAMOND };

        XYMultipleSeriesRenderer r=new XYMultipleSeriesRenderer();

        XYMultipleSeriesRenderer renderer = buildRenderer(r,colors, styles);

        setChartSettings(renderer, "", "", "", 0.8, 7, wdmin, wdmax,Color.WHITE, Color.WHITE);

        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);

        showChart(renderer, dataset);

    }

    private void showChart(XYMultipleSeriesRenderer renderer,XYMultipleSeriesDataset dataset) {
        GraphicalView v = ChartFactory.getLineChartView(this, dataset, renderer);
        v.setBackgroundColor(Color.TRANSPARENT);
        l_temp.addView(v);
    }

    private void setText() {
        // TODO Auto-generated method stub
        values = new ArrayList<double[]>();
        int max ;
        for (int i =0;i<yvalues.size();i++){
           Log.i("......",""+yvalues.get(i)+";;;"+yvaluesmin.get(i));
        }
        values.add(new double[] { yvalues.get(0), yvalues.get(1), yvalues.get(2), yvalues.get(3), yvalues.get(4), yvalues.get(5), yvalues.get(6) });
        values.add(new double[] { yvaluesmin.get(0), yvaluesmin.get(1), yvaluesmin.get(2), yvaluesmin.get(3), yvaluesmin.get(4), yvaluesmin.get(5), yvaluesmin.get(6) });
    }

    private void setXvalue() {
        x = new ArrayList<double[]>();
        for (int i = 0; i < titles.length; i++) {
            x.add(new double[] { 1, 2, 3, 4, 5, 6, 7 });
        }
    }

    private void setTitles() {
        titles = new String[] { "高温", "低温" };
    }

    /**
     * Builds an XY multiple series renderer.
     *
     * @param colors
     *            the series rendering colors
     * @param styles
     *            the series point styles
     * @return the XY multiple series renderers
     */
    protected XYMultipleSeriesRenderer buildRenderer(XYMultipleSeriesRenderer renderer,int[] colors,
                                                     PointStyle[] styles) {
        setRenderer(renderer, colors, styles);
        return renderer;
    }

    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
                               PointStyle[] styles) {

        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer xys = new XYSeriesRenderer();
            renderer.addSeriesRenderer(xys);
            xys.setColor(getResources().getColor(R.color.line_temp_high));// ������ɫ
            xys.setPointStyle(PointStyle.CIRCLE);// ���õ����ʽ
            xys.setFillPoints(true);// ���㣨��ʾ�ĵ��ǿ��Ļ���ʵ�ģ�
            xys.setLineWidth(3);// �����߿�
            xys.setDisplayChartValues(true);

            if (i==1) {
                xys.setChartValuesSpacing(-30);// ��ʾ�ĵ��ֵ��ͼ�ľ���
            }
            if (width>480) {
                xys.setChartValuesTextSize(25);
                xys.setDisplayChartValuesDistance(55);
            }else {
                xys.setChartValuesTextSize(15);
                xys.setDisplayChartValuesDistance(35);
            }
            xys.setColor(colors[i]);
            xys.setPointStyle(styles[i]);

        }

    }

    /**
     * Sets a few of the series renderer settings.
     *
     * @param renderer
     *            the renderer to set the properties to
     * @param title
     *            the chart title
     * @param xTitle
     *            the title for the X axis
     * @param yTitle
     *            the title for the Y axis
     * @param xMin
     *            the minimum value on the X axis
     * @param xMax
     *            the maximum value on the X axis
     * @param yMin
     *            the minimum value on the Y axis
     * @param yMax
     *            the maximum value on the Y axis
     * @param axesColor
     *            the axes color
     * @param labelsColor
     *            the labels color
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String title, String xTitle, String yTitle, double xMin,
                                    double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);// ����y��Сֵ
        renderer.setYAxisMax(yMax);// ����y���ֵ
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));// ����4������͸��
        renderer.setAxesColor(Color.argb(0x00, 0x00, 0x00, 0x01)); // ����������ɫ
        renderer.setXLabelsColor(Color.argb(0x00, 0x00, 0x00, 0x01));// ����������ɫ
        renderer.setYLabelsColor(0, Color.rgb(255, 255, 255));// ������ɫ
        renderer.setLabelsColor(Color.rgb(255, 255, 255));// ������˵��������ɫ
        renderer.setPanEnabled(false, false); // �����Ƿ��ǿɻ��������� �϶�
        renderer.setShowGridX(true);// ��ʾ���
        renderer.setShowGridY(false);
        renderer.setShowAxes(false);
        renderer.setYLabels(4);// y��̶�
        renderer.setXLabels(0);// ����X����ʾ�Ŀ̶ȱ�ǩ�ĸ���
        renderer.setLegendHeight(150);


        if (width>480) {
            renderer.setAxisTitleTextSize(30);
            renderer.setChartTitleTextSize(35);
            renderer.setLabelsTextSize(35);// y fontsize
            renderer.setLegendTextSize(35);
            renderer.setPointSize(5f);// ���õ�Ĵ�С
            renderer.setMargins(new int[] { 15, 15, 15, 15 });
        }else{
            renderer.setLabelsTextSize(15);// ����������ִ�С
            renderer.setLegendTextSize(18);//����ͼ���ı���С
            renderer.setLegendTextSize(25);
            renderer.setPointSize(5f);// ���õ�Ĵ�С
            renderer.setMargins(new int[] { 15, 15, 15, 15 });
        }
    }

    /**
     * Builds an XY multiple dataset using the provided values.
     *
     * @param titles
     *            the series titles
     * @param xValues
     *            the values for the X axis
     * @param yValues
     *            the values for the Y axis
     * @return the XY multiple dataset
     */
    protected XYMultipleSeriesDataset buildDataset(String[] titles,
                                                   List<double[]> xValues, List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeries(dataset, titles, xValues, yValues, 0);
        return dataset;
    }

    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
                            List<double[]> xValues, List<double[]> yValues, int scale) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i], scale);

            double[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
                if (yV[k] > wdmax) {
                    wdmax = yV[k];
                }
                if (yV[k] < wdmin) {
                    wdmin = yV[k];
                }
                // series.addAnnotation((int)yV[k]+"��", xV[k], yV[k]);
            }

            dataset.addSeries(series);
        }
        double key = (wdmax - wdmin);
        System.out.println("wdmax-wdmin=" + key);
        if (1<key&&key < 5) {
            wdmax = wdmax + 5;
            wdmin = wdmin - 5;
        } else if (key >= 5 && key <= 10) {
            wdmax = wdmax + 10;
            wdmin = wdmin - 5;
        } else if (key > 10 && key <= 20) {
            wdmax = wdmax + 15;
            wdmin = wdmin - 5;
        }
        System.out.println("wdmax=" + wdmax + "wdmin=" + wdmin);
    }
    private void getwAndh() {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }



    private void viewSetText(String respon){
        try {
            json = new JSONObject(respon);
            JSONArray jsonArray1  =  json.getJSONArray("HeWeather data service 3.0");

            JSONArray jsonArrayWeek,jsonArrayWeek2,jsonArrayWeek3,jsonArrayWeek4,jsonArrayWeek5,jsonArrayWeek6;
            JSONObject city = jsonArray1.getJSONObject(0).getJSONObject("aqi").getJSONObject("city");//当前城市
            JSONArray jsonArray2  =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            JSONObject wind = jsonArray2.getJSONObject(0).getJSONObject("wind");//风向+sc
            JSONObject cond = jsonArray2.getJSONObject(0).getJSONObject("cond");//天气情况和图标的获取对象
            //获取一周最高最低温
            yvalues = new ArrayList<>();
            yvaluesmin = new ArrayList<>();
            for (int i=0;i< 7;i++){
                JSONArray jsonArraytemp =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
                Integer temps =Integer.parseInt(jsonArraytemp.getJSONObject(i).getJSONObject("tmp").getString("max"));
                yvalues.add(temps);
                Integer tempmin =Integer.parseInt(jsonArraytemp.getJSONObject(i).getJSONObject("tmp").getString("min"));
                Log.i(">>>>>",""+temps);
                Log.i(">>>>",""+temps);
                yvaluesmin.add(tempmin);
            }
            Log.i(">>>",yvalues.size()+"-----"+yvaluesmin.size());
            //一周天气
            jsonArrayWeek =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            jsonArrayWeek2 =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            jsonArrayWeek3 =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            jsonArrayWeek4 =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            jsonArrayWeek5 =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            jsonArrayWeek6 =  jsonArray1.getJSONObject(0).getJSONArray("daily_forecast");//
            t_detail_01.setText(jsonArrayWeek.getJSONObject(1).getJSONObject("cond").getString("txt_d"));
            t_detail_02.setText(jsonArrayWeek.getJSONObject(2).getJSONObject("cond").getString("txt_d"));
            t_detail_03.setText(jsonArrayWeek.getJSONObject(3).getJSONObject("cond").getString("txt_d"));
            t_detail_04.setText(jsonArrayWeek.getJSONObject(4).getJSONObject("cond").getString("txt_d"));
            t_detail_05.setText(jsonArrayWeek.getJSONObject(5).getJSONObject("cond").getString("txt_d"));
            t_detail_06.setText(jsonArrayWeek.getJSONObject(6).getJSONObject("cond").getString("txt_d"));
            t_temp_01.setText(jsonArrayWeek.getJSONObject(1).getJSONObject("tmp").getString("max")+"℃");
            t_temp_02.setText(jsonArrayWeek.getJSONObject(2).getJSONObject("tmp").getString("max")+"℃");
            t_temp_03.setText(jsonArrayWeek.getJSONObject(3).getJSONObject("tmp").getString("max")+"℃");
            t_temp_04.setText(jsonArrayWeek.getJSONObject(4).getJSONObject("tmp").getString("max")+"℃");
            t_temp_05.setText(jsonArrayWeek.getJSONObject(5).getJSONObject("tmp").getString("max")+"℃");
            t_temp_06.setText(jsonArrayWeek.getJSONObject(6).getJSONObject("tmp").getString("max")+"℃");
            i_icon_01.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(1).getJSONObject("cond").getString("code_d"))));
            i_icon_02.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(2).getJSONObject("cond").getString("code_d"))));
            i_icon_03.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(3).getJSONObject("cond").getString("code_d"))));
            i_icon_04.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(4).getJSONObject("cond").getString("code_d"))));
            i_icon_05.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(5).getJSONObject("cond").getString("code_d"))));
            i_icon_06.setImageResource(choose.parseDayIcon(Integer.parseInt(jsonArrayWeek.getJSONObject(6).getJSONObject("cond").getString("code_d"))));
            //start当前的天气预报
            Log.i("mmeda",jsonArray2.getJSONObject(0).getString("hum")+jsonArray2.getJSONObject(0).getJSONObject("tmp").getString("max"));//湿度
            String temp = jsonArray2.getJSONObject(0).getJSONObject("tmp").getString("max")+"℃";//温度
            i_current_icon.setBackgroundResource(choose.parseDayIcon(Integer.parseInt(cond.getString("code_d"))));
            t_current_date.setText(jsonArray2.getJSONObject(0).getString("date"));
            t_current_temp.setText(temp);
            t_current_detail.setText(cond.getString("txt_d"));
            t_current_aqi.setText("空气质量  "+city.getString("qlty"));
            t_current_wind.setText("风向  "+wind.getString("dir")+"  "+wind.getString("sc"));
            t_current_hum.setText("湿度  "+jsonArray2.getJSONObject(0).getString("hum")+"%");

            if(!cond.getString("txt_d").contains("晴")){
                linearBack.setBackgroundResource(R.mipmap.yinbeijing);
            }else{
                linearBack.setBackgroundResource(R.mipmap.qingbeijing);
            }


            //end当前的信息
            //satrt指数的定义赋值
            JSONObject shushidu = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("comf");
            JSONObject xiche = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("cw");
            JSONObject chuanyi = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("drsg");
            JSONObject ganmao = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("flu");
            JSONObject yundong = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("sport");
            JSONObject lvyou = jsonArray1.getJSONObject(0).getJSONObject("suggestion").getJSONObject("trav");
            Log.i("mmedas",shushidu.getString("brf")+">>"+xiche.getString("brf"));
            t_wea_index_gm.setText(shushidu.getString("brf"));
            t_wea_index_xc.setText(xiche.getString("brf"));
            t_wea_index_ls.setText(yundong.getString("brf"));
            t_wea_index_ly.setText(lvyou.getString("brf"));
            t_wea_index_cy.setText(chuanyi.getString("brf"));
            t_wea_index_zwx.setText(shushidu.getString("brf"));
            //end指数的定义赋值

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 更改星期几的显示
    public String getWeek() {
        String Week = "周";
        String netweek1 = "周一";
        String netweek2 = "周二";
        String netweek3 = "周三";
        String netweek4 = "周四";
        String netweek5 = "周五";
        String netweek6 = "周六";
        String netweek7 = "周日";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 也可将此值当参数传进来
        Date curDate = new Date(System.currentTimeMillis());
        String pTime = format.format(curDate);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("<<<<<",""+c.get(Calendar.DAY_OF_WEEK)+pTime);
        switch (c.get(Calendar.DAY_OF_WEEK)) {

            case 1:
                Week += "天";
                t_current_week.setText(Week);
                t_week_01.setText(netweek1);
                t_week_02.setText(netweek2);
                t_week_03.setText(netweek3);
                t_week_04.setText(netweek4);
                t_week_05.setText(netweek5);
                t_week_06.setText(netweek6);
                break;
            case 2:
                Week += "一";
                t_current_week.setText(Week);
                t_week_01.setText(netweek2);
                t_week_02.setText(netweek3);
                t_week_03.setText(netweek4);
                t_week_04.setText(netweek5);
                t_week_05.setText(netweek6);
                t_week_06.setText(netweek7);
                break;
            case 3:
                Week += "二";
                t_current_week.setText(Week);
                t_week_01.setText(netweek3);
                t_week_02.setText(netweek4);
                t_week_03.setText(netweek5);
                t_week_04.setText(netweek6);
                t_week_05.setText(netweek7);
                t_week_06.setText(netweek1);
                break;
            case 4:
                Week += "三";
                t_current_week.setText(Week);
                t_week_01.setText(netweek4);
                t_week_02.setText(netweek5);
                t_week_03.setText(netweek6);
                t_week_04.setText(netweek7);
                t_week_05.setText(netweek1);
                t_week_06.setText(netweek2);
                break;
            case 5:
                Week += "四";
                t_current_week.setText(Week);
                t_week_01.setText(netweek5);
                t_week_02.setText(netweek6);
                t_week_03.setText(netweek7);
                t_week_04.setText(netweek1);
                t_week_05.setText(netweek2);
                t_week_06.setText(netweek3);
                break;
            case 6:
                Week += "五";
                t_current_week.setText(Week);
                t_week_01.setText(netweek6);
                t_week_02.setText(netweek7);
                t_week_03.setText(netweek1);
                t_week_04.setText(netweek2);
                t_week_05.setText(netweek3);
                t_week_06.setText(netweek4);
                break;
            case 7:
                Week += "六";
                t_current_week.setText(Week);
                t_week_01.setText(netweek7);
                t_week_02.setText(netweek1);
                t_week_03.setText(netweek2);
                t_week_04.setText(netweek3);
                t_week_05.setText(netweek4);
                t_week_06.setText(netweek5);
                break;
            default:
                break;
        }
        return Week;
    }

    /**
     *
     */
    private void apiTest() {

        Parameters para = new Parameters();

        para.put("city", "lanzhou");
        ApiStoreSDK.execute("http://apis.baidu.com/heweather/weather/free",
                ApiStoreSDK.GET,
                para,
                new ApiCallBack() {

                    @Override
                    public void onSuccess(int status, String responseString) {
                        Log.i("sdkdemo", "onSuccess");
                        Log.i("memda",responseString);
                        viewSetText(responseString);
                        try {
                            CacheUtils.setCache("http://apis.baidu.com/heweather/weather/free",
                                    responseString, WeWeatherActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        drawLines();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("sdkdemo", "onComplete");
                    }

                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        Log.i("sdkdemo", "onError, status: " + status);
                        Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                    }

                });

    }

    String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(e.getMessage()).append("\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            str.append(e.getStackTrace()[i]).append("\n");
        }
        return str.toString();
    }
}
