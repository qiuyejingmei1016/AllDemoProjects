package com.yyh.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private static final int SELECT_PDF_FILE_REQUEST_CODE = 3;

    private PDFView mPdfView;
    private EditText mInputView;

    private String mExterPath;//sd卡绝对路径
    private File mDownLoadFile;//下载文件
    private String mDownLoadUrl = "http://clfile.imooc.com/class/assist/118/1328281/AsyncTask.pdf";
    //    private String mDownLoadUrl = "http://120.79.14.225:8998/AnyChatFaceX/agent/file/pdf/contentBadFile.pdf";
    private String mPdfName; //下载的文件名称
    private ProgressDialog mProgressDialog;//正在下载进度条
    private int contentLen;

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
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        mPdfView = (PDFView) findViewById(R.id.pdf_view);
        mInputView = (EditText) findViewById(R.id.input);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt1) {
            openAssetsFile("Android.pdf", 0);//打开Assets下文件
        } else if (id == R.id.confirm) {//跳到指定页码
            if (mInputView != null && !TextUtils.isEmpty(mInputView.getText().toString())) {
                mPdfView.recycle();
                mPdfView.destroyDrawingCache();
                openAssetsFile("Android.pdf", Integer.parseInt(mInputView.getText().toString()) - 1);
            }
        } else if (id == R.id.bt2) {
            openLocalFile();
        } else if (id == R.id.bt3) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mExterPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            mPdfName = mDownLoadUrl.substring(mDownLoadUrl.lastIndexOf("/"));

            mProgressDialog = ProgressDialog.show(MainActivity.this, "", "正在下载");
            DownloadPDF downloadpdf = new DownloadPDF();
            downloadpdf.execute();
        } else if (id == R.id.bt4) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 打开Assets下文件
     *
     * @param pathName
     * @param page
     */
    private void openAssetsFile(String pathName, int page) {
        mPdfView.fromAsset(pathName)
                .defaultPage(page)
                .swipeHorizontal(false)
                .scrollHandle(new DefaultScrollHandle(this))
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("====onError====", t.toString());
                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .load();
    }

    /**
     * 选择本地文件打开
     */
    private void openLocalFile() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED

                    && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                ActivityCompat.requestPermissions(
                        this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            } else {
                selectLocalFile();
            }
        } else {
            selectLocalFile();
        }
    }

    private void selectLocalFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, SELECT_PDF_FILE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 展示本地所选择的文件
     *
     * @param uri
     */
    private void displayLocalFile(Uri uri) {
        String selectFileName = getSelectFileName(uri);
        if (TextUtils.isEmpty(selectFileName) || mPdfView == null) {
            return;
        }
        Toast.makeText(MainActivity.this, selectFileName, Toast.LENGTH_LONG).show();
        mPdfView.fromUri(uri)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("====onError====", t.toString());
                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    /**
     * 展示下载后的文件
     *
     * @param file
     */
    private void displayDownLoadFile(File file) {
        if (file == null || mPdfView == null) {
            return;
        }
        Toast.makeText(this, "FileName: " + file.getName() + "\n Path: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "FileName: " + file.getName() + "\n Path: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        mPdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .enableAnnotationRendering(true)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.e("====onError====", t.toString());
                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    /**
     * 权限获取结果回调
     */
    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE:
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectLocalFile();
                } else {
                    Toast.makeText(this, "请手动打开权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 文件选择回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            switch (requestCode) {
                case SELECT_PDF_FILE_REQUEST_CODE: {
                    Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    displayLocalFile(uri);
                    break;
                }
            }
        }
    }

    public String getSelectFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    class DownloadPDF extends AsyncTask<String, Float, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url;
            try {
                url = new URL(mDownLoadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setDoInput(true);
                conn.setConnectTimeout(3000);
                conn.connect();
                //获取下载文件的大小
                contentLen = conn.getContentLength();
                Log.e("======contentLen", contentLen + "");
                //根据下载文件大小设置进度条最大值（使用标记区别实时进度更新）
                publishProgress((float) contentLen);
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    byte[] bytes = new byte[1024];
                    final InputStream is = conn.getInputStream();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPdfView.fromStream(is)
//                                    .swipeHorizontal(false)
//                                    .scrollHandle(new DefaultScrollHandle(MainActivity.this))
//                                    .onError(new OnErrorListener() {
//                                        @Override
//                                        public void onError(Throwable t) {
//                                            Log.e("====onError====", t.toString());
//                                            Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .load();
//                        }
//                    });

                    mDownLoadFile = new File(mExterPath + "/", mPdfName);
                    if (!mDownLoadFile.exists()) {
                        mDownLoadFile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(mDownLoadFile);
                    int len = -1;
                    int progress = 0;
                    while ((len = is.read(bytes)) > 0) {
                        fos.write(bytes, 0, len);
                        fos.flush();
                        progress = progress + len;
                        publishProgress((float) progress);
                    }
                    fos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("==Exception", e.toString());
            } finally {

            }
            return "下载完成";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result值", result);
            mProgressDialog.dismiss();
            displayDownLoadFile(mDownLoadFile);
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            Log.e("=======onProgressUpdate", (int) ((values[0] / contentLen) * 100) + "%");
        }
    }
}