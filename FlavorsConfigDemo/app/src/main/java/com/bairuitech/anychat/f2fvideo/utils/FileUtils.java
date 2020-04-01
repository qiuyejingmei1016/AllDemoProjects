package com.bairuitech.anychat.f2fvideo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @describe: sd卡文件操作工具类
 * @author: yyh
 * @createTime: 2018/5/17 10:38
 * @className: RecruitFileUtils
 */
public class FileUtils {
    /**
     * 判断sdcrad是否已经安装
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 根据文件路径判断文件是否存在
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean isFileExists(Context context, String filePath) {
        if (EmptyUtils.isNullOrEmpty(filePath)) {
            UIUtils.makeToast(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            UIUtils.makeToast(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取sdcard路径
     */
    public static String getSDCardRoot() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * 获取缓存地址
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 保存bitmap到本地
     */
    public static String saveBitmap(String savePath, String name, Bitmap bitmap) {
        if (EmptyUtils.isNullOrEmpty(savePath)) {
            savePath = getSDCardRoot();
        }
        File file = new File(savePath, "AnyChat/signature");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (EmptyUtils.isNullOrEmpty(name)) {
            name = String.valueOf(System.currentTimeMillis());
        }
        File filePic = new File(file, name + ".png");
        if (filePic.exists()) {
            return filePic.getAbsolutePath();
        }
        try {
            if (!filePic.exists()) {
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    /**
     * 创建文件路径及文件
     */
    public static String createMkdirsAndFiles(String path, String filename) {
        if (EmptyUtils.isNullOrEmpty(path)) {
            return "";
        }
        File file = new File(getSDCardRoot(), path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File f = new File(file, filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getAbsolutePath();
    }

    /**
     * 把日志内容写入文件
     */
    public static void write2File(String path, String text, boolean append) {
        BufferedWriter bw = null;
        try {
            //创建流对象
            bw = new BufferedWriter(new FileWriter(path, append));
            //写入文件
            bw.write(text);
            //换行刷新
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流资源
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件(初始化时清空之前的日志信息)
     */
    public static boolean deleteLogFile(String fileName) {
        String path = getSDCardRoot() + LogTools.DIRPATH;
        if (EmptyUtils.isNullOrEmpty(fileName) || EmptyUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path, fileName);
        if (file.exists()) {
            try {
                file.delete();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取单个文件的MD5值
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}