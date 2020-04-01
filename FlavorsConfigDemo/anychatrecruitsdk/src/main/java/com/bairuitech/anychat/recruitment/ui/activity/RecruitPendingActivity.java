package com.bairuitech.anychat.recruitment.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bairuitech.anychat.main.AnyChatSDK;
import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.logic.RecruitBundleKeys;

/**
 * @describe: 面试排队暂时挂起界面
 * @author: yyh
 * @createTime: 2020/2/18 11:34
 * @className: RecruitPendingActivity
 */
public class RecruitPendingActivity extends RecruitAnyChatBaseActivity implements View.OnClickListener {

    private AnyChatSDK mAnyChatSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_activity_recruit_pending);

        initView();
        initSDK();
    }

    private void initView() {
        findViewById(R.id.sdk_not_pende_view).setOnClickListener(this);
    }

    private void initSDK() {
        if (mAnyChatSDK == null) {
            mAnyChatSDK = AnyChatSDK.getInstance();
        }
        //注册LinkCloseEvent事件
        mAnyChatSDK.registerLinkCloseEvent(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sdk_not_pende_view) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    @Override
    public void onLinkCloseStatus(int errorCode, String errorMsg) {
        //注销LinkCloseEvent事件
        if (mAnyChatSDK != null) {
            mAnyChatSDK.unregisterLinkCloseEvent(this);
        }
        Intent intent = new Intent();
        intent.putExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE, true);
        intent.putExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_CODE, errorCode);
        intent.putExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_MSG, errorMsg);
        setResult(RESULT_OK, intent);
        finish();
    }

}
