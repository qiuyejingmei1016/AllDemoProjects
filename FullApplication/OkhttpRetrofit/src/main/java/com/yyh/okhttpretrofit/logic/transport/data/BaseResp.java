/*
 * 文件名: BaseResp.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-16
 */
package com.yyh.okhttpretrofit.logic.transport.data;

/**
 * @describe: 基础响应数据, 解析code和msg
 * @author: yyh
 * @createTime: 2018/5/18 14:59
 * @className: BaseResp
 */
public class BaseResp {

    /**
     * 操作成功
     */
    public static final int OK = 1;

    /**
     * Token错误
     */
    public static final int TOKEN_ERROR = 17;

    private Integer code;
    private String msg;

    public int getCode() {
        if (code != null) {
            return code;
        }
        return 0;
    }

    public String getMessage() {
        return msg;
    }
}