package com.yyh.configtest.ui.base;

import android.content.Intent;
import android.os.Bundle;

/**
 * Activity生命周期事件回调
 *
 * @author Kelvin Van
 */
public interface ActivityLifecycleListener {

    void onActivityStart();

    void onActivityResume();

    void onActivityRestart();

    void onActivityPause();

    void onActivityStop();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onActivityNewIntent(Intent intent);

    void onActivityDestroy();
}
