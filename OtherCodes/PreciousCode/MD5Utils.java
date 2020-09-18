package com.newrecord.cloud.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @describe: MD5加密工具类
 * @author: yyh
 * @createTime: 2019/7/16 10:34
 * @className: MD5Utils
 */
public class MD5Utils {

    /**
     * md5加密字符串
     *
     * @param string
     */
    public static String encodeMD5(String string) throws NoSuchAlgorithmException {
        if (StringUtil.isNullOrEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(string.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            if ((b & 0xFF) < 0x10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toHexString(b & 0xFF));
        }
        return stringBuilder.toString().toUpperCase();
    }

//    public static String encodeBy32BitMD5(String source) {
//        return encrypt(source, false);
//    }
//
//    private static String encrypt(String string, boolean is16bit) {
//        if (StringUtil.isNullOrEmpty(string)) {
//            return "";
//        }
//
//        String encryptedStr = null;
//        try {
//            MessageDigest digester = MessageDigest.getInstance("MD5Utils");
//            encryptedStr = convertToHexString(digester.digest(string.getBytes("utf-8")));
//            if (is16bit) {
//                encryptedStr = encryptedStr.substring(8, 24);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return encryptedStr;
//    }
//
//    private static String convertToHexString(byte data[]) {
//        int i;
//        StringBuilder buf = new StringBuilder();
//        for (int offset = 0; offset < data.length; offset++) {
//            i = data[offset];
//            if (i < 0) {
//                i += 256;
//            }
//            if (i < 16) {
//                buf.append("0");
//            }
//            buf.append(Integer.toHexString(i));
//        }
//        return buf.toString();
//    }
}