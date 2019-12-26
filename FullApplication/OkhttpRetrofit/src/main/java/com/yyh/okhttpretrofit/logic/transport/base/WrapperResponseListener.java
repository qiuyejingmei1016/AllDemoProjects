/*
 * 文件名: WrapperResponseListener.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-1-28
 */
package com.yyh.okhttpretrofit.logic.transport.base;

/**
 * @describe: ResponseListener嵌套包装
 * @author: yyh
 * @createTime: 2018/5/18 14:58
 * @className: WrapperResponseListener
 */
public class WrapperResponseListener implements Response.ResponseListener {

    private Response.ResponseListener mMainListener;
    private Response.ResponseListener mWrappedListener;

    /**
     * @param mainListener    主回调
     * @param wrappedListener 副回调
     */
    public WrapperResponseListener(Response.ResponseListener mainListener, Response.ResponseListener wrappedListener) {
        this.mMainListener = mainListener;
        this.mWrappedListener = wrappedListener;
    }

    @Override
    public void onResponse(Response response) {
        if (mMainListener != null) {
            mMainListener.onResponse(response);
        }
        if (mWrappedListener != null) {
            mWrappedListener.onResponse(response);
        }
    }
}