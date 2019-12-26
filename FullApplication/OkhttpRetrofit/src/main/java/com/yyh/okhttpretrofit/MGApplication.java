/*
 * 文件名: MGApp.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-2
 */
package com.yyh.okhttpretrofit;

import android.app.Application;
import android.content.Context;

import com.yyh.okhttpretrofit.logic.transport.http.IRetrofitEngineProxy;
import com.yyh.okhttpretrofit.logic.transport.http.concrete.RetrofitEngineProxy;

import java.util.HashMap;
import java.util.Map;


/**
 * 应用实例
 *
 * @author Kelvin Van
 */
public class MGApplication extends Application {

    private static MGApplication sInstance;

    private static IRetrofitEngineProxy sRetrofitProxy;

    public MGApplication() {
        super();
        sInstance = this;
    }

    public static Map<String, String> getHttpRequestHeaders(Context context) {
        return sInstance.fillHttpRequestHeaders(context);
    }

    protected Map<String, String> fillHttpRequestHeaders(Context context) {
        Map<String, String> headers;
        headers = new HashMap<String, String>();
        headers.put("Content-type", "application/json;charset=UTF-8");
        return headers;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MGApplication getInstance() {
        return sInstance;
    }


    public static synchronized IRetrofitEngineProxy getRetrofitProxy(Context context) {
        if (sRetrofitProxy == null) {
            sRetrofitProxy = sInstance.newRetrofitProxy(context);
        }
        if (sRetrofitProxy != null) {
            sRetrofitProxy.setDebug(BuildConfig.DEBUG);
        }
        return sRetrofitProxy;
    }

    protected IRetrofitEngineProxy newRetrofitProxy(Context context) {
        return new RetrofitEngineProxy();
    }
}