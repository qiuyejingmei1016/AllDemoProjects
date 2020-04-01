package com.bairuitech.anychat.recruitment.logic.model.trans;

import com.google.gson.Gson;

/**
 * @describe: 外部调用传参模型实体类
 * @author: yyh
 * @createTime: 2019/4/30 16:36
 * @className: RecruitTransferModel
 */
public class RecruitTransferModel {

    private String nickName;//用户登录昵称

    private String strUserId;//用户id

    private String loginIp;//AnyChat登录ip

    private String loginPort;//AnyChat登录端口

    private String loginAppId;//AnyChat登录应用id

    private String reservationNo;//面试预约码

    private RecruitBusinessModel recruitBusinessModel;//业务信息模型实体类

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(String loginPort) {
        this.loginPort = loginPort;
    }

    public String getLoginAppId() {
        return loginAppId;
    }

    public void setLoginAppId(String loginAppId) {
        this.loginAppId = loginAppId;
    }

    public String getReservationNo() {
        return reservationNo;
    }

    public void setReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;
    }

    public RecruitBusinessModel getRecruitBusinessModel() {
        return recruitBusinessModel;
    }

    public void setRecruitBusinessModel(RecruitBusinessModel recruitBusinessModel) {
        this.recruitBusinessModel = recruitBusinessModel;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}