/*
 * 文件名: OkHttpUtil.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2016-7-18
 */
package com.yyh.okhttpretrofit.logic.transport.http;

import android.util.Log;

import com.yyh.okhttpretrofit.logic.transport.ProgressUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 基于okhttp2的http操作封装
 *
 * @author Kelvin Van
 */
public class OkHttpUtil {

    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_VALUE_ANYTHING = "*/*";
    public static final String TAG = "OkHttpUtil";

    private static final HashMap<String, String> COMMON_HEADERS = new HashMap<String, String>();
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final ThreadLocal<Integer> REQUEST_MARKER = new ThreadLocal<Integer>();

    private static OkHttpClient sOkHttpClient;
    private static OkHttpClient.Builder mBuilder;
    private static long sConnectTimeout = 30000;
    private static long sReadTimeout = 30000;
    private static long sWriteTimeout = 30000;
    private static String sUserAgent;

    private static OkHttpClient initDefault() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(sConnectTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(sReadTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(sWriteTimeout, TimeUnit.MILLISECONDS);
        builder.addInterceptor(new MarkInterceptor());
        mBuilder = builder;
        return builder.build();
    }

   /* public static void setConnectTimeout(long timeoutMS) {
        sConnectTimeout = timeoutMS;
        if (mBuilder != null) {
            mBuilder.connectTimeout(timeoutMS, TimeUnit.MILLISECONDS);
        }
    }*/

   /* public static void setReadTimeout(long timeoutMS) {
        sReadTimeout = timeoutMS;
        if (mBuilder != null) {
            mBuilder.readTimeout(timeoutMS, TimeUnit.MILLISECONDS);
        }
    }*/

   /* public static void setWriteTimeout(long timeoutMS) {
        sWriteTimeout = timeoutMS;
        if (mBuilder != null) {
            mBuilder.writeTimeout(timeoutMS, TimeUnit.MILLISECONDS);
        }
    }*/

    private static OkHttpClient get() {
        if (sOkHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (sOkHttpClient == null) {
                    sOkHttpClient = initDefault();
                }
            }
        }
        return sOkHttpClient;
    }

    public static Integer getRequestMarker() {
        return REQUEST_MARKER.get();
    }

    private static Call newCall(String url, String method, Map<String, String> headers, boolean useGzip,
                                RequestBody body) {
        Request.Builder builder = new Request.Builder();
        builder.url(url).method(method, body);

        if (sUserAgent != null) {
            builder.header("User-Agent", sUserAgent);
        }
        if (useGzip) {
            builder.header("Accept-Encoding", "gzip, deflate");
        } else {
            builder.header(ACCEPT, ACCEPT_VALUE_ANYTHING);
        }
        if (!COMMON_HEADERS.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = COMMON_HEADERS.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.build();
        OkHttpClient client = get();
        return client.newCall(request);
    }

    public static Response execute(String url, String method, Map<String, String> headers, boolean useGzip,
                                   RequestBody body) throws IOException {
        return newCall(url, method, headers, useGzip, body).execute();
    }

    public static Call enqueue(String url, String method, Map<String, String> headers, boolean useGzip,
                               RequestBody body, Callback callback) {
        Call call = newCall(url, method, headers, useGzip, body);
        call.enqueue(callback);
        return call;
    }

   /* public static synchronized void addCommonHeader(String headerKey, String headerValue) {
        COMMON_HEADERS.put(headerKey, headerValue);
    }*/

//    public static synchronized void removeCommonHeader(String headerKey) {
//        COMMON_HEADERS.remove(headerKey);
//    }

//    public static void setUserAgent(String userAgent) {
//        sUserAgent = userAgent;
//    }

    /*public static class ProgressiveResponseBody extends ResponseBody {

        private ResponseBody mResponseBody;
        private ProgressUpdateListener mProgressUpdateListener;
        private BufferedSource mSource;

        public ProgressiveResponseBody(ResponseBody responseBody, ProgressUpdateListener progressUpdateListener) {
            super();
            this.mResponseBody = responseBody;
            this.mProgressUpdateListener = progressUpdateListener;
        }

        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (mSource == null) {
                mSource = Okio.buffer(new ProgressiveSource(mResponseBody.source()));
            }
            return mSource;
        }

        class ProgressiveSource extends ForwardingSource {

            private long mBytesRead;

            ProgressiveSource(Source delegate) {
                super(delegate);
            }

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long ret = super.read(sink, byteCount);
                mBytesRead += ret != -1 ? ret : 0;
                mProgressUpdateListener.onProgressUpdated(mBytesRead, (int) ret, mResponseBody.contentLength());
                return ret;
            }
        }
    }*/

    public static class ProgressiveRequestBody extends RequestBody {

        private RequestBody mRequestBody;
        private ProgressUpdateListener mProgressUpdateListener;
        private BufferedSink mSink;

        public ProgressiveRequestBody(RequestBody requestBody, ProgressUpdateListener progressUpdateListener) {
            super();
            this.mRequestBody = requestBody;
            this.mProgressUpdateListener = progressUpdateListener;
        }

        @Override
        public MediaType contentType() {
            return mRequestBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return mRequestBody.contentLength();
        }

        @Override
        public void writeTo(BufferedSink bufferedSink) throws IOException {
            if (mSink == null) {
                mSink = Okio.buffer(new ProgressiveSink(bufferedSink));
            }
            mRequestBody.writeTo(mSink);
            mSink.flush();
        }

        class ProgressiveSink extends ForwardingSink {

            private long mBytesWritten;
            private long mContentLength;

            ProgressiveSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (mContentLength == 0) {
                    mContentLength = contentLength();
                }
                mBytesWritten += byteCount;
                mProgressUpdateListener.onProgressUpdated(mBytesWritten, (int) byteCount, mContentLength);
            }
        }
    }

    private static class MarkInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            int counter = COUNTER.incrementAndGet();
            REQUEST_MARKER.set(counter);
            Request request = chain.request();
            String url = request.url().toString();
            Log.i(TAG, String.format("%s %s(%d)", request.method(), url, counter));
            Response response = chain.proceed(request);
            if (response != null) {
                Log.i(TAG, String.format("Response-Code(%d): %d, URI: %s", counter, response.code(), url));
            }
            return response;
        }
    }
}