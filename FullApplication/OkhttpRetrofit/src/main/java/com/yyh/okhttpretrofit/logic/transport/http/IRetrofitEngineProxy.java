package com.yyh.okhttpretrofit.logic.transport.http;

import android.content.Context;

import com.yyh.okhttpretrofit.logic.transport.base.Response;
import com.yyh.okhttpretrofit.logic.transport.base.Request;

import java.util.Map;

import retrofit2.Call;

public interface IRetrofitEngineProxy {
    <T> T createHttpServer(String url, Map<String, String> headers, Class<T> service);

    <T> T execute(Call<T> t);

    <T> void enqueue(Call<T> call, Context context, Request request, Response.ResponseListener mainListener, Response.ResponseListener listener);

    void setDebug(boolean isDebug);
}
