package com.newrecord.cloud.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @describe: sd卡文件操作工具类
 * @author: yyh
 * @createTime: 2018/5/17 10:38
 * @className: FileUtils
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
        return isFileExists(context, filePath, true);
    }

    public static boolean isFileExists(Context context, String filePath, boolean isShowTip) {
        if (StringUtil.isNullOrEmpty(filePath)) {
            if (isShowTip) {
                UIAction.makeToast(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            if (isShowTip) {
                UIAction.makeToast(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
            }
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
     * 创建文件路径及文件
     */
    public static String createMkdirsAndFiles(String path, String filename) {
        if (StringUtil.isNullOrEmpty(path)) {
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
        String path = getSDCardRoot() + LogUtils.DIRPATH;
        if (StringUtil.isNullOrEmpty(fileName) || StringUtil.isNullOrEmpty(path)) {
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
     * 删除本地录像文件
     *
     * @param fileName 文件绝对路径
     */
    public static boolean deleteRecordFile(String fileName) {
        if (StringUtil.isNullOrEmpty(fileName)) {
            return false;
        }
        File file = new File(fileName);
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
     * 删除录像空文件夹
     */
    public static boolean deleteEmptyFile(String path) {
        if (StringUtil.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            try {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File chidFile : files) {
                            if (chidFile.exists() && chidFile.isDirectory()) {
                                File[] subFiles = chidFile.listFiles();
                                if (!(subFiles != null && subFiles.length > 0)) {
                                    deleteRecordFile(chidFile.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 保存bitmap到本地
     */
    public static String saveBitmap(String savePath, String name, Bitmap bitmap) {
        if (StringUtil.isNullOrEmpty(savePath)) {
            savePath = getSDCardRoot();
        }
        File file = new File(savePath, "AnyChat");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtil.isNullOrEmpty(name)) {
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
     * 复制raw文件夹中的文件到本地存储
     *
     * @param context
     * @param rawResId
     * @param path
     * @param fileName
     * @return
     */
    public static String copyRawFile2SDCard(Context context, int rawResId, String path, String fileName) {
        if (StringUtil.isNullOrEmpty(path)) {
            return "";
        }
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            try {
                pathFile.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        File file = new File(path + fileName);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}