package com.newrecord.cloud.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.newrecord.cloud.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 沉浸式状态栏工具类
 * */
public class StatusBarUtil {

    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";    //OPPO
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";     //小米
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";       //华为
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";   //锤子
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";          //vivo

    /**
     * 设置状态栏透明度、背景颜色、文字颜色
     * @param isTranslate 是否透明，若为true，则bgColor设置无效
     * @param isDarkText  字体颜色，只有黑白两色，无论什么色值，都只会转为黑白两色
     * @param bgColor     背景色，即状态栏颜色，isTranslate若为true则此值无效
     */
    public static boolean setStatusColor(Activity activity, boolean isTranslate, boolean isDarkText, @ColorRes int bgColor) {
        //MIUI系统
        if (isMIUI6Later()) {
            setMIUI6Translate(activity, isTranslate);
            setMIUI6StatusBarDarkMode(activity, isDarkText);
        }else{
            //如果系统为6.0及以上，使用Google自带的方式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | (isDarkText ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
                window.setStatusBarColor(isTranslate ? Color.TRANSPARENT : ContextCompat.getColor(activity, bgColor));

            } else {
                //OPPO Color3.0 & Android 5.1
                if (isColorOS_3()) {
                    //控制字体颜色，只有黑白两色
                    final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = isDarkText ? 0x00000010 : 0x00190000;
                    Window window = activity.getWindow();
                    View decorView = window.getDecorView();
                    decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(isTranslate ? Color.TRANSPARENT : ContextCompat.getColor(activity, bgColor));
                    }

                }  else if (isFlyme4Later()) {
                    darModeForFlyme4(activity, isDarkText);
                    if (isTranslate) {
                        setStatusTranslate(activity);
                    }
                } else {
                    //其他机型，如华为、三星、索尼、诺基亚、VIVO、锤子、360等
                    if (isTranslate) {
                        setStatusTranslate(activity);
                    }
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean setStatusColor(Activity activity, boolean isTranslate, boolean isDarkText) {
        try {
            return setStatusColor(activity, isTranslate, isDarkText, R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 是否是MIUI6及以后版本
     * @return 是否是MIUI6
     */
    private static boolean isMIUI6Later() {
        try {
            Class<?> cla = Class.forName("android.os.SystemProperties");
            Method mtd = cla.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, KEY_VERSION_MIUI);
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置MIUI6及以上状态栏透明,字体为默认白色，Android6.0以上也可以
     * @param on 是否为透明
     */
    private static void setMIUI6Translate(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 设置MIUI6及以上，Android6.0以下版本状态栏黑色字符
     */
    private static void setMIUI6StatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否是ColorOS_3.0系统 Android 5.1
     * @return 是否是colorOS_3.0
     */
    private static boolean isColorOS_3() {
        try {
            Class<?> cla = Class.forName("android.os.SystemProperties");
            Method mtd = cla.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, KEY_VERSION_OPPO);
            val = val.replaceAll("[vV]", "");
            val = val.substring(0, 1);
            int version = Integer.parseInt(val);
            return version >= 3;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否是魅族Flyme4
     * @return 是否是魅族Flyme4
     */
    private static boolean isFlyme4Later() {
        if ("MEIZU".equals(Build.BRAND.trim().toUpperCase())) {
            return Build.FINGERPRINT.contains("Flyme_OS_4") || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4") || Pattern.compile("Flyme_OS_[4|5]", Pattern.CASE_INSENSITIVE)
                    .matcher(Build.DISPLAY).find();
        }
        return false;
    }

    /**
     * 设置魅族Flyme4以后 状态栏黑色字体
     * @param dark 是否黑色
     */
    private static void darModeForFlyme4(Activity activity, boolean dark) {
        boolean result = false;
        try {
            WindowManager.LayoutParams e = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(e);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(e, value);
            activity.getWindow().setAttributes(e);
        } catch (Exception var8) {
            Log.e("StatusBar", "darkIcon: failed");
        }
    }

    /**
     * 设置状态栏透明
     */
    public static void setStatusTranslate(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏背景颜色，不适配5.0以下系统
     * @param color 颜色值
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
        }
    }
}