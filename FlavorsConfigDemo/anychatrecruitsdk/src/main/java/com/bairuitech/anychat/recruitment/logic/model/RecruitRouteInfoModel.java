package com.bairuitech.anychat.recruitment.logic.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * @describe: 业务数据通道获取路由信息返回对象模型
 * @author: yyh
 * @createTime: 2020/2/26 14:57
 * @className: RecruitRouteInfoModel
 */
public class RecruitRouteInfoModel {

    private String msg;
    private DataEntity data;
    private String requestId;
    private int errorCode;
    private String strUserId;
    private ParamsEntity params;
    private int userId;
    private String command;

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public ParamsEntity getParams() {
        return params;
    }

    public int getUserId() {
        return userId;
    }

    public String getCommand() {
        return command;
    }

    public class DataEntity {
        private int queueId;
        private List<RecruitVideoCallModel.Content> content;
        private String businessHallId;
        private int status;

        public int getQueueId() {
            return queueId;
        }

        public List<RecruitVideoCallModel.Content> getContent() {
            return content;
        }

        public String getBusinessHallId() {
            return businessHallId;
        }

        public int getStatus() {
            return status;
        }

        public class Content {

            private int groupOrder;
            private String groupName;
            private List<GroupDataEntity> groupData;

            public class GroupDataEntity {
                private String name;
                private String value;
                private String key;
                private int order;
            }
        }
    }

    public class ParamsEntity {

        private String routeKey;
        private String strUserId;
        private int userId;
    }

    public static RecruitRouteInfoModel fromJson(String json) {
        try {
            return new Gson().fromJson(json, RecruitRouteInfoModel.class);
        } catch (Exception e) {
            return null;
        }
    }
}