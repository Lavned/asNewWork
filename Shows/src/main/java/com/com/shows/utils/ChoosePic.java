package com.com.shows.utils;

import com.yixinke.shows.R;

/**
 * Created by Administrator on 2016/4/9 0009.
 */
public class ChoosePic {
    static int dayIconstr;

    public int parseDayIcon(int dayIcon)
    {
        if (100==dayIcon){
            dayIconstr= R.mipmap.qing;
        }
        else if (101==dayIcon){
            dayIconstr=R.mipmap.duoyun;
        }
        else if (102==dayIcon){
            dayIconstr=R.mipmap.duoyun;
        }
        else if (103==dayIcon){
            dayIconstr=R.mipmap.duoyun;
            //晴转多云
        }
        else if (104==dayIcon){
            dayIconstr=R.mipmap.ying;
        }
        else if (200==dayIcon){
            dayIconstr=R.mipmap.feng;
            //有风
        }
        else if (201==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (202==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (203==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (204==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (205==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (206==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (207==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (208==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (209==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (210==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (211==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (212==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        else if (213==dayIcon){
            dayIconstr=R.mipmap.feng;
        }
        //300	阵雨301	强阵雨302	雷阵雨303	强雷阵雨304	雷阵雨伴有冰雹305
        else if (300==dayIcon){
            dayIconstr=R.mipmap.zhenyu;
        }
        else if (301==dayIcon){
            dayIconstr=R.mipmap.leizhenyu;
        }
        else if (302==dayIcon){
            dayIconstr=R.mipmap.leizhenyu;
        }
        else if (303==dayIcon){
            dayIconstr=R.mipmap.leizhenyu;
        }
        else if (304==dayIcon){
            dayIconstr=R.mipmap.leizhenyubanyoubingbao;
        }
        else if (305==dayIcon){
            dayIconstr=R.mipmap.xiaoyu;
        }
        //小雨306	中雨307	大雨308	极端降雨309	毛毛雨/细雨310	暴雨
        else if (306==dayIcon){
            dayIconstr=R.mipmap.zhongyu;
        }
        else if (307==dayIcon){
            dayIconstr=R.mipmap.dayu;
        }
        else if (308==dayIcon){
            dayIconstr=R.mipmap.dabaoyu;
        }
        else if (309==dayIcon){
            dayIconstr=R.mipmap.xiaoyu;
        }
        else if (310==dayIcon){
            dayIconstr=R.mipmap.dabaoyu;
        }

        else if (312==dayIcon){
            dayIconstr=R.mipmap.tedabaoyu;
        }
        else if (313==dayIcon){
            dayIconstr=R.mipmap.tedabaoyu;
        }
        //冻雨400	小雪401	中雪402	大雪403	暴雪404	雨夹雪405	雨雪天气406	阵雨夹雪407//阵雪
        else if (400==dayIcon){
            dayIconstr=R.mipmap.xiaoxue;
        }
        else if (401==dayIcon){
            dayIconstr=R.mipmap.zhongxue;
        }
        else if (402==dayIcon){
            dayIconstr=R.mipmap.daxue;
        }
        else if (403==dayIcon){
            dayIconstr=R.mipmap.baoxue;
        }
        else if (404==dayIcon){
            dayIconstr=R.mipmap.yujiaxue;
        }
        else if (405==dayIcon){
            dayIconstr=R.mipmap.yujiaxue;
        }
        else if (406==dayIcon){
            dayIconstr=R.mipmap.zhenxue;
        }
        else if (407==dayIcon){
            dayIconstr=R.mipmap.zhenxue;
        }
        //500	薄雾501	雾502	霾503	扬沙504	浮尘506	火山灰507	沙尘暴508	强沙尘暴
        else if (500==dayIcon){
            dayIconstr=R.mipmap.wu;
        }
        else if (501==dayIcon){
            dayIconstr=R.mipmap.wu;
        }
        else if (502==dayIcon){
            dayIconstr=R.mipmap.shachenbao;
        }
        else if (503==dayIcon){
            dayIconstr=R.mipmap.yangsha;
        }
        else if (504==dayIcon){
            dayIconstr=R.mipmap.fuchen;
        }
        else if (506==dayIcon){
            dayIconstr=R.mipmap.fuchen;
        }
        else if (507==dayIcon){
            dayIconstr=R.mipmap.shachenbao;
        }
        else if (508==dayIcon){
            dayIconstr=R.mipmap.qiangshachenbao;
        }
        else if (900==dayIcon){
            dayIconstr=R.mipmap.re;
        }
        else if (901==dayIcon){
            dayIconstr=R.mipmap.leng;
        }
        return dayIconstr;
    }
}
