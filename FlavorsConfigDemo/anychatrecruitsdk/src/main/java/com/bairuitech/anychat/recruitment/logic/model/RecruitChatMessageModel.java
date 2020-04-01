package com.bairuitech.anychat.recruitment.logic.model;

/**
 * @describe: 聊天信息实体类
 * @author: yyh
 * @createTime: 2020/2/18 16:19
 * @className: RecruitChatMessageModel
 */
public class RecruitChatMessageModel {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private String content;
    private int type;

    public RecruitChatMessageModel(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}