package com.yyh.okhttpretrofit.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.yyh.okhttpretrofit.BuildConfig;
import com.yyh.okhttpretrofit.logic.transport.GsonFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @describe: 文件操作工具类
 * @author: yyh
 * @createTime: 2018/5/18 15:45
 * @className: FileUtils
 */
public class FileUtils {

    public static final String TAG = "Utility";

    public static File getUserCacheDir(Context context, String userId) {
        File cacheDir = context.getCacheDir();
        File userCacheDir = new File(cacheDir, userId);
        if (!userCacheDir.exists()) {
            userCacheDir.mkdirs();
        }
        return userCacheDir;
    }

    public static boolean saveContentToCacheInJson(Context context, String userId, String fileName, Object content) {
        File userCacheDir = getUserCacheDir(context, userId);
        File saveFile = new File(userCacheDir, fileName);
        File saveFileTmp = new File(userCacheDir, fileName + ".tmp");
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(saveFileTmp, false);
            bw = new BufferedWriter(fw);
            Gson gson = GsonFactory.newGson();
            gson.toJson(content, bw);
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "saveContentToCacheInJson " + fileName + " ok");
            }
            bw.flush();
            bw.close();
            if (!saveFileTmp.renameTo(saveFile)) {
                Log.e(TAG, "saveContentToCacheInJson renameTo failed");
                return false;
            }
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "saveContentToCacheInJson error", ex);
            return false;
        } finally {
            if (bw == null) {
                return false;
            }
            try {
                bw.close();
            } catch (IOException e) {
            }
        }
    }

}
