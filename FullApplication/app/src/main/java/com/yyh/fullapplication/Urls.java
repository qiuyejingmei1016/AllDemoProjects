package com.yyh.fullapplication;

/**
 * Created by Administrator on 2018/5/18.
 */

public class Urls {
    public final static String BASE_URL = "http://120.79.105.188:8080/AnyChatFaceXClient";//http中原

    /**
     * 获取短信验证码接口地址
     */
    public final static String SMS_URL = BASE_URL + "/v1/client/sendSmsForUserLogin";


    /**
     * 流水号创建接口
     */
    public final static String CREATE_TRADE_URL = BASE_URL + "/v1/client/trade/createTrade";

    /**
     * 更新流水信息
     */
    public final static String UNDATETRADEINFO_URL = BASE_URL+"/v1/client/trade/updateTradeInfo";


}
