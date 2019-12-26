package com.yyh.okhttpretrofit.logic.api;

import com.yyh.okhttpretrofit.logic.RequestField;
import com.yyh.okhttpretrofit.logic.transport.data.BaseResp;
import com.yyh.okhttpretrofit.logic.transport.data.ModelResp;
import com.yyh.okhttpretrofit.logic.transport.data.PostResp;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * @describe: 网络接口定义类
 * @author: yyh
 * @createTime: 2018/5/18 15:12
 * @className: ApiRequestServer
 */
public interface ApiRequestServer {

    @GET("business/readingtask/query/detail/{" + RequestField.MSG_ID + "}.json")
    Call<ModelResp> getReadTaskList(@Path(RequestField.MSG_ID) String msgId);

    // /business/readingtask/query/book/review/{MSG_ID}.json?num=12?groupId=6236620?version=1
    @GET("business/readingtask/query/book/review/{" + RequestField.MSG_ID + "}.json")
    Call<ModelResp> getBookComment(@Path(RequestField.MSG_ID) String msgId, @QueryMap Map<String, String> map);

    // /business/readingtask/query/book/review/{GROUP_ID}/{PERSON_ID}.json
    @GET("rest/user/detail/{" + RequestField.GROUP_ID + "}/{" + RequestField.PERSON_ID + "}.json")
    Call<ModelResp> getSchoolPersonInfo(@Path(RequestField.GROUP_ID) String groupNumber,
                                        @Path(RequestField.PERSON_ID) String personId);



    @GET("base/aas/vcode/17666536986.json")
    Call<ModelResp> sendSMS(@QueryMap Map<String, String> map);



    @GET("v1/client/sendSmsForUserLogin")
    Call<BaseResp> getSMSCode(@QueryMap Map<String, String> map);

    //  /rest/vke/modify/flag/{MSG_ID}/.json?num=12?groupId=6236620?version=1
    @POST("v1/client/trade/updateTradeInfo/{" + RequestField.MSG_ID + "}.json")
    Call<BaseResp> sendModify(@Path(RequestField.MSG_ID) String msgId, @QueryMap Map<String, String> map);



    @POST("v1/client/trade/createTrade")
    Call<ModelResp> createTrade(@Body ModelResp modelResp);

    @POST("v1/client/newtrade/createTrade")
    Call<PostResp> createTrade(@Body PostResp resp);
}
