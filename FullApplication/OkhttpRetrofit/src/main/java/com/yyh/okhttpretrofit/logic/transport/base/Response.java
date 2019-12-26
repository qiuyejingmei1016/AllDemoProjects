
package com.yyh.okhttpretrofit.logic.transport.base;

/**
 *  @describe: 响应
 *  @author: yyh
 *  @createTime: 2018/5/18 14:55
 *  @className:  Response
 */
public class Response {

    /**
     * 失败
     */
    public static final int BUSINESS_CODE_NOK = 0;
    /**
     * 成功
     */
    public static final int BUSINESS_CODE_OK = 1;

    /**
     * 业务码
     */
    private int mBusinessCode = BUSINESS_CODE_NOK;
    private Request mRequestInfo;
    private Object mData;
    private String message;
    private int code;

    public Request getRequestInfo() {
        return mRequestInfo;
    }

    public void setRequestInfo(Request requestInfo) {
        this.mRequestInfo = requestInfo;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }

    public int getBusinessCode() {
        return mBusinessCode;
    }

    public void setBusinessCode(int businessCode) {
        this.mBusinessCode = businessCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public interface ResponseListener {

        void onResponse(Response response);
    }
}