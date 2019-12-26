/*
 * 文件名: ManagerBase.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-2-20
 */
package com.yyh.okhttpretrofit.logic;

import android.content.Context;

/**
 * 管理器基类
 *
 * @author Kelvin Van
 */
public abstract class ManagerBase {

    private Context mContext;

    public ManagerBase(Context context) {
        this.mContext = context;
    }

    protected final Context getContext() {
        return this.mContext;
    }

}