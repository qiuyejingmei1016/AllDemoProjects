package com.yyh.application;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private String mDownLoadUrl = "http://clfile.imooc.com/class/assist/118/1328281/AsyncTask.pdf";
    private String mFileName; //下载的文件名称
    private ProgressDialog mProgressDialog;//正在下载进度条
    private DownloadAsyncTask mDownloadAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt3) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            mFileName = mDownLoadUrl.substring(mDownLoadUrl.lastIndexOf("/"));

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在下载");
            //设置进度条
            mProgressDialog.setMax(100);
            //设置初始值为0，其实可以不用设置，默认就是0
            mProgressDialog.incrementProgressBy(0);
            //是否精确显示对话框，flase为是，反之为否
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            //设置进度条样式
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mDownloadAsyncTask != null) {
                        mDownloadAsyncTask.cancel(false);
                    }
                }
            });

            mDownloadAsyncTask = new DownloadAsyncTask(mDownLoadUrl);
            mDownloadAsyncTask.setDownloadListener(new DownloadAsyncTask.DownloadListener() {
                @Override
                public void onDownloading(float progress) {
                    mProgressDialog.setProgress((int) progress);
                }

                @Override
                public void onDownloadComplete(String path) {
                    Log.e("===onDownloadComplete", path);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onDownloadError(String msg) {
                    Log.e("===onDownloadError", msg);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onDownloadCancel() {
                    Log.e("===onDownloadCancel", "===");
                }
            });
            mDownloadAsyncTask.execute(mFileName);
            mProgressDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadAsyncTask != null) {
            mDownloadAsyncTask.cancel(true);
            mDownloadAsyncTask = null;
        }
    }
}