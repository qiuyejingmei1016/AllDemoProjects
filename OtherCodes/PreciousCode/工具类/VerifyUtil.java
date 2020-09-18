package com.newrecord.cloud.utils;

import android.text.TextUtils;

/**
 * 校验工具
 */

public class VerifyUtil {


    /**
     * 验证手机格式
     */
    public static boolean isMobile(String mobiles) {
        String telRegex = "[1][3456789]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }else {
            return mobiles.matches(telRegex);
        }
    }


    /**
     * 产生1000-9999的随机数
     * */
    public static String generateVarCode(){
        int randomVarCode = (int)(Math.random() * (9999 - 1000 + 1)) + 1000;
        return String.valueOf(randomVarCode);
    }
}
