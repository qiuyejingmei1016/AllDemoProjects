/*
 * 文件名: GsonFactory.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-5-4
 */
package com.yyh.okhttpretrofit.logic.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

/**
 *  @describe: Gson工厂
 *  @author: yyh
 *  @createTime: 2018/5/18 15:05
 *  @className:  GsonFactory
 */
public final class GsonFactory {

    private static final Gson STATIC;

    static {
        STATIC = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC | Modifier.TRANSIENT)
                .disableHtmlEscaping()
                .create();
    }

    public static Gson newGson() {
        return STATIC;
    }

}