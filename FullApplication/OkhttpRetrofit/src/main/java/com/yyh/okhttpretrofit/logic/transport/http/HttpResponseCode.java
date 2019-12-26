package com.yyh.okhttpretrofit.logic.transport.http;

public class HttpResponseCode {

    // ====================================================异常执行码====================================================

    /**
     * 失败
     */
    public static final int EXEC_CODE_NOK = 0;
    /**
     * 网络不可用
     */
    public static final int EXEC_CODE_NETWORK_UNAVAILABLE = -1;
    /**
     * 无法正常请求
     */
    public static final int EXEC_CODE_CANNOT_SEND_REQUEST = -2;
    /**
     * 连接异常
     */
    public static final int EXEC_CODE_CONNECT_ERROR = -3;
    /**
     * 读取超时
     */
    public static final int EXEC_CODE_SOCKET_TIMEOUT = -4;
    /**
     * IO异常, 包括Socket异常, 以及本地异常
     */
    public static final int EXEC_CODE_IO_ERROR = -5;
    /**
     * 解析异常
     */
    public static final int EXEC_CODE_PARSE_ERROR = -6;


    // ====================================================正常执行码====================================================

    /**
     * 请求成功发出
     */
    public static final int EXEC_CODE_SEND_SUCCESSFUL = 1;
    /**
     * 请求成功完成
     */
    public static final int EXEC_CODE_REQUEST_SUCCESSFUL = 2;
    /**
     * 手动中断
     */
    public static final int EXEC_CODE_INTERRUPTED = 3;

}
