package com.yyh.okhttpretrofit.logic.transport.base;

/**
 *  @describe: 请求
 *  @author: yyh
 *  @createTime: 2018/5/18 14:54
 *  @className:  Request
 */
public class Request {

    /**
     * 请求的业务Id, 能够通过此Id知道具体的业务
     */
    private int requestId;

    /**
     * 请求的业务数据
     */
    private Object data;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}