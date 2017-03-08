package com.tyb.xd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangpeiyu on 2016/7/3.
 */
public class TimeUtils {

    /**
     * 获取系统当前的时间
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }

    /**
     * 根据时间戳返回指定格式的时间
     *
     * @param time
     * @return
     */
    public static String getTime(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = format.format(time);
        return str;
    }

    /**
     * 根据时间戳返回指定格式的时间
     *
     * @param time
     * @return
     */
    public static String getTimeOnlyHour(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String str = format.format(time);
        return str;
    }
    /**
     * 根据指定格式的时间返回时间戳
     *
     * @param strtime
     * @return
     */
    public static Long getLongTime(String strtime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(strtime);
            return date.getTime();
        } catch (ParseException e) {
            /**
             * 时间装换失败
             */
        }
        return date.getTime();
    }

    /**
     * 根据两个时间字符串判断是否超多了5分钟
     *
     * @param oldTime
     * @param currentTime
     * @return
     */
    public static Boolean TimeOverFiveMinute(String oldTime, String currentTime) {
        Long long_currentTome = getLongTime(currentTime);
        Long long_oldTome = getLongTime(oldTime);
        if (long_currentTome - long_oldTome > 5 * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据两个时间字符串判断是否超多了5分钟
     *
     * @param oldTime
     * @param currentTime
     * @return
     */
    public static Boolean TimeOverTenMinute(String oldTime, String currentTime) {
        Long long_currentTome = getLongTime(currentTime);
        Long long_oldTome = getLongTime(oldTime);
        if (long_currentTome - long_oldTome > 10 * 60 * 1000) {
            return true;
        } else {
            return false;
        }

    }

    public static Boolean TimeOverNow(String time) {
        Long lTime = getLongTime(time);
        Long lNowTime = System.currentTimeMillis();
        if (lTime < lNowTime) {
            return false;
        } else {
            return true;
        }
    }
}
