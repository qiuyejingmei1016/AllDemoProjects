package com.bairuitech.anychat.f2fvideo.logic.model;

import com.bairuitech.anychat.f2fvideo.logic.Config;
import com.google.gson.Gson;

import java.util.Random;

/**
 * @describe: http网络请求参数传递基类
 * @author: yyh
 * @createTime: 2020/3/7 17:18
 * @className: PostBaseModel
 */
public class PostBaseModel {

    private String appTypeCode;
    private String routeKey;
    private String appConfigCode;
    private String appId;
    private String custPhone;

    private String from = Config.ANDROID;//平台标识
    private String version = Config.VERSION_CODE;//版本号
    private String requestId = String.valueOf(new Random().nextInt(90000000));//请求id 8位请求随机数
    private String timestamp = String.valueOf(System.currentTimeMillis());   //时间戳

    public void setAppTypeCode(String appTypeCode) {
        this.appTypeCode = appTypeCode;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public void setAppConfigCode(String appConfigCode) {
        this.appConfigCode = appConfigCode;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class RouKeyModel {

        private String appTypeCode;
        private String reservationNo;

        public void setAppTypeCode(String appTypeCode) {
            this.appTypeCode = appTypeCode;
        }

        public void setReservationNo(String reservationNo) {
            this.reservationNo = reservationNo;
        }

        public String toJson() {
            return new Gson().toJson(this);
        }
    }


    public String toJson() {
        return new Gson().toJson(this);
    }
}
