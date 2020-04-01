package com.bairuitech.anychat.recruitment.logic.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * @describe: app呼叫坐席传参实体类
 * @author: AnyChat
 * @createTime: 2019/3/11 16:08
 * @className: RecruitVideoCallModel
 */
public class RecruitVideoCallModel implements Serializable {

    private String from = "Android";//平台类型标识
    private String thirdTradeNo; //业务流水号
    private int type = 2;         //枚举值，1：横向排版 2：纵向排版
    private String expansion;     //扩展字段：用于传递地理位置信息、ip地址信息使用
    private List<Content> content;//展示内容

    public String getThirdTradeNo() {
        return thirdTradeNo;
    }

    public void setThirdTradeNo(String thirdTradeNo) {
        this.thirdTradeNo = thirdTradeNo;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExpansion() {
        return expansion;
    }

    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public List<Content> getContent() {
        return content;
    }

    public static class Content implements Serializable {
        private String groupName; //分组展示名称
        private int groupOrder;//分组排序号
        private List<GroupData> groupData;//分组展示内容

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public void setGroupOrder(int groupOrder) {
            this.groupOrder = groupOrder;
        }

        public List<GroupData> getGroupData() {
            return groupData;
        }

        public void setGroupData(List<GroupData> groupData) {
            this.groupData = groupData;
        }

        public static class GroupData implements Serializable {
            private String key;//属性标识
            private String value; //属性值
            private String name;//属性名称
            private int order; //排序号

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setOrder(int order) {
                this.order = order;
            }
        }
    }

    public static RecruitVideoCallModel fromJson(String json) {
        try {
            return new Gson().fromJson(json, RecruitVideoCallModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}