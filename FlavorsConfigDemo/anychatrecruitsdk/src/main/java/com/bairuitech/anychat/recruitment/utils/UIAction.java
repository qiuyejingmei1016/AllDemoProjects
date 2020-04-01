package com.bairuitech.anychat.recruitment.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.recruitment.R;

import java.lang.reflect.Method;

/**
 * @describe: UI常用方法封装
 * @author: yyh
 * @createTime: 2018/8/3 16:47
 * @className: UIAction
 */
public class UIAction {

    /**
     * 开启全屏模式
     */
    public static void setFullScreenOn(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }

    /**
     * 关闭全屏模式
     */
    public static void setFullScreenOff(Window window) {
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(attr);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static Toast makeToast(Context context, int resId, int duration) {
        return Toast.makeText(context, resId, duration);
    }

    public static Toast makeToast(Context context, CharSequence text, int duration) {
        return Toast.makeText(context, text, duration);
    }

    public static void setTitle(Activity activity, int titleTextResId) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.sdk_title_text);
        if (textView == null) {
            return;
        }
        textView.setText(titleTextResId);
    }

    public static void setTitle(Activity activity, int titleTextResId, View.OnClickListener clickListener) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.sdk_title_text);
        if (textView == null) {
            return;
        }
        textView.setOnClickListener(clickListener);
        textView.setText(titleTextResId);
    }

    public static void setTitleLeft(Activity activity, int titleTextResId, View.OnClickListener clickListener) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.sdk_title_left_text);
        if (textView == null) {
            return;
        }
        textView.setOnClickListener(clickListener);
        textView.setText(titleTextResId);
    }

    public static ImageButton setTitleBarLeftImgBtn(View view, int iconResId, View.OnClickListener clickListener) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.sdk_title_left_img_btn);
        if (btn != null) {
            btn.setImageResource(iconResId);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(clickListener);
        }
        return btn;
    }

    public static ImageButton setTitleBarRightImgBtn(View view, int iconResId, View.OnClickListener clickListener) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.sdk_title_right_img_btn);
        if (btn != null) {
            btn.setImageResource(iconResId);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(clickListener);
        }
        return btn;
    }

    public static void setBackgroundResourceSafety(View view, int resId) {
        int paddingTop = view.getPaddingTop();
        int paddingBottom = view.getPaddingBottom();
        int paddingLeft = view.getPaddingLeft();
        int paddingRight = view.getPaddingRight();
        view.setBackgroundResource(resId);
        if (paddingLeft != 0 || paddingTop != 0 || paddingRight != 0 || paddingBottom != 0) {
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }

    /**
     * 获取屏幕真实宽度
     */
    public static int getScreenRealWidth(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    /**
     * 获取屏幕真实高度(不包含底部虚拟返回键高度)
     */
    public static int getScreenRealHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics dm = new DisplayMetrics();
        try {
//            @SuppressWarnings("rawtypes")
//            Class c = Class.forName("android.view.Display");
//            @SuppressWarnings("unchecked")
//            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//            method.invoke(display, dm);
//            vh = dm.heightPixels;
            vh = windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            result = context.getResources().getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    /**
     * 设置横屏模式
     *
     * @param activity
     */
    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 设置竖屏模式
     *
     * @param activity
     */
    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 设置跟随系统模式
     *
     * @param activity
     */
    public static void setUnspecified(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}