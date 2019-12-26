package com.yyh.buildconfigdemo.permission;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.yyh.buildconfigdemo.R;
import com.yyh.buildconfigdemo.permission.easypermissions.EasyPermissions;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * @describe: 权限管理工具类
 * @author: yyh
 * @createTime: 2018/6/8 11:27
 * @className: PermissionUtil
 */
public class PermissionUtil implements EasyPermissions.PermissionCallbacks {
    private static final String PACKAGE_URL_SCHEME = "package:";
    public static short REQUEST_CODE_PERMISSION_FRAGMENT = 123;
    public static final short REQUEST_CODE_PERMISSION_ACTIVITY = 124;
    public static final String DIVIDE = ",";

    private static PermissionUtil sInstance;
    private WeakReference<PermissonCallBack> mCalRef;
    private boolean mIsNeedFinsh;

    private PermissionUtil() {
    }

    public static PermissionUtil getInstance() {
        if (sInstance == null) {
            synchronized (PermissionUtil.class) {
                if (sInstance == null) {
                    sInstance = new PermissionUtil();
                }
            }
        }
        return sInstance;
    }

    public PermissionUtil setRequestCode(Short s) {
        REQUEST_CODE_PERMISSION_FRAGMENT = s;
        return this;
    }

    public PermissionUtil needToFinsh(boolean isNeedFinsh) {
        mIsNeedFinsh = isNeedFinsh;
        return this;
    }

    /**
     * @param activity
     * @param pers
     */
    public void request(Activity activity, String... pers) {
        PermissonCallBack callBack = PermissionUtil.getInstance().setCallBack(((PermissonCallBack) activity));
        if (!EasyPermissions.hasPermissions(activity, pers)) {
            EasyPermissions.requestPermissions(activity, "", R.string.ok, R.string.cancel,
                    REQUEST_CODE_PERMISSION_ACTIVITY, pers);
        } else {
            callBack.onPermissionsGranted(REQUEST_CODE_PERMISSION_ACTIVITY, Arrays.asList(pers));
        }
    }

    public static boolean hasPer(Context context, String... pers) {
        return EasyPermissions.hasPermissions(context, pers);
    }

    public static void requestCallBack(int requestCode, String[] permissions, int[] grantResults, Object fragment) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, fragment);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mCalRef != null) {
            mCalRef.get().onPermissionsGranted(requestCode, perms);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (mCalRef != null) {
            mCalRef.get().onPermissionsDenied(requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        if (mCalRef != null) {
            mCalRef.get().onRequestPermissionsResult(i, strings, ints);
        }
    }

    public interface PermissonCallBack {

        void onPermissionsGranted(int requestCode, List<String> perms);

        void onPermissionsDenied(int requestCode, List<String> perms);

        void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints);
    }

    public PermissonCallBack setCallBack(PermissonCallBack callBack) {
        mCalRef = new WeakReference<PermissonCallBack>(callBack);
        return mCalRef.get();
    }
}
