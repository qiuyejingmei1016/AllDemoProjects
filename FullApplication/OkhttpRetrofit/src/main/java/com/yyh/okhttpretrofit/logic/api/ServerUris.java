package com.yyh.okhttpretrofit.logic.api;

public class ServerUris {

//    public final static String BASE_URL = "https://x.vxiao.cn/";
//    public final static String BASE_URL = "http://120.79.105.188:8080/AnyChatFaceXClient/";//http中原

    public final static String BASE_URL = "http://192.168.0.119:8081/AnyChatFaceXClient/";


    /**
     * 获取短信验证码接口地址
     */
    public final static String SMS_URL = BASE_URL + "/v1/client/sendSmsForUserLogin";

    public final static String SMS_URL_TEST = BASE_URL + "/v1/client/sendSmsForUserLogin";
}
