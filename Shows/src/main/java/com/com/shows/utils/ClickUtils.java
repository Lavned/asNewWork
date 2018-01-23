package com.com.shows.utils;

/**
 * Created by Administrator on 2016/5/13 0013.
 */
public class ClickUtils {
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
