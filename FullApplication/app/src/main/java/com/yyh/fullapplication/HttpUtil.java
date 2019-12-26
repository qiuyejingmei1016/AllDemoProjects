package com.yyh.fullapplication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okio.Buffer;

/**
 * @describe: http网络请求类
 * @author: yyh
 * @createTime: 2018/5/18 9:35
 * @className: HttpUtil
 */
public class HttpUtil {

    private static HttpUtil mHttpUtilInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    @SuppressLint("NewApi")
    private HttpUtil() {

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());


            mOkHttpClient = new OkHttpClient();
            mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            mOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
            mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
            mOkHttpClient.setSslSocketFactory(sc.getSocketFactory());
            mOkHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            mHandler = new Handler(Looper.getMainLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过单例模式构造对象
     *
     * @return AnyChatHttpUtil
     */
    private static HttpUtil getInstance() {
        if (mHttpUtilInstance == null) {
            synchronized (HttpUtil.class) {
                if (mHttpUtilInstance == null) {
                    mHttpUtilInstance = new HttpUtil();
                }
            }
        }
        return mHttpUtilInstance;
    }

    /**
     * 构造Get请求
     *
     * @param url      请求的url
     * @param callback 结果回调的方法
     */
    @SuppressWarnings("rawtypes")
    private void getRequest(String url, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        call(callback, request);
    }

    /**
     * 构造Get请求
     *
     * @param headers  请求头
     * @param url      请求的url
     * @param callback 结果回调的方法
     */
    @SuppressWarnings("rawtypes")
    private void getRequest(String url, List<ParamPair> headers, final ResultCallback callback) {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0) {
            for (ParamPair header : headers) {
                builder.addHeader(header.key, header.value);
            }
        }
        final Request request = builder.build();
        call(callback, request);
    }

    /**
     * 构造post 请求
     *
     * @param url      请求的url
     * @param callback 结果回调的方法
     * @param params   请求参数
     */
    @SuppressWarnings("rawtypes")
    private void postRequest(String url, final ResultCallback callback, List<ParamPair> params) {
        Request request = buildPostRequest(url, params);
        call(callback, request);
    }

    /**
     * 构造post 请求
     *
     * @param url      请求url
     * @param callback 结果回调
     * @param headers  请求头
     * @param params   请求参数
     */
    @SuppressWarnings("rawtypes")
    private void postRequest(String url, final ResultCallback callback, List<ParamPair> headers, List<ParamPair> params) {
        Request request = buildPostRequest(url, headers, params);
        call(callback, request);
    }

    /**
     * 处理请求结果的回调
     *
     * @param callback
     * @param request
     */
    @SuppressWarnings("rawtypes")
    private void call(final ResultCallback callback, Request request) {
//        if (BuildConfig.DEBUG) {
            // TODO: 2018/5/18 (网络请求)
            String urlString = request.urlString();
            Log.d("HTTP", "=========== HTTP REQUEST ==========="
                    + (TextUtils.isEmpty(request.method()) ? "" : (request.method() + "   "))
                    + urlString + "\n");
            String method = request.method();
            if ("POST".equals(method)) {
                Log.d("HTTP", "=========request body:\n" + bodyToString(request.body()));
            }
//        }

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                dispatchFailEvent(callback, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String str = response.body().string();
                    dispatchSuccessEvent(callback, str);
                    if (BuildConfig.DEBUG) {
                        // TODO: 2018/5/18 (网络请求响应)
                        Log.d("HTTP", "========== HTTP RESPONSE ==========" + "response  body:\n" + str);
                    }
                } catch (final Exception e) {
                    dispatchFailEvent(callback, e);
                }
            }
        });
    }

    /**
     * 发送失败的回调
     *
     * @param callback
     * @param e
     */
    @SuppressWarnings("rawtypes")
    private void dispatchFailEvent(final ResultCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * 发送成功的调
     *
     * @param callback
     * @param obj
     */
    @SuppressWarnings("rawtypes")
    private void dispatchSuccessEvent(final ResultCallback callback, final Object obj) {
        mHandler.post(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(obj);
                }
            }
        });
    }

    /**
     * 构造post请求
     *
     * @param url    请求url
     * @param params 请求的参数
     * @return 返回 Request
     */
    private Request buildPostRequest(String url, List<ParamPair> params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params != null && params.size() > 0) {
            for (ParamPair param : params) {
                builder.add(param.key, param.value);
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * 构造post请求
     *
     * @param url     请求url
     * @param headers 请求头
     * @param params  请求的参数
     * @return 返回 Request
     */
    private Request buildPostRequest(String url, List<ParamPair> headers, List<ParamPair> params) {
        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        if (params != null && params.size() > 0) {
            for (ParamPair param : params) {
                formBuilder.add(param.key, param.value);
            }
        }
        RequestBody requestBody = formBuilder.build();
        com.squareup.okhttp.Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0) {
            for (ParamPair header : headers) {
                builder.addHeader(header.key, header.value);
            }
        }
        Request request = builder.post(requestBody).build();
        return request;
    }


    /**
     * get请求
     *
     * @param url      请求url
     * @param callback 请求回调
     */
    @SuppressWarnings("rawtypes")
    public static void get(String url, ResultCallback callback) {
        getInstance().getRequest(url, callback);
    }

    /**
     * get请求
     *
     * @param url      请求url
     * @param headers  请求头
     * @param callback 请求回调
     */
    @SuppressWarnings("rawtypes")
    public static void get(String url, List<ParamPair> headers, ResultCallback callback) {
        getInstance().getRequest(url, headers, callback);
    }


    /**
     * post请求
     *
     * @param url      请求url
     * @param callback 请求回调
     * @param params   请求参数
     */
    @SuppressWarnings("rawtypes")
    public static void post(String url, final ResultCallback callback, List<ParamPair> params) {
        getInstance().postRequest(url, callback, params);
    }

    @SuppressWarnings("rawtypes")
    public static void post(String url, final ResultCallback callback, List<ParamPair> headers, List<ParamPair> params) {
        getInstance().postRequest(url, callback, headers, params);
    }

    /**
     * http请求回调类,回调方法在UI线程中执行
     *
     * @param <T>
     */
    public static abstract class ResultCallback<T> {

        /**
         * 请求成功回调
         *
         * @param response
         */
        public abstract void onSuccess(T response);

        /**
         * 请求失败回调
         *
         * @param e
         */
        public abstract void onFailure(Exception e);
    }

    /**
     * post请求参数类
     */
    public static class ParamPair {

        public String key;//请求的参数
        public String value;//参数的值

        public ParamPair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private static String bodyToString(final RequestBody request) {
        try {
            RequestBody copy = request;
            Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}