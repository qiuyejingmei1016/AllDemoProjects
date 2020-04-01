package com.bairuitech.anychat.recruitment.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bairuitech.anychat.main.AnyChatLinkCloseEvent;

/**
 * @describe: 基类Activity
 * @author: AnyChat
 * @createTime: 2019/3/22 14:14
 * @className: RecruitAnyChatBaseActivity
 */
public class RecruitAnyChatBaseActivity extends AppCompatActivity implements AnyChatLinkCloseEvent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLinkCloseStatus(int errorCode, String errorMsg) {
    }
}