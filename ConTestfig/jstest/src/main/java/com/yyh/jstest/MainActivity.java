package com.yyh.jstest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String sex = intent.getStringExtra("sex");
        String nation = intent.getStringExtra("nation");

        mTextView.setText("传递信息\nname: " + name + "\nsex: " + sex + "\nnation: " + nation);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initView() {
        mTextView = (TextView) findViewById(R.id.text_view);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/web.html");
        mWebView.addJavascriptInterface(MainActivity.this, "yyh");
    }

    @JavascriptInterface
    public void startFunction() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openApp("com.anychat.zhonyuan");
            }
        });
    }

    @JavascriptInterface
    public void startFunction(String json) {
        Log.e("=========", json);
        Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void startFunction2(String json) {
        Log.e("=========", json);
        Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "没有安装" + packageName, Toast.LENGTH_SHORT).show();
        }
    }

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