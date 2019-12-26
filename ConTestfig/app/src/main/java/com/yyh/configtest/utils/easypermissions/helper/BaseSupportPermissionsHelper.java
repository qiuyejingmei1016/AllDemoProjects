package com.yyh.configtest.utils.easypermissions.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yyh.configtest.utils.IAlertDialog;
import com.yyh.configtest.utils.easypermissions.EasyPermissions;

import java.util.Arrays;

/**
 * Implementation of {@link PermissionHelper} for Support Library host classes.
 */
public abstract class BaseSupportPermissionsHelper<T> extends PermissionHelper<T> {

    private IAlertDialog mDialog;

    public BaseSupportPermissionsHelper(@NonNull T host) {
        super(host);
    }

    public abstract FragmentManager getSupportFragmentManager();

    @Override
    @SuppressLint("NewApi")
    public void showRequestPermissionRationale(@NonNull String rationale,
                                               int positiveButton,
                                               int negativeButton,
                                               int requestCode,
                                               @NonNull String... perms) {
        //PermissionUtil.showGoSettingDialog(getContext());
        if (getHost() instanceof Fragment) {
            PermissionHelper.newInstance((Fragment) getHost()).directRequestPermissions(
                    requestCode, perms);
        } else if (getHost() instanceof android.app.Fragment) {
            PermissionHelper.newInstance((android.app.Fragment) getHost()).directRequestPermissions(
                    requestCode, perms);
        } else if (getHost() instanceof Activity) {
            PermissionHelper.newInstance((Activity) getHost()).directRequestPermissions(
                    requestCode, perms);
        }
    }

    private class PermissonDialogClickListener implements Dialog.OnClickListener {

        private EasyPermissions.PermissionCallbacks mCallbacks;
        private int requestCode;
        private String[] permissions;

        public PermissonDialogClickListener(EasyPermissions.PermissionCallbacks callbacks, int requestCode, String... permissions) {
            mCallbacks = callbacks;
            this.requestCode = requestCode;
            this.permissions = permissions;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            if (which == Dialog.BUTTON_POSITIVE) {
                if (getHost() instanceof Fragment) {
                    PermissionHelper.newInstance((Fragment) getHost()).directRequestPermissions(
                            requestCode, permissions);
                } else if (getHost() instanceof android.app.Fragment) {
                    PermissionHelper.newInstance((android.app.Fragment) getHost()).directRequestPermissions(
                            requestCode, permissions);
                } else if (getHost() instanceof Activity) {
                    PermissionHelper.newInstance((Activity) getHost()).directRequestPermissions(
                            requestCode, permissions);
                } else {
                    throw new RuntimeException("Host must be an Activity or Fragment!");
                }
            } else {
                notifyPermissionDenied(requestCode, permissions);
            }
        }


        private void notifyPermissionDenied(int requestCode, String... permissions) {
            if (mCallbacks != null) {
                mCallbacks.onPermissionsDenied(requestCode,
                        Arrays.asList(permissions));
            }
        }
    }
}
