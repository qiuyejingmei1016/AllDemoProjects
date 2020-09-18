package com.newrecord.cloud.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * @describe: 打开指定文件
 * @className: OpenFileUtil
 */
public class OpenFileUtil {

    private static final String[][] MIME_Maps = {
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    /**
     * 根据后缀名获取MIMEType
     *
     * @param file
     * @return
     */
    private static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        //获取文件的后缀名
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType)) {
            return type;
        }
        for (int i = 0; i < MIME_Maps.length; i++) {
            if (fileType.equals(MIME_Maps[i][0])) {
                type = MIME_Maps[i][1];
            }
        }
        return type;
    }

    /**
     * 打开文件
     *
     * @param filePath 文件路径
     */
    public static Intent openFile(Context context, String filePath) {
        if(TextUtils.isEmpty(filePath)){
            Toast.makeText(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在或者已损坏", Toast.LENGTH_SHORT).show();
            return null;
        }
        Intent intent = new Intent();
        Uri uri = null;
        //URL文件临时权限适配 7.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //第二个参数就是Manifest中的authorities
                String packageName = context.getPackageName();
                String authority = packageName + ".FileProvider";
                uri = FileProvider.getUriForFile(context, authority, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            uri = Uri.fromFile(file);
        }

        if (uri != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMIMEType(file));
            return intent;
        }
        return null;
    }
}