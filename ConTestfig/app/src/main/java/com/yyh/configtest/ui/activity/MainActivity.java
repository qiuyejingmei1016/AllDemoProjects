package com.yyh.configtest.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yyh.configtest.R;
import com.yyh.configtest.ui.base.NormalActivity;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_OK = 1;
    private static final int PHONE_STATE_OK = 2;
    private static final int REQUEST_CODE = 1;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        requestPermission();

        Intent intent = getIntent();
        String view_string = intent.getStringExtra("view_string");

        ((TextView) findViewById(R.id.text_view)).setText(this.getClass().getName());
        TextView urlInfoView = (TextView) findViewById(R.id.view_url);
        if (!TextUtils.isEmpty(view_string)) {
            urlInfoView.setText(view_string);
        }
        mButton = (Button) findViewById(R.id.test);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NormalActivity.actionTestFragment(MainActivity.this, "测试一下 a --> f");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE:
                String test_data = data.getStringExtra("test_data");
                if (!TextUtils.isEmpty(test_data)) {
                    mButton.setText("接收数据最终展示: " + test_data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // 先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_OK);
            } else {
                // 说明已经获取到CAMERA权限了 想干嘛干嘛
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // 先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_OK);
            } else {
                // 说明已经获取到CAMERA权限了 想干嘛干嘛
            }
        } else {
            // 这个说明系统版本在6.0之下，不需要动态获取权限。
        }
    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
            case PHONE_STATE_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 这里已经获取到 CAMERA 权限，想干嘛干嘛了可以
                } else {
                    // 这里是拒绝给APP CAMERA 权限，给个提示什么的说明一下都可以。
                    Toast.makeText(this, "请手动打开摄像头权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
