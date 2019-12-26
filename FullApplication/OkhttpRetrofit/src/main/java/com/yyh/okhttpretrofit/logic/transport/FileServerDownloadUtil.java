/*
 * 文件名: FileServerDownloadUtil.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-3-13
 *//*

package com.yyh.okhttpretrofit.transport.http;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.net.HttpURLConnection;

import cn.mashang.groups.logic.UserInfo;
import cn.mashang.groups.logic.transport.ServerUris;
import cn.mashang.groups.utils.StringUtil;

*/
/**
 * 文件服务器下载通用类
 *
 * @author Kelvin Van
 *//*

public class FileServerDownloadUtil {

    */
/**
     * 原始图片
     *//*

    public static final int IMAGE_TYPE_ORIGIN = 0;
    */
/**
     * 缩略图
     *//*

    public static final int IMAGE_TYPE_THUMB = 1;

    public static void displayImage(ImageLoaer loader, String fileId, ImageView imageView
            , int imageType) {
        loader.displayImage(resolveUri(fileId, imageType), imageView);
    }

    public static void displayImage(ImageLoader loader, String fileId, ImageView imageView
            , DisplayImageOptions options, int imageType) {
        loader.displayImage(resolveUri(fileId, imageType), imageView, options);
    }

    public static void displayImage(ImageLoader loader, String fileId, ImageView imageView
            , DisplayImageOptions options, int imageType, ImageLoadingListener listener) {
        loader.displayImage(resolveUri(fileId, imageType), imageView, options, listener);
    }

    public static void loadImage(ImageLoader loader, String fileId, DisplayImageOptions options, ImageLoadingListener listener
            , int imageType) {
        loader.loadImage(resolveUri(fileId, imageType), options, listener);
    }

    public static String resolveUri(String fileId, int imageType) {
        if (StringUtil.isNullOrEmpty(fileId)) {
            return "";
        }
        switch (imageType) {
            case IMAGE_TYPE_THUMB:
                return ServerUris.getDownloadThumbUri(fileId);
            case IMAGE_TYPE_ORIGIN:
            default:
                return ServerUris.getDownloadUri(fileId);
        }
    }

    */
/**
     * 文件服务器下载地址
     *
     * @author Kelvin Van
     *//*

    public static class FileServerImageDownloader extends BaseImageDownloader {

        private String mUserAgent;

        public FileServerImageDownloader(Context context, String userAgent, int connectTimeout, int readTimeout) {
            super(context, connectTimeout, readTimeout);
            this.mUserAgent = userAgent;
        }

//        public void setUserAgent(String userAgent) {
//            this.mUserAgent = userAgent;
//        }

        @Override
        protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
            HttpURLConnection conn = super.createConnection(url, extra);
            if (ServerUris.isDownloadUri(url)) {
                String clientId = UserInfo.get().getClientId();
                if (clientId != null) {
                    conn.addRequestProperty("ClientId", clientId);
                }
                String token = UserInfo.get().getToken();
                if (token != null) {
                    conn.addRequestProperty("tokenId", token);
                }
            }
            if (mUserAgent != null) {
                conn.addRequestProperty("User-Agent", mUserAgent);
            }
            return conn;
        }
    }

    public static class FileServerImageNameGenerator extends Md5FileNameGenerator {

        public FileServerImageNameGenerator() {
            super();
        }

        public static String fixThumbUri(String imageUri) {
            if (ServerUris.isDownloadUri(imageUri)) {
                imageUri = imageUri.substring(imageUri.lastIndexOf('/') + 1);
            }
            return imageUri;
        }

        @Override
        public String generate(String imageUri) {
            return super.generate(fixThumbUri(imageUri));
        }
    }
}*/
