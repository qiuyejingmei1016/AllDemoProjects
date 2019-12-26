
package com.yyh.configtest.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.yyh.configtest.utils.UIAction;

import java.util.ArrayList;

/**
 * Base Fragment Activity
 *
 * @author Kelvin Van
 */
abstract class BaseFragmentActivity extends FragmentActivity {

    private Toast mToast;

    private boolean mDestroyed;
    private ArrayList<ActivityLifecycleListener> mLifecycleListeners;

    protected synchronized final void registerLifecycleListener(ActivityLifecycleListener listener) {
        if (mDestroyed) {
            return;
        }
        if (mLifecycleListeners == null) {
            mLifecycleListeners = new ArrayList<ActivityLifecycleListener>();
        }
        if (!mLifecycleListeners.contains(listener)) {
            mLifecycleListeners.add(listener);
        }
    }

    protected synchronized final void unregisterLifecycleListener(ActivityLifecycleListener listener) {
        if (mLifecycleListeners != null) {
            mLifecycleListeners.remove(listener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityResume();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityRestart();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityPause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityStop();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mLifecycleListeners != null && !mLifecycleListeners.isEmpty()) {
            for (ActivityLifecycleListener listener : mLifecycleListeners) {
                listener.onActivityNewIntent(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        if (mLifecycleListeners != null) {
            if (!mLifecycleListeners.isEmpty()) {
                for (ActivityLifecycleListener listener : mLifecycleListeners) {
                    listener.onActivityDestroy();
                }
            }
            mLifecycleListeners.clear();
        }
    }

    public void showToast(int resId) {
        showToast(getText(resId));
    }

    public void showToast(CharSequence text) {
        if (mToast == null) {
            mToast = UIAction.makeToast(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showLongToast(int resId) {
        showLongToast(getText(resId));
    }

    public void showLongToast(CharSequence text) {
        if (mToast == null) {
            mToast = UIAction.makeToast(this, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

}