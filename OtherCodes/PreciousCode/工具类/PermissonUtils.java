package com.newrecord.cloud.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @describe: android 6.0及以上设备动态权限封装操作类
 * @author: yyh
 * @createTime: 2018/11/29 10:55
 * @className: PermissonUtils
 */
public class PermissonUtils {

    private static final String PACKAGE_URL_SCHEME = "package:";

    private static PermissonUtils sInstance;

    public static final int PERMISSION_REQUESTCODE = 0x01;//动态权限请求码

    private PermissionCallback mCallBack;//权限请求回调接口

    private boolean mIsNeedFinsh;//是否销毁当前界面

    private AlertDialog mDialog;

    /**
     * 权限请求回调接口设置
     */
    public void setPermissionCallback(PermissionCallback mCallBack) {
        this.mCallBack = mCallBack;
    }

    private PermissonUtils() {
    }

    public static PermissonUtils getInstance() {
        if (sInstance == null) {
            synchronized (PermissonUtils.class) {
                if (sInstance == null) {
                    sInstance = new PermissonUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 权限禁止后显示设置弹窗
     *
     * @param isNeedFinsh 取消时是否结束当前界面
     * @return
     */
    public PermissonUtils needToFinsh(boolean isNeedFinsh) {
        this.mIsNeedFinsh = isNeedFinsh;
        return this;
    }

    /**
     * 权限被拒绝后提示弹窗
     */
    public AlertDialog showGoSettingDialog(final Activity activity) {
        if (mDialog == null) {
            LinearLayout rootView = new LinearLayout(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            rootView.setLayoutParams(layoutParams);
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //标题展示view
            TextView titleView = new TextView(activity);
            titleView.setText("权限设置");
            titleView.setPadding(0, 50, 0, 0);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            titleView.setTextColor(Color.parseColor("#333333"));
            titleView.setGravity(Gravity.CENTER);
            titleView.setLayoutParams(textParams);
            rootView.addView(titleView);
            //内容展示view
            TextView contentView = new TextView(activity);
            contentView.setText("权限被禁止，请手动打开？");
            contentView.setPadding(0, 30, 0, 50);
            contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            contentView.setTextColor(Color.parseColor("#333333"));
            contentView.setLayoutParams(textParams);
            rootView.addView(contentView);

            //间隔线
            LinearLayout lineView = new LinearLayout(activity);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lineParams.height = 3;//间隔线高度
            lineView.setLayoutParams(lineParams);
            lineView.setBackgroundColor(Color.parseColor("#dddddd"));
            rootView.addView(lineView);

            //底部按钮布局
            LinearLayout bottomLayout = new LinearLayout(activity);
            bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rootView.addView(bottomLayout);

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnParams.weight = 1.0f;
            //确定view
            TextView confirmView = new TextView(activity);
            confirmView.setText("确定");
            confirmView.setPadding(0, 30, 0, 30);
            confirmView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            confirmView.setTextColor(Color.parseColor("#333333"));
            confirmView.setGravity(Gravity.CENTER);
            confirmView.setLayoutParams(btnParams);
            bottomLayout.addView(confirmView);
            //间隔线view
            LinearLayout shortLineView = new LinearLayout(activity);
            LinearLayout.LayoutParams shortLineParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            shortLineParams.width = 3;
            shortLineView.setLayoutParams(shortLineParams);
            shortLineView.setBackgroundColor(Color.parseColor("#dddddd"));
            bottomLayout.addView(shortLineView);
            //取消view
            TextView cancelView = new TextView(activity);
            cancelView.setText("取消");
            cancelView.setPadding(0, 30, 0, 30);
            cancelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            cancelView.setTextColor(Color.parseColor("#333333"));
            cancelView.setGravity(Gravity.CENTER);
            cancelView.setLayoutParams(btnParams);
            bottomLayout.addView(cancelView);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT).setView(rootView);
            mDialog = builder.create();
            mDialog.setInverseBackgroundForced(true);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);

            confirmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    mDialog = null;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse(PACKAGE_URL_SCHEME + activity.getPackageName()));
                    activity.startActivityForResult(intent, PERMISSION_REQUESTCODE);
                }
            });
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    mDialog = null;
                    if (mIsNeedFinsh) {
                        activity.finish();
                    }
                }
            });
        }
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
        mDialog.show();
        return mDialog;
    }

    /**
     * 判断是否有相应权限
     *
     * @param activity
     * @param mPermissions
     * @return
     */
    public boolean hasPermissions(Activity activity, String[] mPermissions) {
        for (String perm : mPermissions) {
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 开始请求权限
     *
     * @param activity
     * @param permissions
     */
    public boolean requestPermission(Activity activity, String[] permissions) {
        if (!hasPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUESTCODE);
            return false;
        } else {
            List<String> granted = new ArrayList<String>();
            for (int i = 0; i < permissions.length; i++) {
                granted.add(permissions[i]);
            }
            if (mCallBack != null) {
                mCallBack.onPermissionsGranted(PERMISSION_REQUESTCODE, granted);
            }
            return true;
        }
    }

    /**
     * 权限请求结果回调
     * 1.重写Activity的onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)方法;
     * 2.在重写的onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)方法中调用该方法;
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        List<String> granted = new ArrayList<String>();
        List<String> denied = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (!granted.isEmpty() && mCallBack != null) {
            mCallBack.onPermissionsGranted(requestCode, granted);
        }
        if (!denied.isEmpty() && mCallBack != null) {
            mCallBack.onPermissionsDenied(requestCode, denied);
        }
    }

    /**
     * 权限请求回调接口
     */
    public interface PermissionCallback {
        /**
         * 权限获取完成
         */
        void onPermissionsGranted(int requestCode, List<String> perms);

        /**
         * 权限拒绝
         */
        void onPermissionsDenied(int requestCode, List<String> perms);
    }
}