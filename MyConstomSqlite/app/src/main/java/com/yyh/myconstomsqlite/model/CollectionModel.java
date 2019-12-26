package com.yyh.myconstomsqlite.model;

/**
 * Created by Administrator on 2018/6/27.
 */
public class CollectionModel {
    private String type;
    private String title;
    private String content;
    private String time;

    public CollectionModel(String type, String title, String content, String time) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
