package com.news.yazhidao.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ariesy on 4/27/15.
 */
public class DateUtil {

    /**
     * 获取当前系统的时间  格式:2015-06-04 12:12:34
     * @return
     */
    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    /**
     * 获取当前系统的时间 带有毫秒值  格式:2015-06-04 12:12:34.578
     * @return
     */
    public static String getDateWithMS() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(date);
    }

    /**
     * 获取指定时间的月和日,格式06-24
     */
    public static String getMonthAndDay(String dateStr){
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr));
            return String.format("%02d", c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE));
        } catch (ParseException e) {
            return getMonthAndDay(getDate());
        }
    }

    /**
     * 把字符串转换成毫秒值
     */
    public static long dateStr2Long(String dateStr) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr));
            return c.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String longToDAte(long time){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }


    public static Calendar strToCalendarLong(String strDate) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c;
    }
    @SuppressLint("SimpleDateFormat")
    public static String getTimes(long unixLong) {
        long currentDate = System.currentTimeMillis();
        long times = currentDate / 1000 - unixLong / 1000;
        // return "侧首";
        if (times > 172800) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(unixLong);
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String time = dateformat.format(c.getTime());
            String[] timeArray = time.split("-");
            int month = Integer.parseInt(timeArray[0]);
            int day = Integer.parseInt(timeArray[1]);
            time = month + "-" + day;
            return time;
        } else if (times >= 86400 && times < 172800) {
            return "昨天";
        } else if (times > 3600 && times < 86400) {
            return times / (60 * 60) + "小时前";
        } else if (times > 60 && times <= 3600) {
            return times / (60) + "分钟前";
        } else {
            return "刚刚";
        }
    }
}
