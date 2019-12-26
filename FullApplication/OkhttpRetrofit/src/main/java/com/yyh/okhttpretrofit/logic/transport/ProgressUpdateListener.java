/*
 * 文件名: ProgressUpdateListener.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2012-5-15
 */
package com.yyh.okhttpretrofit.logic.transport;

/**
 * 进度变化回调
 */
public interface ProgressUpdateListener {

    /**
     * 调用此方法通知进度变化
     *
     * @param current 当前进度
     * @param count   变化量
     * @param total   总进度值
     */
    void onProgressUpdated(long current, int count, long total);
}