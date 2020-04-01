package com.bairuitech.anychat.f2fvideo.logic;

import android.content.Context;

import com.bairuitech.anychat.f2fvideo.BuildConfig;
import com.bairuitech.anychat.f2fvideo.logic.model.PostBaseModel;
import com.bairuitech.anychat.f2fvideo.utils.SharedPreferencesUtils;
import com.bairuitech.anychat.f2fvideo.utils.net.OkHttpUtils;

import java.util.Random;

/**
 * @describe: api工具类
 * @author: yyh
 * @createTime: 2019/5/23 18:23
 * @className: ApiManager
 */
public class ApiManager {

    //用于切换测试环境或者生产环境标识
    private static boolean isTestEnvironment = false;
    //AnyChat登录ip
    public static String ANYCHAT_LOGIN_IP = isTestEnvironment ? "120.76.74.249" : "cloud.anychat.cn";
    //AnyChat登录端口
    public static String ANYCHAT_LOGIN_PORT = "8906";
    //Java服务器Ip
    public static String ANYCHAT_JAVA_IP = isTestEnvironment ? "120.76.74.249" : "120.76.74.249";
    //Java服务器端口
    public static String ANYCHAT_JAVA_PORT = isTestEnvironment ? "18080" : "18888";


    public static String BASE_URL = BuildConfig.BASE_URL;


    //根据商户编码获取登录appId、应用配置列表
    private final static String GET_APP_INFO_URL = "/AnyChatPlatform/accessApi/getAppInfo";

    //远程招聘获取面试待办信息
    private final static String GET_WAIT_RECUIT_MESSAGE_URL = "/AnyChatPlatform/accessApi/getReservationInfo";

    //远程招聘根据面试预约码、appId和appTypeCode获取用户信息
    private final static String GET_RECRUIT_USER_INFO_URL = "/AnyChatPlatform/accessApi/getRoute";

    //远程招聘根据appId和appTypeCode获取企业播报相关信息
    private final static String GET_RECRUIT_COMPANY_INFO_URL = "/AnyChatPlatform/accessApi/getCompanyInfo";

    //远程招聘企业播放文件下载地址
    private final static String DOWNLOAD_RECRUIT_COMPANY_FILE_URL =
            "/AnyChatPlatform/accessApi/download?fileNo=%1$s&from=%2$s&version=%3$s&requestId=%4$s&timestamp=%5$s";

    /**
     * 根据商户编码获取登录appId、应用配置列表
     */
    public static void getMerchantAppId(Context context, String merchantName, OkHttpUtils.BaseCallback callback) {
        PostBaseModel baseModel = new PostBaseModel();
        baseModel.setAppConfigCode(merchantName);
        String toJson = baseModel.toJson();

        String url = String.format("http://%1$s:%2$s" + GET_APP_INFO_URL,
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_IP, ANYCHAT_JAVA_IP),
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_PORT, ANYCHAT_JAVA_PORT));
        OkHttpUtils.getInstance().post(url, toJson, callback);
    }

    /**
     * 远程招聘获取面试待办信息
     */
    public static void getRecruitWaitMessage(Context context, String custPhone, String appId,
                                             OkHttpUtils.BaseCallback callback) {
        PostBaseModel baseModel = new PostBaseModel();
        baseModel.setCustPhone(custPhone);
        baseModel.setAppId(appId);
        String toJson = baseModel.toJson();

        String url = String.format("http://%1$s:%2$s" + GET_WAIT_RECUIT_MESSAGE_URL,
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_IP, ANYCHAT_JAVA_IP),
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_PORT, ANYCHAT_JAVA_PORT));
        OkHttpUtils.getInstance().post(url, toJson, callback);
    }

    /**
     * 远程招聘根据面试预约码、appId和apptypecode获取用户信息
     */
    public static void getRecruitUserInfo(Context context, String appId, String appTypeCode,
                                          String reservationNo, OkHttpUtils.BaseCallback callback) {
        PostBaseModel baseModel = new PostBaseModel();
        baseModel.setAppId(appId);
        baseModel.setAppTypeCode(appTypeCode);
        PostBaseModel.RouKeyModel keyModel = new PostBaseModel.RouKeyModel();
        keyModel.setAppTypeCode(appTypeCode);
        keyModel.setReservationNo(reservationNo);
        baseModel.setRouteKey(keyModel.toJson());
        String toJson = baseModel.toJson();
        String url = String.format("http://%1$s:%2$s" + GET_RECRUIT_USER_INFO_URL,
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_IP, ANYCHAT_JAVA_IP),
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_PORT, ANYCHAT_JAVA_PORT));
        OkHttpUtils.getInstance().post(url, toJson, callback);
    }

    /**
     * 远程招聘根据appId和appTypeCode获取企业播报相关信息
     */
    public static void getRecruitCompanyInfo(Context context, String appId, String appTypeCode, OkHttpUtils.BaseCallback callback) {
        PostBaseModel baseModel = new PostBaseModel();
        baseModel.setAppId(appId);
        baseModel.setAppTypeCode(appTypeCode);
        String toJson = baseModel.toJson();
        String url = String.format("http://%1$s:%2$s" + GET_RECRUIT_COMPANY_INFO_URL,
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_IP, ANYCHAT_JAVA_IP),
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_PORT, ANYCHAT_JAVA_PORT));
        OkHttpUtils.getInstance().post(url, toJson, callback);
    }

    /**
     * 企业播放文件下载地址
     */
    public static String getDownRecruitCompanyFileUrl(Context context, String fileNo) {
        String format = String.format("http://%1$s:%2$s",
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_IP, ANYCHAT_JAVA_IP),
                SharedPreferencesUtils.get(context, SharedPreferencesUtils.LOGIN_JAVA_PORT, ANYCHAT_JAVA_PORT));

        return String.format(format + DOWNLOAD_RECRUIT_COMPANY_FILE_URL, fileNo, Config.ANDROID,
                Config.VERSION_CODE, String.valueOf(new Random().nextInt(90000000)), String.valueOf(System.currentTimeMillis()));
    }
}