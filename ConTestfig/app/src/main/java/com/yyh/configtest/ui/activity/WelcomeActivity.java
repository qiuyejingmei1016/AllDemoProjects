package com.yyh.configtest.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yyh.configtest.R;
import com.yyh.configtest.utils.PermissionUtil;
import com.yyh.configtest.utils.UIAction;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener,
        PermissionUtil.PermissonCallBack {

    private static final long REDIRECT_DELAY = 1500;
    private ImageView mLogoView;
    private ImageView mWelcomeView;
    private String[] mPermissions;
    private String mViewString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {//如果是分享过来的要走这个intent跳到详情页
            Uri uri = intent.getData();
            if (uri != null) {
                String host = uri.getHost();
                String dataString = intent.getDataString();
                String id = uri.getQueryParameter("id");
                String path = uri.getPath();
                String encodedPath = uri.getEncodedPath();
                String productId = uri.getQuery();

                mViewString = "host: " + host + "\n" + "dataString: " + dataString + "\n" + "id: " + id
                        + "\n" + "type: " + path + "\n" + "encodedPath: " + encodedPath +
                        "\n" + "productId: " + productId;
            }
        }
    }

    private void initView() {
        mLogoView = (ImageView) findViewById(R.id.logo);
        mLogoView.setImageResource(R.drawable.ic_welcome_mashang);
        mWelcomeView = (ImageView) findViewById(R.id.welcome);
        mWelcomeView.setImageResource(R.drawable.bg_welcome);
        mWelcomeView.setOnClickListener(this);

        mPermissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PermissionUtil.getInstance().needToFinsh(true).request(this, mPermissions);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.welcome) {
            UIAction.makeToast(this, "click", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.requestCallBack(requestCode, permissions, grantResults, PermissionUtil.getInstance());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() == mPermissions.length) {
            doNext();
        }
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionUtil.showGoSettingDialog(this, perms);
    }


    private void doNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirect();
            }
        }, REDIRECT_DELAY);
    }

    private void redirect() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("view_string", mViewString);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //跳转到系统设置权限后回来到这里。resultCode = 0 data = null 不需要判断Activit ResultCode
        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_PERMISSION_ACTIVITY:
                if (PermissionUtil.hasPer(this, mPermissions)) {
                    doNext();
                    return;
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
