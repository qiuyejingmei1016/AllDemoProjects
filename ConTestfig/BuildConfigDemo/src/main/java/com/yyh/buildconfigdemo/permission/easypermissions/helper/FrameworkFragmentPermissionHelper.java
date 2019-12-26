package com.yyh.buildconfigdemo.permission.easypermissions.helper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Permissions helper for {@link Fragment} from the framework.
 */
class FrameworkFragmentPermissionHelper extends BaseFrameworkPermissionsHelper<Fragment> {

    public FrameworkFragmentPermissionHelper(@NonNull Fragment host) {
        super(host);
    }

    @Override
    public FragmentManager getFragmentManager() {
        return getHost().getChildFragmentManager();
    }

    @Override
    @SuppressLint("NewApi")
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        getHost().requestPermissions(perms, requestCode);
    }

    @Override
    @SuppressLint("NewApi")
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return getHost().shouldShowRequestPermissionRationale(perm);
    }

    @Override
    @SuppressLint("NewApi")
    public Context getContext() {
        return getHost().getActivity();
    }
}
