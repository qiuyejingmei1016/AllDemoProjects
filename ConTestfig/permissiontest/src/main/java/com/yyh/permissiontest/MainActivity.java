package com.yyh.permissiontest;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yyh.permissiontest.permission.PermissionUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionUtil.PermissonCallBack{

    private String[] mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissions = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PermissionUtil.getInstance().needToFinsh(true).request(this, mPermissions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.requestCallBack(requestCode, permissions, grantResults, PermissionUtil.getInstance());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() == mPermissions.length) {
            Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "权限拒绝", Toast.LENGTH_SHORT).show();
        finish();
    }
}
