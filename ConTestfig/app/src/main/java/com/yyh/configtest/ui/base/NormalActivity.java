package com.yyh.configtest.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.yyh.configtest.R;
import com.yyh.configtest.ui.fragment.FragmentTest2;
import com.yyh.configtest.ui.fragment.TestFragment;
import com.yyh.configtest.utils.UIAction;

/**
 * 默认Activity
 */
public class NormalActivity extends MGBaseFragmentActivity {

    public static final String EXTRA_FRAGMENT_CLASS = "na_zz";
    public static final String EXTRA_SOFT_INPUT_MODE = "na_sim";
    private static final String EXTRA_HANDLE_BACK_PRESSED_EVENT = "na_back";

    private static final String TAG = "NormalActivity";
    private boolean mHandleBackPressedEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String fragmentClass = intent.getStringExtra(EXTRA_FRAGMENT_CLASS);
        Class<? extends Fragment> clz;
        try {
            clz = (Class<? extends Fragment>) Class.forName(fragmentClass);
        } catch (Exception e) {
            Log.e(TAG, String.format("forName(%s) error", fragmentClass), e);
            finish();
            return;
        }

        setContentView(R.layout.content_frame);

        this.mHandleBackPressedEvent = intent.getBooleanExtra(EXTRA_HANDLE_BACK_PRESSED_EVENT, false);

        if (intent.hasExtra(EXTRA_SOFT_INPUT_MODE)) {
            getWindow().setSoftInputMode(intent.getIntExtra(EXTRA_SOFT_INPUT_MODE, 0));
        }
        Fragment fragment = newFragment(clz, intent);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (!mHandleBackPressedEvent || !UIAction.handleBackPressedEvent(this)) {
            super.onBackPressed();
        }
    }

    private static Intent newIntent(Context context, Class<? extends Fragment> fragmentClass) {
        return newIntent(context, fragmentClass, null);
    }

    private static Intent newIntent(Context context, Class<? extends Fragment> fragmentClass, Bundle extras) {
        Intent intent = new Intent(context, NormalActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass.getName());
        return intent;
    }


    private static Intent setHandleBackPressedEvent(Intent intent) {
        return intent.putExtra(EXTRA_HANDLE_BACK_PRESSED_EVENT, true);
    }


    public static Intent actionTestFragment(Context context, String text) {
        Intent intent = newIntent(context, TestFragment.class);
        intent.putExtra("string", text);
        setHandleBackPressedEvent(intent);
        return intent;
    }

    public static Intent actionTest2Fragment(Context context, String text) {
        Intent intent = newIntent(context, FragmentTest2.class);
        intent.putExtra("string", text);
        setHandleBackPressedEvent(intent);
        return intent;
    }
}