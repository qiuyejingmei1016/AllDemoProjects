package com.bairuitech.anychat.f2fvideo;

import android.app.Application;

/**
 * @describe: 应用实例
 * @author: AnyChat
 * @createTime: 2019/3/15 17:51
 * @className: AnyChatVideoApp
 */
public class AnyChatVideoApp extends Application {

    private String signaturePath;//签名底图sd卡绝对路径

    private String address;//地理位置信息

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}