/*
 * 文件名: MGBaseFragmentActivity.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-2
 */
package com.yyh.configtest.ui.base;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.yyh.configtest.R;

/**
 * 基础FragmentActivity
 *
 * @author Kelvin Van
 */
@SuppressLint("Registered")
public abstract class MGBaseFragmentActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = null;
        UseFragment useFragment = getClass().getAnnotation(UseFragment.class);
        if (useFragment != null) {
            Class<? extends Fragment> fragmentClz = useFragment.value();
            fragment = newFragment(fragmentClz, getIntent());
        }
        if (fragment == null) {
            fragment = buildFragment();
        }
        if (fragment != null) {
            setContentView(R.layout.content_frame);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    protected static <T extends Fragment> T newFragment(Class<T> clz, Intent intent) {
        return NewFragmentUtil.newFragment(clz, intent.getExtras());
    }

    protected Fragment buildFragment() {
        return null;
    }

    protected boolean allowCreateWithoutUserInfo() {
        return false;
    }


    protected boolean isFinishedByBatchEnabled() {
        return !allowCreateWithoutUserInfo();
    }

    protected boolean isIsolatedFinishedEnabled() {
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        mStarted = false;
    }

    private boolean mStarted;

    public boolean isActivityStarted() {
        return mStarted;
    }

    private boolean mResumed;

    public boolean isActivityResumed() {
        return mResumed;
    }

    @TargetApi(11)
    protected void restartActivityCauseLanguageChanged() {
        recreate();
    }


    /**
     * 在UI线程上处理Intent
     */
    protected void receiveIntentOnUI(Intent intent) {
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return event.getKeyCode() != KeyEvent.KEYCODE_POWER && super.dispatchKeyEvent(event);
    }


    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
        }
    }

}