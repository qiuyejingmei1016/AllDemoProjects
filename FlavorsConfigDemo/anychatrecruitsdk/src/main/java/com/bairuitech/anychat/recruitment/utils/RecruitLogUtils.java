package com.bairuitech.anychat.recruitment.utils;

import android.text.format.DateFormat;
import android.util.Log;

import com.bairuitech.anychat.recruitment.BuildConfig;

/**
 * @describe: 日志管理工具
 * @author: yyh
 * @createTime: 2018/5/17 10:41
 * @className: RecruitLogUtils
 */
public class RecruitLogUtils {

    private static final String TAG = "===yyh===";

    private static final boolean isDeBug = BuildConfig.DEBUG;

    //用于开关是否把日志写入文件
    private static final boolean isWrite = true;

    //存放日志文件的所在路径
    public static final String DIRPATH = "/AnyChat/AnyChatRecruitLog";

    //存放日志的文本名
    public static final String LOGNAME = "log.txt";

    //设置时间的格式
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void v(String tag, String msg) {
        // 是否开启日志输出
        if (isDeBug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDeBug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDeBug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
    }

    /**
     * 截断输出日志
     */
    public static void e(String tag, String msg) {
        if (msg == null || msg.length() == 0) {
            return;
        }
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            if (isDeBug) {
                Log.e(TAG + tag, msg);
            }
            if (isWrite) {
                write(tag, msg);
            }
        } else {
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                if (isDeBug) {
                    Log.e(TAG + tag, logContent);
                }
                if (isWrite) {
                    write(tag, msg);
                }
            }
            if (isDeBug) {
                Log.e(TAG + tag, msg);// 打印剩余日志
            }
            if (isWrite) {
                write(tag, msg);
            }
        }
    }

    /**
     * 用于把日志内容写入制定的文件
     */
    public static void write(String tag, String msg) {
        String path = RecruitFileUtils.createMkdirsAndFiles(DIRPATH, LOGNAME);
        if (StringUtil.isNullOrEmpty(path)) {
            return;
        }
        String log = DateFormat.format(DATE_FORMAT, System.currentTimeMillis())
                + tag + "===>>"
                + msg
                + "\n============end============\n";
        RecruitFileUtils.write2File(path, log, true);
    }
}