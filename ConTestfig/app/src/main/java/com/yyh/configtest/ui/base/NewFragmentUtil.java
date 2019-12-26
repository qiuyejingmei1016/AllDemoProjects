package com.yyh.configtest.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * 新建Fragment实例
 */
public class NewFragmentUtil {

    public static final String TAG = "NewFragmentUtil";

    @NonNull
    public static <T extends Fragment> T newFragment(Class<T> clz, Bundle args) {
//        if (BuildConfig.DEBUG) {
//            Log.debug(TAG, "newFragment clz: " + clz);
//        }
        try {
            T fragment = clz.newInstance();
            fragment.setArguments(args);
            return fragment;
        } catch (Exception e) {
            Log.e(TAG, "newFragment error, clz: " + clz);
            return null;
        }
    }
}