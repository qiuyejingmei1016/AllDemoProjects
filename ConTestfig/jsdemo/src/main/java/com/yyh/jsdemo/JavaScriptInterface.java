package com.yyh.jsdemo;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;


class JavaScriptInterface {

    private Handler mHandler;
    public static final int NO_PARAMETERS = 1;//js无参函数调java
    public static final int HAS_PARAMETERS = 2;//js有参函数调java

    public JavaScriptInterface(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @JavascriptInterface
    public void startFunction() {
        Message msg = mHandler.obtainMessage();
        msg.what = NO_PARAMETERS;
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        Message msg = mHandler.obtainMessage();
        msg.what = HAS_PARAMETERS;
        msg.obj = text;
        mHandler.sendMessage(msg);
    }
}
