package com.yyh.jsdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private WebView mWebView;
    private JavaScriptInterface mJavaScriptInterface;

    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == JavaScriptInterface.NO_PARAMETERS) {//js无参函数调java
                Toast.makeText(MainActivity.this, "Java被H5无参函数调用了", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "正在唤起微信", Toast.LENGTH_SHORT).show();
//                openApp("com.tencent.mm");
            } else if (msg.what == JavaScriptInterface.HAS_PARAMETERS) {//js有参函数调java
                String text = (String) msg.obj;
//                new AlertDialog.Builder(MainActivity.this).setMessage(text).show();
                Toast.makeText(MainActivity.this, "Java被H5有参函数调用了,以下是H5传递的参数\n\n" + text, Toast.LENGTH_LONG).show();
            }
        }
    };

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_js).setOnClickListener(this);

        mWebView = (WebView) findViewById(R.id.webview);
        // 启用javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 从assets目录下面的加载html
        mWebView.loadUrl("file:///android_asset/web.html");
//        mWebView.loadUrl("https://h5.anychat.net.cn/AnyChatFaceXClient/unifyVideoStatic/uploadPhoto.html");

        //设置js支持调用java本地代码
        mJavaScriptInterface = new JavaScriptInterface(mUiHandler);
        mWebView.addJavascriptInterface(mJavaScriptInterface, "jsObj");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置允许JS弹窗时，必须设置该回调(设置setWebChromeClient响应弹窗)，不然也无法打开JS Alert弹窗
        mWebView.setWebChromeClient(new WebChromeClient() /*{
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
//                build.setTitle("alert1");
                build.setMessage(message);
                build.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.confirm();
                    }
                });
                build.setCancelable(false);
                build.create().show();
                return true;
            }
        }*/);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn) {//java调用Js无参函数
            //无参数调用
            mWebView.loadUrl("javascript:javaCalljs()");
        } else if (viewId == R.id.btn_js) {//java调用Js有参函数
            //传递参数调用
            mWebView.loadUrl("javascript:javaCalljsWith(" + "'JAVA调用了JS的有参函数并传递值 www.baidu.com'" + ")");
        }
    }

    private void openApp(String packageName) {
        PackageManager packageManager = getPackageManager();
        if (checkPackInfo(packageName)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            intent.putExtra("name", "yyh");
            intent.putExtra("sex", "男");
            intent.putExtra("nation", "汉");
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "还没有安装,快去下载安装吧", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查app是否存在
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}