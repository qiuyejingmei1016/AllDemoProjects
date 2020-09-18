package com.newrecord.cloud.utils.net;

import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.newrecord.cloud.utils.LogUtils;
import com.newrecord.cloud.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @describe: 网络请求封装类
 * @author: yyh
 * @createTime: 2019/5/21 15:33
 * @className: OkHttpUtils
 */
public class OkHttpUtils {

    private static OkHttpUtils mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;
    private Handler mHandler;

    private OkHttpUtils() {
//        mHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
        TrustManager[] trustManagers = new TrustManager[]{trustManager};
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        mHttpClient = builder.build();

        mGson = new Gson();
        mHandler = new Handler();
    }

    public static OkHttpUtils getInstance() {
        if (null == mInstance) {
            synchronized (OkHttpUtils.class) {
                if (null == mInstance) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * get请求
     *
     * @param url      请求地址
     * @param callback
     */
    public void get(String url, BaseCallback callback) {
        Request request = buildRequest(url, HttpMethodType.GET, null);
        doRequest(request, callback);
    }

    /**
     * post请求 （提交表单方式）
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback
     */
    public void post(String url, Map<String, String> params, BaseCallback callback) {
        Request request = buildRequest(url, HttpMethodType.POST, params);
        doRequest(request, callback);
    }


    private MediaType mMediaType = MediaType.parse("application/json;charset=UTF-8");

    /**
     * post请求 （提交json方式)
     *
     * @param url      请求地址
     * @param json     请求参数json字符串
     * @param callback
     */
    public void post(String url, String json, BaseCallback callback) {
        RequestBody body = RequestBody.create(mMediaType, json);
        Request request = new Request.Builder().url(url).post(body).build();
        doRequest(request, callback);
    }

    /**
     * 构建request对象
     *
     * @param url        请求地址
     * @param methodType 请求方式
     * @param params     请求参数
     * @return
     */
    private Request buildRequest(String url, HttpMethodType methodType, Map<String, String> params) {
        Request.Builder builder = new Request.Builder().url(url);
        if (methodType == HttpMethodType.POST) {// post请求
            RequestBody body = builderFormData(params);
            builder.post(body);
        } else if (methodType == HttpMethodType.GET) {// get请求
            builder.get();
        } else if (methodType == HttpMethodType.UPLOAD_FILE) {// 上传文件

        }
        return builder.build();
    }

    /**
     * 通过params获取requestbody
     *
     * @param params 请求参数
     * @return
     */
    private RequestBody builderFormData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 无论是post请求还是get请求都需要用到request
     *
     * @param request
     * @param baseCallback
     */
    private void doRequest(final Request request, final BaseCallback baseCallback) {
        LogUtils.e("===HTTP Requset===", request.toString());
        if (LogUtils.isDeBug) {
            if (!StringUtil.isNullOrEmpty(request.toString())) {
                String method = request.method();
                if ("POST".equals(method.toUpperCase())) {
                    RequestBody body = request.body();
                    if (request.body() instanceof FormBody) {
                        StringBuilder sb = new StringBuilder();
                        FormBody formBody = (FormBody) request.body();
                        for (int i = 0; i < formBody.size(); i++) {
                            sb.append(formBody.encodedName(i) + "=" + formBody.encodedValue(i) + ",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                        LogUtils.e("===Request body===", sb.toString());
                    } else {
                        LogUtils.e("===Request body===", bodyToString(body));
                    }
                }
            }
        }

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(baseCallback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultStr = response.body().string();
                    if (baseCallback.mType == String.class) {
                        /* baseCallback.onSuccess(response,resultStr); */
                        callbackSuccess(baseCallback, resultStr);
                    } else {
                        try {
                            Object obj = mGson.fromJson(resultStr, baseCallback.mType);
                            // Object obj = resultStr;
                            /* baseCallback.onSuccess(response,obj); */
                            callbackSuccess(baseCallback, obj);
                        } catch (com.google.gson.JsonParseException e) { // Json解析的错误
                            /* baseCallback.onError(response,response.code(),e); */
                            callbackFailure(baseCallback, e);
                        }
                    }
                } else {
                    callbackFailure(baseCallback, new Exception("errorcode=" + response.code()));
                    /* baseCallback.onError(response,response.code(),null); */
                }
            }
        });
    }

    private void callbackSuccess(final BaseCallback callback, final Object obj) {
        if (LogUtils.isDeBug) {
            if (obj != null && !StringUtil.isNullOrEmpty(obj.toString())) {
                LogUtils.e("===HTTP Response===", obj.toString());
            }
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(obj);
            }
        });
    }

    private void callbackFailure(final BaseCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {// mHandler在哪个线程new的，run就在那个线程运行
                callback.onFailure(e);
            }
        });
    }

    // 回调
    public static abstract class BaseCallback<T> {
        Type mType = getSuperclassTypeParameter(getClass());

        // // 加载网络数据成功前，进度条等显示
        // public abstract void onBeforeRequest(Request request);
        // 请求成功时调用此方法
        // public abstract void onResponse(Response response);
        // 状态码大于200，小于300 时调用此方法
        public abstract void onSuccess(T t);

        // 请求失败时调用此方法
        public abstract void onFailure(Exception e);

        // 状态码400，404，403，500等时调用此方法
        // public abstract void onError(Response response, int code, Exception
        // e);

        // Token 验证失败。状态码401,402,403 等时调用此方法
        // public abstract void onTokenError(Response response, int code);
    }

    // 枚举，区分get与post
    enum HttpMethodType {
        GET, POST, UPLOAD_FILE
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * post请求参数相关 用于打印post请求的参数方便查看
     *
     * @param request
     * @return
     */
    public String bodyToString(final RequestBody request) {
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