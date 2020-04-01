package com.bairuitech.anychat.recruitment.logic.model;

import com.google.gson.Gson;

/**
 * @describe: 实体类基类
 * @author: AnyChat
 * @createTime: 2019/3/11 16:01
 * @className: RecruitBaseModel
 */
public class RecruitBaseModel {

    private String requestId;//请求id 8位请求随机数
    private String from = "Android";//平台类型(Android ios)
    private String command;    //指令
    private long timestamp;   //时间戳
    private int userId;        //anychat登录返回id
    private String strUserId;  //用户名或者登陆账号名
    private String appId;      //anychat登录应用id

    private String version;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private ParamsEntity params;

    public void setParams(ParamsEntity params) {
        this.params = params;
    }

    public static class ParamsEntity {

    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}