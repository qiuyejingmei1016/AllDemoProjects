package com.bairuitech.anychat.f2fvideo.logic.model;

import com.google.gson.Gson;

public class AccessRouteModel {

    private String msg;
    private ContentEntity content;
    private int errorcode = -1;

    public String getMsg() {
        return msg;
    }

    public ContentEntity getContent() {
        return content;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public class ContentEntity {
        private int queueId;
        private String createdUserId;
        private String routeKey;
        private String businessName;
        private String routeName;
        private String businessCode;
        private String isCheck;
        private String createdOrgId;
        private String appId;
        private String paramData;
        private int businessHallId;
        private String createdTime;
        private int id;
        private String integratorName;
        private int routeType;
        private String integratorCode;

        public int getQueueId() {
            return queueId;
        }

        public String getCreatedUserId() {
            return createdUserId;
        }

        public String getRouteKey() {
            return routeKey;
        }

        public String getBusinessName() {
            return businessName;
        }

        public String getRouteName() {
            return routeName;
        }

        public String getBusinessCode() {
            return businessCode;
        }

        public String getIsCheck() {
            return isCheck;
        }

        public String getCreatedOrgId() {
            return createdOrgId;
        }

        public String getAppId() {
            return appId;
        }

        public String getParamData() {
            return paramData;
        }

        public int getBusinessHallId() {
            return businessHallId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public int getId() {
            return id;
        }

        public String getIntegratorName() {
            return integratorName;
        }

        public int getRouteType() {
            return routeType;
        }

        public String getIntegratorCode() {
            return integratorCode;
        }
    }

    public static AccessRouteModel fromJson(String json) {
        try {
            return new Gson().fromJson(json, AccessRouteModel.class);
        } catch (Exception e) {
            return null;
        }
    }
}
