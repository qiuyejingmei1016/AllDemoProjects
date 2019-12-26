package com.yyh.okhttpretrofit.logic;

import android.content.Context;
import android.text.TextUtils;

import com.yyh.okhttpretrofit.MGApplication;
import com.yyh.okhttpretrofit.logic.api.ApiRequestServer;
import com.yyh.okhttpretrofit.logic.api.ServerUris;
import com.yyh.okhttpretrofit.logic.transport.base.Request;
import com.yyh.okhttpretrofit.logic.transport.base.Response;
import com.yyh.okhttpretrofit.logic.transport.base.Response.ResponseListener;
import com.yyh.okhttpretrofit.logic.transport.data.BaseResp;
import com.yyh.okhttpretrofit.logic.transport.data.ModelResp;
import com.yyh.okhttpretrofit.logic.transport.data.PostResp;
import com.yyh.okhttpretrofit.logic.transport.http.IRetrofitEngineProxy;
import com.yyh.okhttpretrofit.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * @describe: 网络请求管理类
 * @author: yyh
 * @createTime: 2018/5/18 15:11
 * @className: ApiRequestManager
 */
public class ApiRequestManager extends ManagerBase implements Response.ResponseListener {
    private ApiRequestServer mServer;
    private IRetrofitEngineProxy mProxy;

    public ApiRequestManager(Context context) {
        super(context);
        IRetrofitEngineProxy proxy = MGApplication.getRetrofitProxy(context);
        if (proxy != null) {
            mProxy = proxy;
            mServer = proxy.createHttpServer(ServerUris.BASE_URL,
                    MGApplication.getHttpRequestHeaders(context), ApiRequestServer.class);
        }
    }


    public static final String getBookFileName(String type) {
        StringBuilder builder = new StringBuilder();
        builder.append("save_file_");
        builder.append("t_").append(type);
        return builder.toString();
    }

    /**
     * @param modelResp post数据
     * @param userId    用户id
     * @param isSave    是否保存响应的数据
     * @param saveType
     * @param listener
     */
    public void createTrade(ModelResp modelResp, String userId, boolean isSave,
                            String saveType, ResponseListener listener) {
        Request requestInfo = new Request();
        requestInfo.setRequestId(CallbackIds.CREATE_TRADE);

        CustomRequestData data = new CustomRequestData();
        data.setUserId(userId);
        data.setSave(isSave);
        data.setSaveType(saveType);

        requestInfo.setData(data);
        Call<ModelResp> call = mServer.createTrade(modelResp);
        mProxy.enqueue(call, getContext(), requestInfo, this, listener);
    }

    /**
     * 获取短信验证码
     *
     * @param userId
     * @param phoneNumber 手机号
     * @param listener
     */
    public void getVerificationCode(String userId, String phoneNumber, ResponseListener listener) {
        Request requestInfo = new Request();
        requestInfo.setRequestId(CallbackIds.GET_VERIFICATION_CODE);
        requestInfo.setData(userId);
        Map<String, String> map = new HashMap<String, String>();
        if (!TextUtils.isEmpty(phoneNumber)) {
            map.put(RequestField.PHONENUMBER, phoneNumber);
        }
        mProxy.enqueue(mServer.getSMSCode(map), getContext(), requestInfo,
                this, listener);
    }

    public void sendModify(String userId, String msgId, String groupId, String personId,
                           ResponseListener listener) {
        Request requestInfo = new Request();
        requestInfo.setRequestId(CallbackIds.SEND_MODIFY);
        requestInfo.setData(userId);
        Map<String, String> map = new HashMap<String, String>();
        if (!TextUtils.isEmpty(groupId)) {
            map.put(RequestField.GROUP_ID, groupId);
        }
        if (!TextUtils.isEmpty(personId)) {
            map.put(RequestField.PERSON_ID, personId);
        }
        mProxy.enqueue(mServer.sendModify(msgId, map), getContext(), requestInfo,
                this, listener);
    }

    public void createTrade(String userId, String userName, ResponseListener listener) {
        Request requestInfo = new Request();
        requestInfo.setRequestId(CallbackIds.SEND_MODIFY);
        requestInfo.setData(userId);
        PostResp resp = new PostResp(userId,userName);
        mProxy.enqueue(mServer.createTrade(resp), getContext(), requestInfo,
                this, listener);
    }

    public void sendSMS(String userId, ResponseListener listener) {
        Request requestInfo = new Request();
        requestInfo.setRequestId(CallbackIds.SEND_SMS);
        requestInfo.setData(userId);
        Map<String, String> map = new HashMap<String, String>();
        mProxy.enqueue(mServer.sendSMS(map), getContext(), requestInfo,
                this, listener);
    }

    @Override
    public void onResponse(Response response) {
        Request request = response.getRequestInfo();
        switch (request.getRequestId()) {
            case CallbackIds.CREATE_TRADE: {
                CustomRequestData data = (CustomRequestData) request.getData();
                if (!data.isSave()) {
                    return;
                }
                ModelResp resp = (ModelResp) response.getData();
                if (resp == null || resp.getCode() != BaseResp.OK) {
                    return;
                }
                String userId = data.getUserId();
                String type = data.getSaveType();
                String fileName = getBookFileName(type);
                FileUtils.saveContentToCacheInJson(getContext(), userId, fileName, resp);
                break;
            }
        }
    }

    public static class CustomRequestData {
        String userId;
        String saveType;
        boolean isSave;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSaveType() {
            return saveType;
        }

        public void setSaveType(String saveType) {
            this.saveType = saveType;
        }

        public boolean isSave() {
            return isSave;
        }

        public void setSave(boolean save) {
            isSave = save;
        }
    }
}
