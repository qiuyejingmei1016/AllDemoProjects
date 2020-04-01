package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bairuitech.anychat.f2fvideo.R;

/**
 * @describe: 服务选择界面
 * @author: yyh
 * @createTime: 2019/7/30 17:47
 * @className: SelectServiceActivity
 */
public class SelectServiceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        initView();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        ImageButton titleButton = (ImageButton) findViewById(R.id.title_left_img_btn);
        titleView.setText(R.string.select_service_title);
        titleButton.setImageResource(R.mipmap.ico_back);
        titleButton.setOnClickListener(this);

        findViewById(R.id.select_business_view).setOnClickListener(this);
        findViewById(R.id.select_reservate_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.title_left_img_btn) {
            finish();
        } else if (viewId == R.id.select_business_view) {//业务入口
            Intent intent = new Intent(this, BusinessActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.select_reservate_view) {//预约入口
            Intent intent = new Intent(this, ReservateBusinessActivity.class);
            startActivity(intent);
        }
    }
}