package com.yyh.fullapplication;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;

import static com.yyh.fullapplication.Urls.CREATE_TRADE_URL;
import static com.yyh.fullapplication.Urls.SMS_URL;
import static com.yyh.fullapplication.Urls.UNDATETRADEINFO_URL;

/**
 * Created by Administrator on 2018/5/18.
 */

public class ApiClient {

    /**
     * 获得打印的参数
     *
     * @param params
     * @return
     */
    private static String getParamString(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder paramStringBuilder = new StringBuilder();
        for (String key : params.keySet()) {
            paramStringBuilder.append(key + "=" + params.get(key) + "&");
        }

        String paramStr = paramStringBuilder.toString();
        if (!TextUtils.isEmpty(paramStr)) {
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        return paramStr;
    }

    /**
     * 更新流水信息
     */
    public static void updateTradeInfo(String tradeNo, String clientBusinessHallName,
                                       String clientBusinessHallNo, HttpUtil.ResultCallback resultCallback) {
        ArrayList<HttpUtil.ParamPair> params = new ArrayList<HttpUtil.ParamPair>();
        if (!TextUtils.isEmpty(tradeNo)) {
            params.add(new HttpUtil.ParamPair("tradeNo", tradeNo));
        }
        if (!TextUtils.isEmpty(clientBusinessHallName)) {
            params.add(new HttpUtil.ParamPair("tradeNo", clientBusinessHallName));
        }
        if (!TextUtils.isEmpty(clientBusinessHallNo)) {
            params.add(new HttpUtil.ParamPair("tradeNo", clientBusinessHallNo));
        }
        HttpUtil.post(UNDATETRADEINFO_URL, resultCallback, params);
    }

    /**
     * 获取短信验证码
     */
    @SuppressWarnings("rawtypes")
    public static void requestSmsCode(String phoneNumber, HttpUtil.ResultCallback resultCallback) {
        HttpUtil.get(SMS_URL + "?phonenumber=" + phoneNumber, resultCallback);
    }


    public static void getClassData(GroupInfo groupInfo, HttpUtil.ResultCallback resultCb) {
        ArrayList<HttpUtil.ParamPair> params = new ArrayList<HttpUtil.ParamPair>();
        if (!TextUtils.isEmpty(groupInfo.getId())) {
            params.add(new HttpUtil.ParamPair("id", groupInfo.getId()));
        }
        if (!TextUtils.isEmpty(groupInfo.getName())) {
            params.add(new HttpUtil.ParamPair("customerName", groupInfo.getName()));
        }
        if (!TextUtils.isEmpty(groupInfo.getNumber())) {
            params.add(new HttpUtil.ParamPair("password", groupInfo.getNumber()));
        }
        HttpUtil.post(CREATE_TRADE_URL, resultCb, params);
    }
}
