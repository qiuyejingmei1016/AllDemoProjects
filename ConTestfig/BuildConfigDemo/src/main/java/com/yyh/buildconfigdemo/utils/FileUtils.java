package com.yyh.buildconfigdemo.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @describe: sd卡文件工具类
 * @author: yyh
 * @createTime: 2018/5/17 10:38
 * @className: FileUtils
 */
public class FileUtils {

    /**
     * 判断sdcrad是否已经安装
     *
     * @return boolean true安装 false 未安装
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 得到sdcard的路径
     *
     * @return
     */
    public static String getSDCardRoot() {
        System.out.println(isSDCardMounted() + Environment.getExternalStorageState());
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * 创建文件路径及文件
     *
     * @param path     路径，方法中以默认包含了sdcard的路径，path格式是"/path...."
     * @param filename 文件的名称
     * @return 返回文件的路径，创建失败的话返回为空
     */
    public static String createMkdirsAndFiles(String path, String filename) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File file = new File(getSDCardRoot(), path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new RuntimeException("创建文件夹不成功");
            }
        }
        File f = new File(file, filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("创建文件不成功");
            }
        }
        return f.getAbsolutePath();
    }

    /**
     * 创建文件路径及文件
     *
     * @param path     文件路径
     * @param filename 文件名
     * @return
     */
    public static String createDirsAndFiles(String path, String filename) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File file = new File(new File(Environment.getExternalStorageDirectory(), path), "crashlog");
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new RuntimeException("创建文件夹不成功");
            }
        }
        File f = new File(file, filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("创建文件不成功");
            }
        }
        return f.getAbsolutePath();
    }

    /**
     * 创建文件路径及文件
     *
     * @param path 文件路径
     * @return
     */
    public static String createDirsAndFiles(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File logFile = getLogFile(path);
        if (!logFile.exists()) {
            try {
                File dir = logFile.getParentFile();
                dir.mkdirs();
            } catch (Exception e) {
                throw new RuntimeException("创建文件夹不成功");
            }
        }
        return logFile.getAbsolutePath();
    }

    public static File getLogFile(String userDataDirPath) {
        return new File(new File(new File(Environment.getExternalStorageDirectory(), userDataDirPath),
                "crashlog"), "log.txt");
    }

    /**
     * 把内容写入文件
     *
     * @param path 文件路径
     * @param text 内容
     */
    public static void write2File(String path, String text, boolean append) {
        BufferedWriter bw = null;
        try {
            //1.创建流对象
            bw = new BufferedWriter(new FileWriter(path, append));
            //2.写入文件
            bw.write(text);
            //换行刷新
            bw.newLine();
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭流资源
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
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路径为空");
        }
        File file = new File(path);
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

}
