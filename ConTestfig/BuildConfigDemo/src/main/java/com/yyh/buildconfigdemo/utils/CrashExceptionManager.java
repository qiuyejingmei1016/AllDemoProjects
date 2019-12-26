package com.yyh.buildconfigdemo.utils;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *  @describe: 异常上报管理类
 *  @author: yyh
 *  @createTime: 2018/5/17 16:34
 *  @className:  CrashExceptionManager
 */
@SuppressWarnings("deprecation")
public class CrashExceptionManager implements UncaughtExceptionHandler {
    private Context mContext = null;
    private UncaughtExceptionHandler mDefaultHandler = null;

    public CrashExceptionManager(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public int postExceptionToServer(Throwable ex) {
        // 获取异常信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();

        HttpResponse response = null;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpParams, 1000 * 30);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpPost httpPost = new HttpPost("http://api.news.kkyouxi.cn/report/read");
        try {
            List params = new ArrayList();
            params.add(new BasicNameValuePair("imei", ""));
            params.add(new BasicNameValuePair("mac", ""));
            params.add(new BasicNameValuePair("model", ""));
            params.add(new BasicNameValuePair("sys_version", ""));
            params.add(new BasicNameValuePair("app_status", ""));
            params.add(new BasicNameValuePair("error_code", "500"));
            params.add(new BasicNameValuePair("packagename", ""));
            params.add(new BasicNameValuePair("msg", result));

            HttpEntity entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == response) {
            return 0;
        }
        if (200 == response.getStatusLine().getStatusCode()) {
            byte[] mResultXml = null;
            try {
                mResultXml = EntityUtils.toByteArray(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String versionStrXml = new String(mResultXml, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return 200;
        }
        return 0;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        if (null == ex && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    while (postExceptionToServer(ex) == 0) ;
//                     退出程序
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }).start();
        }
    }
}
