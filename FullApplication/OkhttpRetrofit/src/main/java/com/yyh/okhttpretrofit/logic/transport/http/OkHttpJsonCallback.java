package com.yyh.okhttpretrofit.logic.transport.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yyh.okhttpretrofit.logic.transport.GsonFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 基于okhttp的json解析
 */
public class OkHttpJsonCallback implements Callback {

    public static final String TAG_EVENT = "OkHttpJson";
    private static final String TAG = "OkHttpJsonCallback";
    private static boolean LOG_DEBUG = true;

    private Class<?> mDeserializeClz;
    private int mExecCode = HttpResponseCode.EXEC_CODE_NOK;
    private Object mData;

    public OkHttpJsonCallback(Class<?> deserializeClz) {
        this.mDeserializeClz = deserializeClz;
    }

    public int getExecCode() {
        return mExecCode;
    }

    public Object getData() {
        return mData;
    }

    private static void logOK(String tag, Response response) {
        Integer marker = OkHttpUtil.getRequestMarker();
        if (marker == null) {
            marker = -1;
        }
        Log.d(tag, String.format("onResponse OK(%d), uri: %s", marker, response.request().url().toString()));
    }

    static void logError(String tag, Exception e, Response response) {
        Request request = response.request();
        int statusCode = response.code();
        Integer marker = OkHttpUtil.getRequestMarker();
        if (marker == null) {
            marker = -1;
        }

        Log.e(tag, String.format("onResponse error(%d), statusCode: %d, uri: %s, %s", marker, statusCode
                , request.url().toString(), e.toString()));
    }

    static void logErrorStatusCode(String tag, Response response) {
        Integer marker = OkHttpUtil.getRequestMarker();
        if (marker == null) {
            marker = -1;
        }
        Log.e(tag, String.format("onResponse error(%d), statusCode: %d, uri: %s", marker, response.code()
                , response.request().url().toString()));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mExecCode = HttpResponseCode.EXEC_CODE_IO_ERROR;
        Integer marker = OkHttpUtil.getRequestMarker();
        if (marker == null) {
            marker = -1;
        }

        Log.e(TAG_EVENT, String.format("onResponse error(%d), statusCode: %d, uri: %s, %s", marker, -1
                , call.request().url().toString(), e.toString()));

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int statusCode = response.code();
        if (statusCode >= 200 && statusCode <= 299 && statusCode != 204) {
            InputStream is = null;
            try {
                is = response.body().byteStream();
                // 判断是否gzip
                boolean gzipInputStream = false;
                String contentEncoding = response.header("Content-Encoding", "");
                if (contentEncoding != null && contentEncoding.contains("gzip")) {
                    gzipInputStream = true;
                }
                BufferedInputStream bis = null;
                if (!gzipInputStream) {
                    bis = new BufferedInputStream(is);
                    bis.mark(2);
                    byte[] header = new byte[2];
                    int result = bis.read(header);
                    bis.reset();
                    if (result != -1) {
                        int gzipHeader = (header[0] << 8) | header[1] & 0xFF;
                        gzipInputStream = gzipHeader == 0x8b1f;
                    }
                }

                if (gzipInputStream) {
                    if (bis != null) {
                        is = new GZIPInputStream(bis);
                    } else {
                        is = new GZIPInputStream(is);
                    }
                } else if (bis != null) {
                    is = bis;
                }

                InputStreamReader reader = new InputStreamReader(is, "UTF-8");
                Object data;
                if (LOG_DEBUG) {
                    // 调试模式
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder buf = new StringBuilder();
                    String temp;
                    while ((temp = br.readLine()) != null) {
                        buf.append(temp);
                    }
                    String json = buf.toString();
                    Log.d(TAG, String.format("Response length:%d, body:\n\t%s", json.length(), json));
                    Gson gson = GsonFactory.newGson();
                    data = gson.fromJson(json, mDeserializeClz);
                } else {
                    // 生产模式
                    Gson gson = GsonFactory.newGson();
                    data = gson.fromJson(reader, mDeserializeClz);
                }

                mData = data;
                mExecCode = HttpResponseCode.EXEC_CODE_REQUEST_SUCCESSFUL;
                logOK(TAG_EVENT, response);
            } catch (JsonSyntaxException e) {
                logError(TAG_EVENT, e, response);
                mExecCode = HttpResponseCode.EXEC_CODE_PARSE_ERROR;
            } catch (IOException e) {
                logError(TAG_EVENT, e, response);
                mExecCode = HttpResponseCode.EXEC_CODE_IO_ERROR;
            } catch (RuntimeException e) {
                logError(TAG_EVENT, e, response);
                mExecCode = HttpResponseCode.EXEC_CODE_NOK;
            } finally {
                if (is == null) {
                    return;
                }
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } else {
            logErrorStatusCode(TAG_EVENT, response);
        }
    }
}