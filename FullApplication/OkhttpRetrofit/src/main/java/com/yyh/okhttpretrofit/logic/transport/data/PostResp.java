package com.yyh.okhttpretrofit.logic.transport.data;

/**
 * Created by Administrator on 2018/8/24.
 */

public class PostResp extends BaseResp {
    private String userId;
    private String userName;

    public PostResp(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
