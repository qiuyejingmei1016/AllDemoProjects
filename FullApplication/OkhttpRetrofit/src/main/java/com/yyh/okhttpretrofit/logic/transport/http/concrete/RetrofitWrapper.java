package com.yyh.okhttpretrofit.logic.transport.http.concrete;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitWrapper {

    public static final String TAG = "R_HTTP==============";
    public static final String EVENT_TAG = "R_HTTP_EVENT==========";
    private static long sConnectTimeout = 30000;
    private static long sReadTimeout = 30000;
    private static long sWriteTimeout = 30000;
    private static boolean mIsDebug = false;
    private Retrofit mRetrofit;
    private Map<String, String> mHeaders;


    public void setHeaders(Map<String, String> headers) {
        this.mHeaders = headers;
    }

    protected Map<String, String> fillHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (mHeaders != null && !mHeaders.isEmpty()) {
            headers.putAll(mHeaders);
        }
        return headers;
    }

    public RetrofitWrapper(String url, Map<String, String> headers) {
        this.mHeaders = headers;
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(sConnectTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(sReadTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(sWriteTimeout, TimeUnit.MILLISECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        builder.interceptors().add(new LogInterceptor());
        builder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder newBuilder = original.newBuilder();
                Map<String, String> headers = fillHeaders(null);
                if (headers != null && !headers.isEmpty()) {
                    Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        newBuilder.addHeader(entry.getKey(), entry.getValue());
                    }
                }
                Request request = newBuilder.build();
                return chain.proceed(request);
            }
        });
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
            builder.sslSocketFactory(sslSocketFactory, trustManager);
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
        OkHttpClient client = builder.build();
        Gson gson = new GsonBuilder().setLenient().create();
        mRetrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
    }

    public static RetrofitWrapper getInstance(String url, Map<String, String> headers, boolean isDebug) {
        mIsDebug = isDebug;
        return new RetrofitWrapper(url, headers);
    }

    public <T> T create(final Class<T> service) {
        return mRetrofit.create(service);
    }

    private static class LogInterceptor implements Interceptor {
        protected static final AtomicInteger ID_GEN = new AtomicInteger(0);

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            int id = ID_GEN.incrementAndGet();
            if (mIsDebug) {
                // TODO: 2018/5/18 (网络请求展示信息)
                Log.e(EVENT_TAG, "request id: " + id + " ,url:" + request.toString());
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
                        Log.e(TAG, "request id:" + id + ",body:\n" + sb.toString());
                    } else {
                        Log.e(TAG, "request id:" + id + ",body:\n" + bodyToString(body));
                    }
                }
            }
            Response response = chain.proceed(chain.request());
            okhttp3.MediaType mediaType = response.body().contentType();

            byte[] bytes = response.body().bytes();
            if (mIsDebug) {
                // TODO: 2018/5/18 (网络请求响应信息)
                Log.e(TAG, "response id:" + id + ", body:\n" + new String(bytes));
            }
            return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, bytes)).build();
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
