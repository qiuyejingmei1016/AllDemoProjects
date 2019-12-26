package com.yyh.buildconfigdemo.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;


/**
 * @describe: 日志管理工具
 * @author: yyh
 * @createTime: 2018/5/17 10:41
 * @className: LogUtils
 */
public class LogUtils {
    //isWrite:用于开关是否吧日志写入txt文件中
    private static final boolean isWrite = true;

    //isDebug :是用来控制，是否打印日志
    // private static final boolean isDeBug = true;
    private static final boolean isDeBug = Config.LOG_DEBUG;

    //存放日志文件的所在路径
    // private static final String DIRPATH = "/log";
    private static final String DIRPATH = "/" + Config.USER_LOG_DIR;

    //存放日志的文本名
    // private static final String LOGNAME = "log.txt";
    private static final String LOGNAME = Config.LOG_FILENAME;

    //设置时间的格式
    private static final String INFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * VERBOSE日志形式的标识符
     */
    public static final int VERBOSE = 5;
    /**
     * DEBUG日志形式的标识符
     */
    public static final int DEBUG = 4;
    /**
     * INFO日志形式的标识符
     */
    public static final int INFO = 3;
    /**
     * WARN日志形式的标识符
     */
    public static final int WARN = 2;
    /**
     * ERROR日志形式的标识符
     */
    public static final int ERROR = 1;

    /**
     * 把异常用来输出日志的综合方法
     *
     * @param @param tag 日志标识
     * @param @param throwable 抛出的异常
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    public static void log(Context context, String tag, Throwable throwable, int type) {
        log(context, tag, exToString(throwable), type);
    }

    /**
     * 用来输出日志的综合方法（文本内容）
     *
     * @param @param tag 日志标识
     * @param @param msg 要输出的内容
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    public static void log(Context context, String tag, String msg, int type) {
        switch (type) {
            case VERBOSE:
                v(tag, msg);// verbose等级
                break;
            case DEBUG:
                d(tag, msg);// debug等级
                break;
            case INFO:
                i(tag, msg);// info等级
                break;
            case WARN:
                w(tag, msg);// warn等级
                break;
            case ERROR:
                e(context, tag, msg);// error等级
                break;
            default:
                break;
        }
    }

    /**
     * verbose等级的日志输出
     *
     * @param tag 日志标识
     * @param msg 要输出的内容
     * @return void 返回类型
     * @throws
     */
    public static void v(String tag, String msg) {
        // 是否开启日志输出
        if (isDeBug) {
            Log.v(tag, msg);
        }
        // 是否将日志写入文件
        if (isWrite) {
//            write(tag, msg);
        }
    }

    /**
     * debug等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void d(String tag, String msg) {
        if (isDeBug) {
            Log.d(tag, msg);
        }
        if (isWrite) {
//            write(tag, msg);
        }
    }

    /**
     * info等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void i(String tag, String msg) {
        if (isDeBug) {
            Log.i(tag, msg);
        }
        if (isWrite) {
//            write(tag, msg);
        }
    }

    /**
     * warn等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void w(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
        if (isWrite) {
//            write(tag, msg);
        }
    }

    /**
     * error等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     */
    public static void e(Context context, String tag, String msg) {
        if (isDeBug) {
            Log.e(tag, msg);
        }
        if (isWrite) {
            write(context, tag, msg);
        }
    }

    /**
     * 用于把日志内容写入制定的文件
     *
     * @param @param tag 标识
     * @param @param msg 要输出的内容
     * @return void 返回类型
     * @throws
     */
    public static void write(Context context, String tag, String msg) {
        String path = FileUtils.createMkdirsAndFiles(DIRPATH, LOGNAME);
//        String path = FileUtils.createDirsAndFiles(DIRPATH, LOGNAME);
//        String path = FileUtils.createDirsAndFiles(DIRPATH);
        if (TextUtils.isEmpty(path)) {
            return;
        }

        // 输出设备信息
        StringBuilder buf = new StringBuilder();
        //获取设备信息
        printDeviceInfo(context, buf, "\r\n");
        buf.append("\r\n");
        //获取存储信息
        printMemoryInfo(context, buf, "\r\n");
        String deviceInfo = buf.toString();

        msg = msg + "\n" + deviceInfo;
        String log = DateFormat.format(INFORMAT, System.currentTimeMillis())
                + tag
                + "======================>>"
                + msg
                + "\n=================================end=================================\n";
        FileUtils.write2File(path, log, true);
    }

    /**
     * 用于把日志内容写入制定的文件
     *
     * @param ex 异常
     */
    public static void write(Throwable ex) {
//        write("", exToString(ex));
    }

    /**
     * 把异常信息转化为字符串
     *
     * @param ex 异常信息
     * @return 异常信息字符串
     */
    private static String exToString(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String result = writer.toString();
        return result;
    }

