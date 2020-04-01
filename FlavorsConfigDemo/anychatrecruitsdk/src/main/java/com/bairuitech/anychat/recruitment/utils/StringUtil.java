package com.bairuitech.anychat.recruitment.utils;

import java.text.SimpleDateFormat;
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
//        int hour = seconds / (60 * 60);
        int min = (seconds / 60) % 60;
        int s = seconds % 60;
//        String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;
//        strShow = hourStr + ":" + minStr + ":" + seondStr;
        strShow = minStr + ":" + seondStr;
        return strShow;
    }

//    public static String formatTime(int timeMs) {
//        int totalSeconds = timeMs / 1000;
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours = totalSeconds / 3600;
//        if (hours > 0) {
//            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
//        } else {
//            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
//        }
//    }

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
}