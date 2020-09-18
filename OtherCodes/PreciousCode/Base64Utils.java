package com.newrecord.cloud.utils;

import android.util.Base64;

/**
 * @describe: base64加解密封装类
 * @author: yyh
 * @createTime: 2019/7/23 11:42
 * @className: Base64Utils
 */
public class Base64Utils {
    //加密
    public static String encode(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    //解密
    public static byte[] decode(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }
}