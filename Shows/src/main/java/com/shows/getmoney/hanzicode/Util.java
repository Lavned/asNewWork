package com.shows.getmoney.hanzicode;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by junweiliu on 16/5/9.
 */
public class Util {


    /**
     * 获取随机的颜色
     *
     * @return
     */
    public static int randomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);
        return Color.argb(255, red, green, blue);
    }

    /**
     * 获取随机的暗色
     *
     * @return
     */
    public static int randomDarkColor() {
        int red = (int) (Math.random() * 100 + 56);
        int green = (int) (Math.random() * 100 + 56);
        int blue = (int) (Math.random() * 100 + 56);
        return Color.argb(255, red, green, blue);
    }

    /**
     * 获取指定长度随机简体中文
     *
     * @param len int
     * @return String
     */
    public static String getRandomJianHan(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); //获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); //获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBk"); //转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

    /**
     * 计算GridView宽高
     *
     * @param gridView
     */
    public static void calGridViewWidthAndHeigh(int numColumns, GridView gridView) {

        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, gridView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);

            if ((i + 1) % numColumns == 0) {
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }

            if ((i + 1) == len && (i + 1) % numColumns != 0) {
                // 统计所有子项的总高度
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        // 适当添加一些高度,防止gridview高度不够,出现滚动条
        totalHeight += 60;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        // params.height最后得到整个ListView完整显示需要的高度
        gridView.setLayoutParams(params);
    }
}
