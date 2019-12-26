/*
 * 文件名: UIAction.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2013-1-11
 */
package com.yyh.configtest.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.yyh.configtest.R;
import com.yyh.configtest.ui.base.BaseFragment;
import com.yyh.configtest.ui.view.MGAlertDialog;


/**
 * UI常用方法封装
 *
 * @author Kelvin Van
 */
public class UIAction {

    private static final String TAG = "UIAction";

    public static Toast makeToast(Context context, int resId, int duration) {
        return Toast.makeText(context, resId, duration);
    }

    public static Toast makeToast(Context context, CharSequence text, int duration) {
        return Toast.makeText(context, text, duration);
    }


    public static IAlertDialog newAlertDialog(Context context) {
        MGAlertDialog alertDlg = new MGAlertDialog(context);
        return alertDlg;
    }

    public static ProgressDialog newProgressDialog(Context context) {
        return new ProgressDialog(context);
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


    public static void actionViewUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(url));
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "startActivity error", e);
        }
    }

    public static void setNoCancel(IAlertDialog dialog) {
        if (dialog instanceof MGAlertDialog) {
            MGAlertDialog alertDlg = (MGAlertDialog) dialog;
            alertDlg.setCancelable(false);
            alertDlg.setCanceledOnTouchOutside(false);
            alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return (keyCode == KeyEvent.KEYCODE_SEARCH);
                }
            });
        }
    }


    public static boolean isLandscape(Activity activity) {
        int orientation = activity.getRequestedOrientation();
        return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE == orientation
                || ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == orientation;
    }

    public static void setLandscape(Activity activity) {
        if (Build.VERSION.SDK_INT >= 9) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    public static boolean handleBackPressedEvent(FragmentActivity fragmentActivity) {
        return handleBackPressedEvent(fragmentActivity.getSupportFragmentManager());
    }

    public static boolean handleBackPressedEvent(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
        return handleBackPressedEvent(fragment);
    }

    public static boolean handleBackPressedEvent(Fragment fragment) {
        return fragment instanceof OnBackPressedEventHandler
                && ((OnBackPressedEventHandler) fragment).handleBackPressedEvent();
    }


    public static IAlertDialog newCancelEditAlertDialogToFinishFragment(Context context, BaseFragment fragment) {
        IAlertDialog dialog = newAlertDialog(context);
        dialog.setMessage("是否取消当前操作");
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "否", null);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "是", new FinishListener(fragment));
        return dialog;
    }

    private static class FinishListener implements DialogInterface.OnClickListener {

        private BaseFragment mFragment;

        public FinishListener(BaseFragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mFragment.finishFragmentOrActivity();
        }
    }
}