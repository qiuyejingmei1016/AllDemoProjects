package com.bairuitech.anychat.f2fvideo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @describe: 共享参数存储工具类
 * @author: yyh
 * @createTime: 2018/8/20 14:49
 * @className: SharedPreferencesUtils
 */
public class SharedPreferencesUtils {

    private static final String FILE_NAME = "share_data";

    public static final String LOGIN_USER_ACCOUNT = "login_user_account";//登录账户

    public static final String LOGIN_APPID = "login_appid";//登录AnyChat 应用id

    public static final String LOGIN_MERCHANT_ID = "merchant_id";//登录商户简称

    public static final String LOGIN_CUSTOMER_INFO = "customer_info";//客户信息

    public static final String LOGIN_CUSTOMER_RESERVATE_INFO = "customer_reservate_info";//客户预约信息

    public static final String LOGIN_ANYCHAT_IP = "login_anychat_ip";//anychat服务器ip

    public static final String LOGIN_ANYCHAT_PORT = "login_anychat_port";//anychat服务器端口

    public static final String LOGIN_JAVA_IP = "login_java_ip";//java服务器ip

    public static final String LOGIN_JAVA_PORT = "login_java_port";//java服务器端口

    /**
     * 保存数据
     */
    public static void save(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取数据
     */
    public static String get(Context context, String key) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    /**
     * 获取数据
     */
    public static String get(Context context, String key, String defValue) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(key, defValue);
    }
}