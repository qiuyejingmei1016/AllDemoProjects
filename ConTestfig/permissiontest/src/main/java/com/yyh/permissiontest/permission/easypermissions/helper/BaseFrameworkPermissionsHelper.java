package com.yyh.permissiontest.permission.easypermissions.helper;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Implementation of {@link PermissionHelper} for framework host classes.
 */
public abstract class BaseFrameworkPermissionsHelper<T> extends PermissionHelper<T> {

    public BaseFrameworkPermissionsHelper(@NonNull T host) {
        super(host);
    }

    public abstract FragmentManager getFragmentManager();

    @Override
    public void showRequestPermissionRationale(@NonNull String rationale,
                                               int positiveButton,
                                               int negativeButton,
                                               int requestCode,
                                               @NonNull String... perms) {
        if (getHost() instanceof Fragment) {
            PermissionHelper.newInstance((Fragment) getHost()).directRequestPermissions(
                    requestCode, perms);
        } else if (getHost() instanceof android.app.Fragment) {
            PermissionHelper.newInstance((android.app.Fragment) getHost()).directRequestPermissions(
                    requestCode,perms);
        } else if (getHost() instanceof Activity) {
            PermissionHelper.newInstance((Activity) getHost()).directRequestPermissions(
                    requestCode,perms);
        }
    }
}
