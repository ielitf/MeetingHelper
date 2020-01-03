package com.ceiv.meetinghelper.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    private static DateTimeUtil instance;

    private DateTimeUtil() {
    }


    public static DateTimeUtil getInstance() {
        if (instance == null) {
            instance = new DateTimeUtil();
        }
        return instance;
    }

    //将毫秒数转化为时钟
    public String timeToClockHH(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH");//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }
    //将毫秒数转化为时钟
    public String timeToClockMM(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm");//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }
    //将毫秒数转化为时钟
    public String timeToClockSS(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("ss");//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }
    //将毫秒数转化为时钟 分+秒
    public String timeToClock(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }
    //将毫秒数转化为时钟 时+分+秒
    public String timeToClock2(Long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(time);
        return hms;
    }

    //将日期转化为毫秒数
    public long transDataToTime(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long time  = 0;
        try {
            time = formatter.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    //将时间毫秒数转换为：时分
    public String transTimeToHHMM(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date(time);
        String curDate = formatter.format(date);
        return curDate;
    }
    //将时间毫秒数转换为：月日
    public String transTimeToMMDD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        Date date = new Date(time);
        String curDate = formatter.format(date);
        return curDate;
    }
    //将时间毫秒数转换为：年-月-日
    public String transTimeToYYMMDD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String curDate = formatter.format(date);
        return curDate;
    }
    //将时间毫秒数转换为：yyyy年MM月dd日
    public String transTimeToYYMMDD2(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(time);
        String curDate = formatter.format(date);
        return curDate;
    }

    //获取系统当前日期时间
    public String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }


    //获取系统当前日期(月-日)
    public String getCurrentDateMMDD() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }
    //获取系统当前日期(年/月/日)
    public String getCurrentDateYYMMDD() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }
    //获取系统当前日期(yyyy年MM月dd日)
    public String getCurrentDateYYMMDD2() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }


    //获取系统当前日期(英文格式)
    public String getCurrentDateEnglish() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }


    //获取系统当前时间
    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }


    //获取系统当前时间
    public String getCurrentTimeHHMM() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String curDate = formatter.format(date);
        return curDate;
    }


    //获取系统当前是星期几
    public String getCurrentWeekDay(int type) {
        String week = "";
        Calendar c1 = Calendar.getInstance();
        int day = c1.get(Calendar.DAY_OF_WEEK);
        if (type == 2) {
            switch (day) {
                case 1:
                    week = "Sunday";
                    break;
                case 2:
                    week = "Monday";
                    break;
                case 3:
                    week = "Tuesdays";
                    break;
                case 4:
                    week = "Wednesday";
                    break;
                case 5:
                    week = "Thursday";
                    break;
                case 6:
                    week = "Fridays";
                    break;
                case 7:
                    week = "Saturday";
                    break;


            }
        } else {
            switch (day) {
                case 1:
                    week = "星期日";
                    break;
                case 2:
                    week = "星期一";
                    break;
                case 3:
                    week = "星期二";
                    break;
                case 4:
                    week = "星期三";
                    break;
                case 5:
                    week = "星期四";
                    break;
                case 6:
                    week = "星期五";
                    break;
                case 7:
                    week = "星期六";
                    break;

            }
        }


        return week;
    }

}
