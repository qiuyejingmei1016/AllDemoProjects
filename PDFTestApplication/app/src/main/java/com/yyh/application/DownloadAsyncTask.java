package com.yyh.application;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Float, Integer> {

    /****** 下载正常 ******/
    private static final int CORRECT = 0;
    /****** 404未找到资源 ******/
    private static final int NOT_FOUND = 1;
    /****** 下载异常 ******/
    private static final int ERROR = 2;
    /****** 取消下载 ******/
    private static final int CANCEL = 3;
    /****** SD异常 ******/
    private static final int SD_ERROR = 4;

    private DownloadListener mDownloadListener;
    private String mUrl;
    private File tempFile;

    public interface DownloadListener {

        public void onDownloading(float progress);

        public void onDownloadComplete(String path);

        public void onDownloadError(String msg);

        public void onDownloadCancel();

    }

    public DownloadAsyncTask(String url) {
        mUrl = url.replace("-", "%2D");
        mUrl = mUrl.replace(" ", "%20");
        mUrl = mUrl.replace("+", "%2B");
    }

    /**
     * 获取下载文件
     *
     * @param filename
     * @return
     */
    private static File getDownloadFile(String filename) {
        String downloadDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Download";
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, filename);
    }

    /**
     * 创建下载文件
     *
     * @param filename
     */
    private static File createDownloadFile(String filename) throws Exception {
        String downloadDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Download";
        File dir = new File(downloadDir);
        return createFile(dir, filename);
    }

    // 在指定SD目录创建文件
    private static File createFile(File dir, String filename) throws Exception {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        file.createNewFile();
        return file;
    }

    /**
     * 创建上传文件
     *
     * @return
     */
    public static File createUploadFile(String filename) throws Exception {
        String uploadDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Hbdownload";
        File dir = new File(uploadDir);
        return createFile(dir, filename);
    }

    @Override
    protected Integer doInBackground(String... params) {
        tempFile = getDownloadFile(params[0]);
//        if (tempFile.exists()) return CORRECT;
        try {
            tempFile = createDownloadFile(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return SD_ERROR;
        }
        FileOutputStream fos = null;
        InputStream istream = null;
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoInput(true);
            connection.setConnectTimeout(3000);
            connection.connect();
            if (tempFile.exists()) {
                long length = tempFile.length();
                if (connection.getContentLength() == length) {
                    return CORRECT;
                }
            }
            int statusCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == statusCode) {
                istream = connection.getInputStream();
                fos = new FileOutputStream(tempFile);
                long total = connection.getContentLength();
                int readTotal = 0;
                byte[] buffer = new byte[1024 * 10];
                int length;
                while ((length = istream.read(buffer)) > -1) {
                    if (isCancelled()) {
                        Log.e("================CANCEL", "====");
                        return CANCEL;
                    }
                    fos.write(buffer, 0, length);
                    readTotal += length;
                    float progress = readTotal / (total * 1.0f) * 100;
                    publishProgress(progress);
                }
                fos.flush();
                return CORRECT;
            }
            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return NOT_FOUND;
            }
            return ERROR;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ERROR;
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        if (mDownloadListener != null) {
            mDownloadListener.onDownloading(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (mDownloadListener != null) {
            switch (result) {
                case CORRECT:
                    mDownloadListener.onDownloadComplete(tempFile.getAbsolutePath());
                    break;
                case NOT_FOUND:
                    mDownloadListener.onDownloadError("http_not_found 404");
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    break;
                case ERROR:
                    mDownloadListener.onDownloadError("文件预览出现异常");
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    break;
                case SD_ERROR:
                    mDownloadListener.onDownloadError("SD卡出现异常，请检查您的设备上是否有SD卡");
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                case CANCEL:
                    mDownloadListener.onDownloadCancel();
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    break;
            }
        }
    }

    public void setDownloadListener(DownloadListener listener) {
        mDownloadListener = listener;
    }
}
