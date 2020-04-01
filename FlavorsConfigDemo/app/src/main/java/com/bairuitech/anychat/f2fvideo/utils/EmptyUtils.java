package com.bairuitech.anychat.f2fvideo.utils;

/**
 * @describe: 字符串操作封装
 * @author: yyh
 * @createTime: 2018/5/29 10:56
 * @className: EmptyUtils
 */
public final class EmptyUtils {

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
}