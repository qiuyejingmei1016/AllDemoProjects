package com.yyh.buildconfigdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * @describe: 获取设备信息
 * @author: yyh
 * @createTime: 2018/5/17 16:47
 * @className: DeviceUtils
 */
public class DeviceUtils {
    /**
     * 获取系统SDK版本号
     */
    public static int getSystemVersion() {
        /*获取当前系统的android版本号*/
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(Activity activity, Context context) {
        String simSerialNumber, ANDROID_ID, mDeviceID, language, manufacturer, model,
                sys_version, sdkLevel, mMacAddress, screenWidth, screenHeight, mCurrentVersion = null, mPackageName = null, mCurrentCode = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //sim 格式
        simSerialNumber = tm.getSimSerialNumber();
        if (null == simSerialNumber || "".equals(simSerialNumber)) {
            simSerialNumber = "89860023030970003726";
        }
        //系统标识
        ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        if (null == ANDROID_ID || "".equals(ANDROID_ID)) {
            ANDROID_ID = "9774d56d682e549c";
        }
        //imei号
        mDeviceID = tm.getDeviceId();
        if (null == mDeviceID || "".equals(mDeviceID)) {
            mDeviceID = "865915020003086";
        }
        //系统语言
        language = Locale.getDefault().getLanguage();
        if (null == language || "".equals(language)) {
            language = "en";
        }
        //手机制造商
        manufacturer = Build.MANUFACTURER;
        if (null == manufacturer || "".equals(manufacturer)) {
            manufacturer = "LG";
        }
        //手机型号
        model = Build.MODEL;
        if (null == model || "".equals(model)) {
            model = "P7000";
        }
        //android系统版本号
        sys_version = Build.VERSION.RELEASE;
        if (null == sys_version || "".equals(sys_version)) {
            sys_version = "4.4.1";
        }
        sdkLevel = Build.VERSION.SDK;
        if (null == sdkLevel || "".equals(sdkLevel)) {
            sdkLevel = "19";
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mMacAddress = wifi.getConnectionInfo().getMacAddress();
        if (null == mMacAddress || "".equals(mMacAddress)) {
            mMacAddress = "00:08:22:4a:57:6d";
        }
        try {
            // 获得应用包名
            mPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
            if (null == mPackageName || "".equals(mPackageName)) {
                mPackageName = "com.qp.weirdnews";
            }
            // 当前版本号1.0.x
            mCurrentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (null == mCurrentVersion || "".equals(mCurrentVersion)) {
                mCurrentVersion = "1.0";
            }
            // 当前版本号 1 2 3 4....
            int currentVerCode = getCurrentVerCode(context);
            mCurrentCode = String.valueOf(currentVerCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // Android获得屏幕的宽和高
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = String.valueOf(display.getWidth());
        screenHeight = String.valueOf(display.getHeight());
        if (null == screenWidth || "".equals(screenWidth)) {
            screenWidth = "480";
        }
        if (null == screenHeight || "".equals(screenHeight)) {
            screenHeight = "800";
        }

        //获取签名信息并使用MD5加密保护
        String installedAPKSign = Md5Utils.encodeBy32BitMD5(getInstalledAPKSignature(context));
        if (null == installedAPKSign || "".equals(installedAPKSign)) {
            installedAPKSign = "5e027396789a18c37aeda616e3d7991b";
        }
        //获取渠道信息
        String channel_id = getAppMetaData(context, "UMENG_CHANNEL");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("simSerialNumber", simSerialNumber);
            jsonObject.put("channel_id", channel_id);
            jsonObject.put("ANDROID_ID", ANDROID_ID);
            jsonObject.put("imei", mDeviceID);
            jsonObject.put("language", language);
            jsonObject.put("manufacturer", manufacturer);
            jsonObject.put("model", model);
            jsonObject.put("sys_version", sys_version);
            jsonObject.put("sdkLevel", sdkLevel);
            jsonObject.put("mMacAddress", mMacAddress);
            jsonObject.put("screenWidth", screenWidth);
            jsonObject.put("screenHeight", screenHeight);
            jsonObject.put("mCurrentVersion", mCurrentVersion);
            jsonObject.put("mPackageName", mPackageName);
            jsonObject.put("mCurrentCode", mCurrentCode);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取apk包的签名信息
     */
    public static String getInstalledAPKSignature(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo appInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (null == appInfo || null == appInfo.signatures) {
                return "";
            }
            return appInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 当前版本号 1 2 3 4....
     */
    public static int getCurrentVerCode(Context context) {
        int mCurrentCode = 0;
        try {
            mCurrentCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            if (0 == mCurrentCode) {
                mCurrentCode = 1;
            }
            return mCurrentCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mCurrentCode;
    }

    /**
     * 获取渠道号
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "x0001";
        }
        String resultData = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                        return resultData;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "x0001";
        }
        return "x0001";
    }

    /**
     * 对图片进行缩放
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }
}
