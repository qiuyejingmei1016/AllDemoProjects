package com.yyh.okhttpretrofit.logic.transport.http.concrete;

import android.content.Context;

import com.yyh.okhttpretrofit.logic.transport.base.Request;
import com.yyh.okhttpretrofit.logic.transport.base.Response.ResponseListener;
import com.yyh.okhttpretrofit.logic.transport.base.WrapperResponseListener;
import com.yyh.okhttpretrofit.logic.transport.http.IRetrofitEngineProxy;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;

import retrofit2.Response;

/**
 * @describe:
 * @author: yyh
 * @createTime: 2018/5/18 15:01
 * @className: RetrofitEngineProxy
 */
public class RetrofitEngineProxy implements IRetrofitEngineProxy {
    private boolean mIsDebug = false;

    @Override
    public <T> T createHttpServer(String url, Map<String, String> headers, Class<T> service) {
        RetrofitWrapper wrapper = RetrofitWrapper.getInstance(url, headers, mIsDebug);
        return wrapper.create(service);
    }

    @Override
    public <T> T execute(Call<T> t) {
        if (t != null) {
            try {
                Response<T> response = t.execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> void enqueue(Call<T> call, Context context, Request request, ResponseListener mainListener, ResponseListener listener) {
        MGRJsonRequest<T> callback;
        if (mainListener != null) {
            callback = new MGRJsonRequest<T>(context, request, new WrapperResponseListener(mainListener, listener));
        } else {
            callback = new MGRJsonRequest<T>(context, request, listener);
        }
        call.enqueue(callback);
    }

    @Override
    public void setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
    }
}
