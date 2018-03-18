package com.liuzh.readinghabit.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期的工具类
 * Created by 刘晓彬 on 2017/3/19.
 */

public class DateUtil {


    public static final long ONE_DAY = 1000 * 60 * 60 * 24;

    /**
     * 获取格式化后的年月日
     *
     * @return 格式化后的年月日
     */
    public static String getOneYMD() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }


    public static String getReadYMD() {
        return getReadYMD(System.currentTimeMillis());
    }

    public static String getReadYMD(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return sdf.format(new Date(mills));
    }

    public static String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.US);
        return sdf.format(new Date());
    }

    public static String getMouth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.US);
        return sdf.format(new Date());
    }

    public static String getTodayLastDay() {
        return getLastDay(System.currentTimeMillis());
    }

    public static String getLastDay(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return sdf.format(new Date(mills - ONE_DAY));
    }

    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.US);
        return sdf.format(new Date());
    }

    /**
     * 是不是闰年
     *
     * @param year 年份
     * @return true：是；false：否
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

}
