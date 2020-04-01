package com.bairuitech.anychat.f2fvideo.logic.model;

import com.google.gson.Gson;

import java.util.List;

public class ParamDataModel {

    private int queueId;
    private int businessHallId;
    private List<ContentEntity> content;

    public int getQueueId() {
        return queueId;
    }

    public int getBusinessHallId() {
        return businessHallId;
    }

    public List<ContentEntity> getContent() {
        return content;
    }

    public class ContentEntity {

        private int groupOrder;
        private String groupName;
        private List<GroupDataEntity> groupData;

        public int getGroupOrder() {
            return groupOrder;
        }

        public String getGroupName() {
            return groupName;
        }

        public List<GroupDataEntity> getGroupData() {
            return groupData;
        }

        public class GroupDataEntity {
            private String name;
            private String value;
            private String key;
            private int order;

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public void setOrder(int order) {
                this.order = order;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public String getKey() {
                return key;
            }

            public int getOrder() {
                return order;
            }
        }
    }

    public static ParamDataModel fromJson(String json) {
        try {
            return new Gson().fromJson(json, ParamDataModel.class);
        } catch (Exception e) {
            return null;
        }
    }
}
