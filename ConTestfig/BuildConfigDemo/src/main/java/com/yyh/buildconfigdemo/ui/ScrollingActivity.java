package com.yyh.buildconfigdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.yyh.buildconfigdemo.R;
import com.yyh.buildconfigdemo.utils.LogUtils;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogInfo();
            }
        });

        Log.e("============测试11111", "日志信息测试");
//        if (BuildConfig.LOG_DEBUG) {
            Log.e("============测试22222", "日志信息测试");
//        }
    }

    private void showLogInfo() {
        LogUtils.e(this, this.getClass().getName(), "test log print");

        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.enter_in_rightoleftt, R.anim.enter_outlefttoright);
    }
}