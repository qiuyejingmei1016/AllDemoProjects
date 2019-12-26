package com.yyh.buildconfigdemo.utils;

import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 *  @describe: 文件数据清理管理工具类
 *  @author: yyh
 *  @createTime: 2018/5/17 17:04
 *  @className:  DataCleanManager
 */
public class DataCleanManager {

    /**
     * 删除指定文件目录下文件及目录
     *
     * @param filePath       文件路径 getExternalCacheDir().getAbsolutePath() + "/qipa/ImageCache"
     * @param deleteThisPath 是否删除目录
     * @return
     */
    public static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 获取目录下文件大小
     *
     * @param filePath getExternalCacheDir().getAbsolutePath() +  "/qipa/ImageCache"
     * @return
     */
    public static String getCacheDataSize(String filePath) {
        try {
            File file = new File(filePath);
            return getCacheSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    //获得文件的大小(获取目录下所有文件的大小)
    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
        return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
    }

    /**
     * 获取目录下所有文件
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
