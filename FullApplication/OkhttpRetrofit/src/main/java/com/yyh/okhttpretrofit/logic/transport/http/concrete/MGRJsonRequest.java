package com.yyh.okhttpretrofit.logic.transport.http.concrete;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yyh.okhttpretrofit.logic.transport.base.HttpErrorCode;
import com.yyh.okhttpretrofit.logic.transport.base.Request;
import com.yyh.okhttpretrofit.logic.transport.base.Response.ResponseListener;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  @describe: Retrofit 请求管理类
 *  @author: yyh
 *  @createTime: 2018/5/18 14:53
 *  @className:  MGRJsonRequest
 */
public class MGRJsonRequest<T> implements Callback<T> {

    public static final String TAG = "Rhttp";
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private Context mContext;
    protected ResponseListener mListener;
    protected Request mRequestInfoData;

    public static RequestBody getRequestBody(String content) {
        return RequestBody.create(okhttp3.MediaType.parse(CONTENT_TYPE), content);
    }

    public MGRJsonRequest(Context context, Request request, ResponseListener listener) {
        this.mContext = context;
        this.mRequestInfoData = request;
        this.mListener = listener;
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        Log.d(TAG, throwable.toString());
        if (mListener == null) {
            return;
        }
        int code;
        if (throwable instanceof UnknownHostException) {
            ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo active = null;
            if (connMgr != null) {
                active = connMgr.getActiveNetworkInfo();
            }
            if (active == null || !active.isConnectedOrConnecting()) {
                code = HttpErrorCode.NO_NETWORK;
            } else {
                code = HttpErrorCode.NETWORK_EXCEPTION;
            }
        } else if (throwable instanceof ConnectException) {
            code = HttpErrorCode.NETWORK_BROKEN;
        } else if (throwable instanceof TimeoutException) {
            code = HttpErrorCode.NETWORK_TIMEOUT;
        } else {
            code = HttpErrorCode.ACTION_FAILED;
        }
        com.yyh.okhttpretrofit.logic.transport.base.Response response = new com.yyh.okhttpretrofit.logic.transport.base.Response();
        response.setBusinessCode(com.yyh.okhttpretrofit.logic.transport.base.Response.BUSINESS_CODE_NOK);
        response.setCode(code);
        if (mRequestInfoData != null) {
            response.setRequestInfo(mRequestInfoData);
        }
        mListener.onResponse(response);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response == null) {
            return;
        }
        T t = response.body();
        com.yyh.okhttpretrofit.logic.transport.base.Response info;
        if (t == null) {
            if (mListener != null) {
                info = new com.yyh.okhttpretrofit.logic.transport.base.Response();
                info.setBusinessCode(com.yyh.okhttpretrofit.logic.transport.base.Response.BUSINESS_CODE_NOK);
                info.setRequestInfo(mRequestInfoData);
                mListener.onResponse(info);
            }
            return;
        }
        if (mListener == null) {
            return;
        }
        info = new com.yyh.okhttpretrofit.logic.transport.base.Response();
        info.setBusinessCode(com.yyh.okhttpretrofit.logic.transport.base.Response.BUSINESS_CODE_OK);
        info.setData(t);
        if (mRequestInfoData != null) {
            info.setRequestInfo(mRequestInfoData);
        }
        mListener.onResponse(info);
    }

}
