package com.newrecord.cloud.utils;

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

    public static final String ANYCHAT_LOGIN_EXPERIENCE_CODE = "login_experience_code";//体验邀请码

    public static final String ANYCHAT_LOGIN_APPID = "anychat_login_appid";//登录AnyChat 应用id

    public static final String LOGIN_ANYCHAT_IP = "def_login_anychat_ip";//anychat服务器ip

    public static final String LOGIN_ANYCHAT_PORT = "def_login_anychat_port";//anychat服务器端口

    public static final String LOGIN_JAVA_IP = "def_login_java_ip";//java服务器ip

    public static final String LOGIN_JAVA_PORT = "def_login_java_port_def";//java服务器端口

    public static final String LOGIN_MODE_BY_APPID = "login_mode_by_appId";//登录模式

    public static final String LOGIN_ENVIRONMENT_TYPE = "def_login_environment_type";//登录环境配置类型

    public static final String BUSINESS_INFO = "business_info";//业务信息相关（渠道、业务、产品）
    public static final String LOGIN_CUSTOMER_INFO = "customer_info";//客户信息相关（姓名、性别）
    public static final String OPEN_ACCOUNT_RESERVE_INFO = "open_account_reserve_info";//对公开户申请信息(预约开户地区、预约开户网点)
    public static final String OPEN_ACCOUNT_BUSINESS_LICENSE_INFO = "open_account_business_license_info";//对公开户营业执照相关信息
    public static final String VIDEO_INTERVIEW_APPLY_INFO = "video_interview_apply_info";//视频面签申请信息(贷款用途、申请额度、申请期限。。。)
    public static final String ACCOUNT_ACTIVATION_INFO = "account_activation_info";//账户激活(银行卡号。。。)

    public static final String OCR_RECOGNIZE_MODE = "ocr_recognize_mode";//ocr识别模式 true 真实模式；false测试模式

    /**
     * 保存数据
     */
    public static void save(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 保存数据
     */
    public static void save(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
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

    /**
     * 获取数据
     */
    public static boolean get(Context context, String key, boolean defValue) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }
}