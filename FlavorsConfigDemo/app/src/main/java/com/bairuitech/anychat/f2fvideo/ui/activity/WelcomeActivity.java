package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.bairuitech.anychat.f2fvideo.AnyChatVideoApp;
import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.utils.FileUtils;
import com.bairuitech.anychat.f2fvideo.utils.PermissonUtils;

import java.util.List;

/**
 * @describe: app启动页
 * @author: yyh
 * @createTime: 2018/8/13 16:29
 * @className: WelcomeActivity
 */
public class WelcomeActivity extends AppCompatActivity implements PermissonUtils.PermissionCallback {

    private static final int REDIRECT_DELAY = 2000;//闪屏时间
    private String[] mPermissions;//请求权限
    private AlertDialog mPermissionDialog;//权限设置弹窗

    private AnyChatVideoApp mAnyChatVideoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        this.mAnyChatVideoApp = (AnyChatVideoApp) getApplication();

        setContentView(R.layout.activity_welcome);
        requestPermission();//请求权限
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        mPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};
        PermissonUtils.getInstance().setPermissionCallback(this);
        PermissonUtils.getInstance().needToFinsh(true).requestPermission(this, mPermissions);
    }

    /**
     * 权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissonUtils.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 权限获取完成
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() == mPermissions.length) {
            //保存签名模板底图到本地存储
            savaSignPicture();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    doNext();
                }
            }, REDIRECT_DELAY);
        }
    }

    /**
     * 保存签名模板底图到本地存储
     */
    private void savaSignPicture() {
        //将内置签名底图复制到本地存储  Android/data/cache/AnyChat/signature/signature.png
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ico_sign);
        String savePath = FileUtils.saveBitmap(FileUtils.getDiskCacheDir(this),
                "signature", bitmap);
        mAnyChatVideoApp.setSignaturePath(savePath);
    }

    /**
     * 权限被拒绝
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        mPermissionDialog = PermissonUtils.getInstance().showGoSettingDialog(this);
    }

    private void doNext() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PermissonUtils.PERMISSION_REQUESTCODE:
                if (PermissonUtils.getInstance().hasPermissions(this, mPermissions)) {
                    //保存签名模板底图到本地存储
                    savaSignPicture();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isFinishing()) {
                                return;
                            }
                            doNext();
                        }
                    }, REDIRECT_DELAY);
                    return;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionDialog != null) {
            if (mPermissionDialog.isShowing()) {
                mPermissionDialog.dismiss();
            }
            mPermissionDialog = null;
        }
    }
}