/*
 * 文件名: SoftResponseListener.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-4-30
 */
package com.yyh.okhttpretrofit.logic.transport.base;

import java.lang.ref.WeakReference;

/**
 * @describe: 软应用的ResponseListener(ui界面处理请求响应信息)
 * @author: yyh
 * @createTime: 2018/5/18 14:58
 * @className: WeakRefResponseListener
 */
public class WeakRefResponseListener implements Response.ResponseListener {

    private WeakReference<Response.ResponseListener> mResponseListenerRef;

    public WeakRefResponseListener(Response.ResponseListener responseListener) {
        this.mResponseListenerRef = new WeakReference<Response.ResponseListener>(responseListener);
    }

    @Override
    public void onResponse(Response response) {
        Response.ResponseListener responseListener = mResponseListenerRef.get();
        if (responseListener != null) {
            responseListener.onResponse(response);
        }
    }
}