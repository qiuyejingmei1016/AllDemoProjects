package com.yyh.myconstomsqlite;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.androidquery.util.AQUtility.close;

/**
 * Created by Administrator on 2018/6/27.
 */

public class CommonUtils {
    /**
     * 复制文件 缓冲区大小
     */
    private static final int BUFFER_SIZE = 100 * 1024;

    public static boolean copyFile(File origin, File dest) {
        if (origin == null || dest == null) {
            return false;
        }
        if (!dest.exists()) {
            File parentFile = dest.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                Log.e("=========", "copyFile failed, cause mkdirs return false");
                return false;
            }
            try {
                if (!dest.createNewFile()) {
                    Log.e("=========", "copyFile failed, cause createNewFile failed");
                    return false;
                }
            } catch (Exception e) {
                Log.e("=========", "copyFile failed, cause createNewFile failed", e);
                return false;
            }
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(origin);
            out = new FileOutputStream(dest);
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
            int length;
            while (true) {
                if (inC.position() == inC.size()) {
                    return true;
                }
                if ((inC.size() - inC.position()) < BUFFER_SIZE) {
                    length = (int) (inC.size() - inC.position());
                } else {
                    length = BUFFER_SIZE;
                }
                inC.transferTo(inC.position(),
                        length,
                        outC);
                inC.position(inC.position() + length);
            }
        } catch (Exception e) {
            Log.e("=========", "copyFile failed", e);
            return false;
        } finally {
            close(in);
            close(out);
        }
    }
}
