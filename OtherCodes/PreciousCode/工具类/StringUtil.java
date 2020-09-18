package com.newrecord.cloud.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @describe: 字符串操作封装
 * @author: yyh
 * @createTime: 2018/5/29 10:56
 * @className: StringUtil
 */
public final class StringUtil {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() < 1;
    }

    public static String getNotNullString(String str, String ret) {
        return str == null ? ret : str;
    }

    public static String getNotNullString(String str) {
        return getNotNullString(str, "");
    }

    /**
     * 拼接多个字符串
     */
    public static String append(String... strs) {
        StringBuilder buffer = new StringBuilder(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            buffer.append(strs[i]);
        }
        return buffer.toString();
    }

    public static boolean isNumberic(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 拼接多个字符串
     *
     * @param strs      多个字符串
     * @param separator 间隔符
     * @return 拼接后的字符串
     */
    public static String append(String[] strs, String separator) {
        StringBuilder buffer = new StringBuilder(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            buffer.append(separator).append(strs[i]);
        }
        return buffer.toString();
    }

    /**
     * 是否相等
     * 如果两个都为null, 或者两个的值一样, 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个都为null, 或者两个的值一样, 则返回true, 否则返回false.
     */
    public static boolean equals(String str1, String str2) {
        return str1 == str2 || equalsNotNull(str1, str2);
    }

    /**
     * 是否相等(不区分大小写)
     * 如果两个都为null, 或者两个的值一样(不区分大小写), 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个都为null, 或者两个的值一样(不区分大小写), 则返回true, 否则返回false.
     */
    private static boolean equalsIgnore(String str1, String str2) {
        return str1 == str2 || equalsIgnoreNotNull(str1, str2);
    }

    /**
     * 是否相等
     * 如果两个的值一样, 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个的值一样, 则返回true, 否则返回false.
     */
    public static boolean equalsNotNull(String str1, String str2) {
        return str1 != null && str1.equals(str2);
    }

    /**
     * 是否相等(不区分大小写)
     * 如果两个的值一样, 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个的值一样(不区分大小写), 则返回true, 否则返回false.
     */
    public static boolean equalsIgnoreNotNull(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    public static String trim(String str) {
        return str != null ? str.trim() : str;
    }

    /**
     * 转换时间  格式hh:mm:ss
     *
     * @param seconds
     */
    public static String formatTime(int seconds) {
        String strShow;
        int hour = seconds / (60 * 60);
        int min = (seconds / 60) % 60;
        int s = seconds % 60;
        String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;
        strShow = hourStr + ":" + minStr + ":" + seondStr;
        return strShow;
    }
	
	 /**
     * 转换时间  格式xx时xx分xx秒
     *
     * @param seconds
     */
    public static String formatHMSTime(int seconds) {
        String strShow;
        int hour = seconds / (60 * 60);
        int min = (seconds / 60) % 60;
        int s = seconds % 60;
        String hourStr = (hour >= 10) ? "" + hour : "" + hour;
        String minStr = (min >= 10) ? "" + min : "" + min;
        String secondStr = (s >= 10) ? "" + s : "" + s;
        if (hour <= 0 && min <= 0) {
            strShow = secondStr + "秒";
        } else if (min <= 0 && s <= 0) {
            strShow = hourStr + "时";
        } else if (hour <= 0 && s <= 0) {
            strShow = minStr + "分";
        } else if (hour <= 0) {
            strShow = minStr + "分" + secondStr + "秒";
        } else if (min <= 0) {
            strShow = hourStr + "时" + secondStr + "秒";
        } else if (s <= 0) {
            strShow = hourStr + "时" + minStr + "分";
        } else {
            strShow = hourStr + "时" + minStr + "分" + secondStr + "秒";
        }
        return strShow;
    }
	
    /**
     * 获取当前时间  格式yyyy-MM-dd
     */
    public static String getCurrentData() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

    /**
     * 获取当前时间  格式yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

    /**
     * 日期格式化 yyyyMMddHHmmssSSS
     */
    public static String getCurrentFormatTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

    public static String formatTimeString(String str) {
        try {
            StringBuilder sb = new StringBuilder(str);
            sb.insert(4, ".");
            sb.insert(7, ".");
            return sb.toString();
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 把yyyymmdd转成yyyy-MM-dd格式
     */
    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 时间比较
     *
     * @param startTime
     * @param endTime
     */
    public static int getTimeCompareSize(String startTime, String endTime) {
        int i = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            // 1结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() < date1.getTime()) {
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //正常情况下的逻辑操作.
                i = 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 从当前时间开始知道直到几天后的时间
     *
     * @param amount 几天后
     */
    public static String getAfterDay(String currentTime, int amount) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date currentToday = format.parse(currentTime);//开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentToday);
            calendar.add(Calendar.DAY_OF_MONTH, amount);
            Date afterDay = calendar.getTime();
            return format.format(afterDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}