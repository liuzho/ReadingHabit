package com.liuzh.readinghabit.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期的工具类
 * Created by 刘晓彬 on 2017/3/19.
 */

public class DateUtil {
    /**
     * 获取格式化后的年月日
     *
     * @return 格式化后的年月日
     */
    public static String getOneYMD() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }


    public static String getReadYMD() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static String getDay() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(new Date());
    }

    public static String getMouth() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return sdf.format(new Date());
    }

    public static String getYear() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new Date());
    }

    /**
     * 是不是闰年
     * @param year 年份
     * @return true：是；false：否
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

}
