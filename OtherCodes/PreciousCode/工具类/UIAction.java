package com.newrecord.cloud.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newrecord.cloud.R;

import java.lang.reflect.Method;
import java.util.List;

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
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        TextView textView = toastView.findViewById(R.id.content_view);
        textView.setText(resId);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, context.getResources().getDimensionPixelOffset(R.dimen.dp_200));
        toast.setDuration(duration);
        toast.setView(toastView);
        return toast;
    }

    public static Toast makeToast(Context context, CharSequence text, int duration) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        TextView textView = toastView.findViewById(R.id.content_view);
        textView.setText(text);
        Toast toast = new Toast(context);
        int offset = context.getResources().getDimensionPixelOffset(R.dimen.dp_200);
        toast.setGravity(Gravity.BOTTOM, 0, context.getResources().getDimensionPixelOffset(R.dimen.dp_200));
        toast.setDuration(duration);
        toast.setView(toastView);
        return toast;
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param titleTextResId 文字内容
     */
    public static void setTitle(Activity activity, @StringRes int titleTextResId) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_text);
        if (textView == null) {
            return;
        }
        textView.setText(titleTextResId);
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param titleTextResId      文字内容
     * @param titleTextColorResId 文字颜色
     */
    public static void setTitle(Activity activity, @StringRes int titleTextResId, @ColorRes int titleTextColorResId) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_text);
        if (textView == null) {
            return;
        }
        textView.setText(titleTextResId);
        textView.setTextColor(activity.getResources().getColor(titleTextColorResId));
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param titleText 文字内容
     */
    public static void setTitle(Activity activity, String titleText) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_text);
        if (textView == null) {
            return;
        }
        textView.setText(titleText);
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param titleText           文字内容
     * @param titleTextColorResId 文字颜色
     */
    public static void setTitle(Activity activity, String titleText, @ColorRes int titleTextColorResId) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_text);
        if (textView == null) {
            return;
        }
        textView.setText(titleText);
        textView.setTextColor(activity.getResources().getColor(titleTextColorResId));
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param titleTextResId 文字内容
     * @param clickListener  点击事件
     */
    public static void setTitle(Activity activity, @StringRes int titleTextResId, View.OnClickListener clickListener) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_text);
        if (textView == null) {
            return;
        }
        textView.setOnClickListener(clickListener);
        textView.setText(titleTextResId);
    }

    /**
     * 标题栏文字设置
     *
     * @param activity
     * @param text          文字内容
     * @param clickListener 点击事件
     */
    public static void setLeftTitle(Activity activity, String text, View.OnClickListener clickListener) {
        if (activity == null) {
            return;
        }
        TextView textView = (TextView) activity.findViewById(R.id.title_left_text);
        if (textView == null) {
            return;
        }
        if (!StringUtil.isNullOrEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(clickListener);
            textView.setText(text);
        }
    }

    /**
     * 标题栏左边图标设置
     *
     * @param view
     * @param iconResId     图片资源id
     * @param clickListener 点击事件
     * @return
     */
    public static ImageButton setTitleBarLeftImgBtn(View view, @DrawableRes int iconResId, View.OnClickListener clickListener) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.title_left_img_btn);
        if (btn != null) {
            btn.setImageResource(iconResId);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(clickListener);
        }
        return btn;
    }

    /**
     * 标题栏右边图标设置
     *
     * @param view
     * @param iconResId     图片资源id
     * @param clickListener 点击事件
     * @return
     */
    public static ImageButton setTitleBarRightImgBtn(View view, @DrawableRes int iconResId, View.OnClickListener clickListener) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.title_right_img_btn);
        if (btn != null) {
            btn.setImageResource(iconResId);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(clickListener);
        }
        return btn;
    }

    /**
     * 标题栏右边图标设置
     *
     * @param view
     * @param layoutParams
     * @param iconResId     图片资源id
     * @param clickListener 点击事件
     * @return
     */
    public static ImageButton setTitleBarRightImgBtn(View view, RelativeLayout.LayoutParams layoutParams,
                                                     @DrawableRes int iconResId, View.OnClickListener clickListener) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.title_right_img_btn);
        if (btn != null) {
            btn.setLayoutParams(layoutParams);
            btn.setBackgroundResource(iconResId);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(clickListener);
        }
        return btn;
    }

    /**
     * 标题栏底部是否显示分割线设置
     *
     * @param activity
     * @param isShowLine 是否显示分割线
     */
    public static void setShowTitleDividerLine(Activity activity, boolean isShowLine) {
        if (activity == null) {
            return;
        }
        View view = activity.findViewById(R.id.title_bar_line_view);
        if (view == null) {
            return;
        }
        view.setVisibility(isShowLine ? View.VISIBLE : View.GONE);
    }

    public static void setKeyValue(View view, int id) {
        TextView keyView = (TextView) view.findViewById(R.id.key);
        keyView.setText(id);
    }

    /**
     * 设置view背景
     *
     * @param view
     * @param resId 资源id
     */
    public static void setBackgroundResourceSafety(View view, @DrawableRes int resId) {
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
     * 隐藏输入法
     */
    public static void hideSoftKeyboard(Context context, View view) {
        if (context == null) {
            return;
        }
        if (view == null) {
            return;
        }
        View rootView = view.getRootView();
        if (rootView == null) {
            return;
        }
        View focusedView = rootView.findFocus();
        if (focusedView == null) {
            return;
        }
        InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        if (inputMgr != null) {
            IBinder windowToken = focusedView.getWindowToken();
            if (windowToken != null) {
                inputMgr.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
            }
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

    /**
     * 获取app版本号 如：versionCode 1
     */
    public static String getAppVersionCode(Context context) {
        float versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        if (versioncode == 0) {
            return "";
        }
        return "" + versioncode;
    }

    /**
     * 返回当前程序版本名 如：versionName 1.0.1
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        if (versionName == null) {
            return "";
        }
        return "App v" + versionName;
    }

    /**
     * 控制多人视频时每个人视频显示的子布局的大小
     *
     * @param itemViewList 多人视频时存储视频显示子布局集合
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     */
    public static void controlItemParams(List<View> itemViewList, int screenWidth, int screenHeight) {
        if (itemViewList == null || itemViewList.size() == 0) {
            return;
        }
        if (itemViewList.size() == 1) {
            View view = itemViewList.get(0);
            RelativeLayout.LayoutParams params;
            params = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
            view.setLayoutParams(params);
        } else if (itemViewList.size() < 5) {
            for (int i = 0; i < itemViewList.size(); i++) {
                View view = itemViewList.get(i);
                RelativeLayout.LayoutParams params;
                params = new RelativeLayout.LayoutParams(screenWidth / 2, screenHeight / 2);
                int heighs = 0;
                int widths = 0;
                if (i == 0 || i == 1) {
                    heighs = 0;
                } else if (i == 2 || i == 3) {
                    heighs = 1;
                }
                if (i == 0 || i == 2) {
                    widths = 0;
                } else if (i == 1 || i == 3) {
                    widths = 1;
                }
                params.setMargins(screenWidth / 2 * widths, screenHeight / 2 * heighs, 0, 0);
                view.setLayoutParams(params);
            }
        } else {
            for (int i = 0; i < itemViewList.size(); i++) {
                View view = itemViewList.get(i);
                RelativeLayout.LayoutParams params;
                params = new RelativeLayout.LayoutParams(screenWidth / 3, screenHeight / 3);

                int heights = 0;
                int widths = 0;

                if (i == 0 || i == 1 || i == 2) {
                    heights = 0;
                } else if (i == 3 || i == 4 || i == 5) {
                    heights = 1;
                } else if (i == 6 || i == 7 || i == 8) {
                    heights = 2;
                } else if (i == 9 || i == 10 || i == 11) {
                    heights = 3;
                } else if (i == 12 || i == 13 || i == 14) {
                    heights = 4;
                } else if (i == 15) {
                    heights = 5;
                }

                if (i == 0 || i == 3 || i == 6 || i == 9 || i == 12 || i == 15) {
                    widths = 0;
                } else if (i == 1 || i == 4 || i == 7 || i == 10 || i == 13) {
                    widths = 1;
                } else {
                    widths = 2;
                }

                params.setMargins(screenWidth / 3 * widths, screenHeight / 3 * heights, 0, 0);
                view.setLayoutParams(params);
            }
        }
    }
}