    /*
     * 获取设备信息
     */
    @SuppressLint("NewApi")
    public static void printDeviceInfo(Context context, StringBuilder appender, String seperator) {
        appender.append("Id=").append(Build.ID).append(seperator);
        appender.append("Display=").append(Build.DISPLAY).append(seperator);
        appender.append("Product=").append(Build.PRODUCT).append(seperator);
        appender.append("Device=").append(Build.DEVICE).append(seperator);
        appender.append("Board=").append(Build.BOARD).append(seperator);
        appender.append("CpuAbi=").append(Build.CPU_ABI).append(seperator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            appender.append("CpuAbi2=").append(Build.CPU_ABI2).append(seperator);
        }
        appender.append("Manufacturer=").append(Build.MANUFACTURER).append(seperator);
        appender.append("Brand=").append(Build.BRAND).append(seperator);
        appender.append("Model=").append(Build.MODEL).append(seperator);
        appender.append("Hardware=").append(Build.HARDWARE).append(seperator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            appender.append("Serial=").append(Build.SERIAL).append(seperator);
        }
        appender.append("Type=").append(Build.TYPE).append(seperator);
        appender.append("Tags=").append(Build.TAGS).append(seperator);
        appender.append("FingerPrint=").append(Build.FINGERPRINT).append(seperator);
        appender.append("Version.Incremental=").append(Build.VERSION.INCREMENTAL).append(seperator);
        appender.append("Version.Release=").append(Build.VERSION.RELEASE).append(seperator);
        appender.append("SDK=").append(Build.VERSION.SDK).append(seperator);
        appender.append("SDKInt=").append(Build.VERSION.SDK_INT).append(seperator);
        appender.append("Version.CodeName=").append(Build.VERSION.CODENAME).append(seperator);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        appender.append("Density=").append(dm.density).append(';');
        appender.append("Width=").append(dm.widthPixels).append(';');
        appender.append("Height=").append(dm.heightPixels).append(';');
        appender.append("ScaledDensity=").append(dm.scaledDensity).append(';');
        appender.append("xdpi=").append(dm.xdpi).append(';');
        appender.append("ydpi=").append(dm.ydpi).append(';');
        appender.append("DensityDpi=").append(dm.densityDpi);
    }


    /**
     * 获取存储信息
     */
    @SuppressLint("SdCardPath")
    public static void printMemoryInfo(Context context, StringBuilder appender, String seperator) {
        // 存储信息
        appender.append("ESS=").append(Environment.getExternalStorageState()).append(';');
        appender.append("ESD=");
        try {
            File esd = Environment.getExternalStorageDirectory();
            appender.append(esd.getCanonicalPath());
        } catch (Exception e) {
            appender.append("unknown");
        }
        appender.append(';');
        appender.append("ESSC=");
        try {
            appender.append(EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory()));
        } catch (Exception e) {
            appender.append("unknown");
        }
        appender.append(';');
        appender.append("EXIST=");
        try {
            appender.append(new File("/mnt/sdcard").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/sdcard").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/storage/sdcard0").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/storage/sdcard1").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/storage/emulated/0").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/storage/emulated/1").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(',');
        try {
            appender.append(new File("/storage/emulated/legacy").exists());
        } catch (Exception e) {
            appender.append(false);
        }
        appender.append(seperator);

        // 存储空间
        File path = Environment.getDataDirectory();
        StatFs fs = new StatFs(path.getPath());
        long blockSize = getBlockSize(fs);
        long availableBlocks = getAvailableBlocks(fs);
        appender.append("IMA=").append(formatFileSize(availableBlocks * blockSize)).append(';');
        long totalBlocks = getBlockCount(fs);
        appender.append("IMT=").append(formatFileSize(totalBlocks * blockSize)).append(';');
        String ess = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(ess) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(ess)) {
            path = Environment.getExternalStorageDirectory();
            fs = new StatFs(path.getPath());
            blockSize = getBlockSize(fs);
            availableBlocks = getAvailableBlocks(fs);
            appender.append("EMA=").append(formatFileSize(availableBlocks * blockSize)).append(';');
            totalBlocks = getBlockCount(fs);
            appender.append("EMT=").append(formatFileSize(totalBlocks * blockSize));
        }
        appender.append(seperator);

        // 内存信息
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        appender.append("AM.MEM=").append(formatFileSize(mi.availMem)).append(',');
        if (Build.VERSION.SDK_INT >= 16) {
            appender.append(formatFileSize(mi.totalMem));
        }
        appender.append(',');
        appender.append(formatFileSize(mi.threshold)).append(',').append(mi.lowMemory);
    }

    /**
     * 转换文件大小
     *
     * @param fileSize
     * @return 文件大小
     */
    public static String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + "B";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize < 1024 * 1024) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    private static long getAvailableBlocks(StatFs fs) {
        if (Build.VERSION.SDK_INT >= 18) {
            return fs.getAvailableBlocksLong();
        } else {
            return fs.getAvailableBlocks();
        }
    }

    private static long getBlockSize(StatFs fs) {
        if (Build.VERSION.SDK_INT >= 18) {
            return fs.getBlockSizeLong();
        } else {
            return fs.getBlockSize();
        }
    }

    private static long getBlockCount(StatFs fs) {
        if (Build.VERSION.SDK_INT >= 18) {
            return fs.getBlockCountLong();
        } else {
            return fs.getBlockCount();
        }
    }
}
