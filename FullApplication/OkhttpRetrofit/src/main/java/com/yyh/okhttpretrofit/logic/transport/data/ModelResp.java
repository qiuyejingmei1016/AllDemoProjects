package com.yyh.okhttpretrofit.logic.transport.data;

import com.google.gson.Gson;
import com.yyh.okhttpretrofit.logic.transport.GsonFactory;

/**
 *  @describe: 网络请求响应数据实体类
 *  @author: yyh
 *  @createTime: 2018/5/18 15:00
 *  @className:  ModelResp
 */
public class ModelResp extends BaseResp {

    private String name;
    private String number;
    private String id;

    public ModelResp(String name, String number, String id) {
        this.name = name;
        this.number = number;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toJson() {
        try {
            Gson gson = GsonFactory.newGson();
            return gson.toJson(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static ModelResp fromJson(String content) {
        try {
            Gson gson = GsonFactory.newGson();
            return gson.fromJson(content, ModelResp.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
