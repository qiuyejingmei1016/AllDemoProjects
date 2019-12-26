package com.yyh.mediarecorderdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/6/11.
 */

public class UIAction {

    public static void setLandscape(Activity activity) {
        if (Build.VERSION.SDK_INT >= 9) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    /**
     * 关闭所有可关闭的流
     *
     * @param closeable 可关闭的流
     */
    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }

    /**
     * 设置媒体不可扫描标志
     *
     * @param directory 要加标志的目录
     * @return 是否设置成功
     */
    public static boolean setNoMediaFlag(File directory) {
        try {
            File noMediaFile = new File(directory, ".nomedia");
            return noMediaFile.exists() || noMediaFile.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }
}
