package com.bairuitech.anychat.f2fvideo.logic.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * @describe: 获取待办信息返回数据实体类模型
 * @author: yyh
 * @createTime: 2020/3/4 18:11
 * @className: WaitRecruitModel
 */
public class WaitRecruitModel {

    private String msg;
    private List<WaitRecruitMessage> content;
    private int errorcode = -1;

    public String getMsg() {
        return msg;
    }

    public List<WaitRecruitMessage> getContent() {
        return content;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public class WaitRecruitMessage {
        private int agentId;
        private String businessName;
        private String custName;
        private String custPhone;
        private String custCardNo;
        private String productName;
        private String businessCode;
        private String productCode;
        private String appTypeCode;
        private String appId;
        private String startTime;
        private String startTimeName;
        private int id;
        private String endTime;
        private String integratorName;
        private String integratorCode;
        private String reservationNo;
        private int status;

        public int getAgentId() {
            return agentId;
        }

        public String getBusinessName() {
            return businessName;
        }

        public String getCustName() {
            return custName;
        }

        public String getCustPhone() {
            return custPhone;
        }

        public String getCustCardNo() {
            return custCardNo;
        }

        public String getProductName() {
            return productName;
        }

        public String getBusinessCode() {
            return businessCode;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getAppTypeCode() {
            return appTypeCode;
        }

        public String getAppId() {
            return appId;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getStartTimeName() {
            return startTimeName;
        }

        public int getId() {
            return id;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getIntegratorName() {
            return integratorName;
        }

        public String getIntegratorCode() {
            return integratorCode;
        }

        public String getReservationNo() {
            return reservationNo;
        }

        public int getStatus() {
            return status;
        }
    }

    public static WaitRecruitModel fromJson(String json) {
        try {
            return new Gson().fromJson(json, WaitRecruitModel.class);
        } catch (Exception e) {
            return null;
        }
    }
}
