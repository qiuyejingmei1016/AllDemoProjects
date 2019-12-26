package com.yyh.mediarecorderdemo.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yyh.mediarecorderdemo.BuildConfig;
import com.yyh.mediarecorderdemo.Config;
import com.yyh.mediarecorderdemo.Mp4Recorder;
import com.yyh.mediarecorderdemo.R;
import com.yyh.mediarecorderdemo.UIAction;

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        MediaRecorder.OnErrorListener, Mp4Recorder.Callback, Runnable {

    private static final int MSG_1_MINUTES_LEFT = 2;
    private static final int MSG_SEVERAL_SECONDS_LEFT = 3;
    private String TAG = "===========";

    private MediaRecorder mMediaRecorder;
    private File mDestDir;
    private boolean mLandscape;
    private Mp4Recorder mMp4Recorder;
    private String mVideoPath;
    private Surface mMediaRecorderSurface;
    private CountDownTimer mCountDownTimer;
    private long mRecordStartTime;
    private TextView mTimeView;
    private Handler mHandler;
    private boolean mStoped;
    private Rect mRect;
    private View mRootView;

    private boolean mRecordDone;
    private long mVideoTime;
    private boolean mStartRecordOnResume;
    private boolean mInCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_start).setOnClickListener(this);
        findViewById(R.id.bt_end).setOnClickListener(this);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mRootView = findViewById(R.id.root_view);

        File rootDir = Environment.getExternalStorageDirectory();
        File userDataDir = new File(rootDir, "录屏测试");
        userDataDir.mkdirs();

        mDestDir = new File(userDataDir, "/videorecord");
        mDestDir.mkdirs();
        UIAction.setNoMediaFlag(mDestDir);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.bt_start) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!Mp4Recorder.checkRecordAudioPermission()) {
                        Toast.makeText(MainActivity.this, "检测到录音权限被禁用，请开启录音权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startRecord(false);
                }
            }, 200);
        } else if (viewId == R.id.bt_end) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            stop(true);
        }
    }

    private synchronized void stop(boolean ok) {
        if (mStoped) {
            return;
        }
        mStoped = true;
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "stop");
        }
        mInCapture = false;
        mRecordDone = ok;
        if (mMp4Recorder != null) {
            mMp4Recorder.stop();
            if (ok) {
                mVideoTime = SystemClock.uptimeMillis() - mRecordStartTime;
                Toast.makeText(this, "正在保存视频", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private synchronized void startRecord(boolean onResume) {
        if (mInCapture) {
            return;
        }
        if (onResume) {
            mStartRecordOnResume = true;
        } else {
            startCountDown();
        }
    }

    private void startCountDown() {
        mHandler = new Handler(new Callback());
        realStart();
    }


    private void realStart() {
        int[] size = getSize();
        mRect = new Rect(0, 0, size[0], size[1]);
        Thread captureThread = new Thread(this);
        captureThread.start();
        // startRecorder();
        mMp4Recorder = new Mp4Recorder();
        mMp4Recorder.setVideoWidth(size[0]);
        mMp4Recorder.setVideoHeight(size[1]);
        mMp4Recorder.setCallback(this);
        File saveFile = new File(mDestDir, "tmp.mp4");
        if (saveFile.exists()) {
            saveFile.delete();
        }
        mVideoPath = saveFile.getPath();
        mMp4Recorder.setSaveFilePath(mVideoPath);
        try {
            mMp4Recorder.start();
            mMediaRecorderSurface = mMp4Recorder.createVideoInputSurface();
        } catch (Exception e) {
            Toast.makeText(this, "录制时发生错误", Toast.LENGTH_SHORT).show();
            mInCapture = false;
//            finishFragmentOrActivity();
            return;
        }
        mCountDownTimer = new CountDownTimer(10 * 60 * 1000, 1000);
        mCountDownTimer.start();
        mRecordStartTime = SystemClock.uptimeMillis();
    }

    @Override
    public void run() {
        Bitmap bitmap;
        while (mInCapture) {
            if (mMediaRecorderSurface != null) {
                mRootView.setDrawingCacheEnabled(true);
                bitmap = mRootView.getDrawingCache(false);
                drawSurface(bitmap);
                mRootView.destroyDrawingCache();
            } else {
                SystemClock.sleep(5);
            }
        }
    }

    private void drawSurface(Bitmap bitmap) {
        if (!mInCapture) {
            return;
        }
        Canvas c = null;
        try {
            c = mMediaRecorderSurface.lockCanvas(null);
            c.drawBitmap(bitmap, null, mRect, null);
        } catch (Exception e) {
        } finally {
            if (c != null) {
                try {
                    mMediaRecorderSurface.unlockCanvasAndPost(c);
                } catch (Exception e) {
                }
            }
        }
    }

    private class Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_1_MINUTES_LEFT:
                    Toast.makeText(MainActivity.this, "距离录制结束还有一分钟", Toast.LENGTH_SHORT).show();
                    return true;
                case MSG_SEVERAL_SECONDS_LEFT:
                    Toast.makeText(MainActivity.this, getString(R.string.class_recorder_several_seconds_left,
                            message.arg1), Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }

    private class CountDownTimer extends android.os.CountDownTimer {

        private long mMillisInFuture;

        public CountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.mMillisInFuture = millisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long passed = mMillisInFuture - millisUntilFinished;
            int totalSeconds = Math.round(passed / 1000f);
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            mTimeView.setText(String.format("%02d'%02d\"", minutes, seconds));
            if (minutes == 9) {
                if (seconds == 0) {
                    mHandler.sendEmptyMessage(MSG_1_MINUTES_LEFT);
                } else if (seconds >= 50) {
                    mHandler.obtainMessage(MSG_SEVERAL_SECONDS_LEFT, 60 - seconds, 0).sendToTarget();
                }
            }
        }

        @Override
        public void onFinish() {
            mTimeView.setText("10'00\"");
            stop(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UIAction.setLandscape(this);
        if (mStartRecordOnResume) {
            mStartRecordOnResume = false;
//            startCountDown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mInCapture = false;
        if (mRootView != null) {
            mRootView.destroyDrawingCache();
        }
        mMediaRecorderSurface = null;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.e(TAG, String.format("onError(%d,%d)", what, extra));
        stop(false);
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        Toast.makeText(this, "录制时发生错误", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishRecordMP4() {
        if (mRecordDone) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("=======录制完成", mVideoPath + "\n" + mVideoTime);
                    Toast.makeText(MainActivity.this, "录制完成" + mVideoPath + "\n" + mVideoTime
                            , Toast.LENGTH_SHORT).show();
//                    intent.putExtra(PublishRecordedClassFragment.EXTRA_VIDEO_PATH, mVideoPath);
//                    intent.putExtra(PublishRecordedClassFragment.EXTRA_TIME, mVideoTime);
//                    intent.putExtra(BundleKeys.EXTRA_LANDSCAPE, mLandscape);
//                    intent.putExtra(BundleKeys.EXTRA_LANDSCAPE, mLandscape);
//
//                    intent.putExtra(BundleKeys.EXTRA_GROUP_TYPE, mGroupType);
//                    intent.putExtra(BundleKeys.EXTRA_MESSAGE_TYPE, mMessageType);
//                    //传递课程目录内新建微课目录信息
//                    intent.putExtra(BundleKeys.EXTRA_CHAPTER_INFO_TEXT, mChapterInfoText);
//                    startActivityAndFinishSelf(intent);
                }
            }, 1000);
        }
    }


    /**
     * 录屏宽高度
     */
    private int[] getSize() {
        int width;
        int height;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        int realHeigh = getScreenRealHeigh();
        int realWidth = getScreenRealWidth();
        if (realHeigh != 0) {
            screenHeight = realHeigh;
        }
        if (realWidth != 0) {
            screenWidth = realWidth;
        }
        if (Config.LOG_DEBUG) {
            Log.e("realSize ", realHeigh + "  " + realWidth);
            Log.e("screenSize ", screenHeight + "  " + screenWidth);
        }
        if (mLandscape) {
            width = Math.max(screenWidth, screenHeight);
            height = Math.min(screenWidth, screenHeight);
        } else {
            width = Math.min(screenWidth, screenHeight);
            height = Math.max(screenWidth, screenHeight);
        }
        return new int[]{width, height};
    }

    /**
     * 获取屏幕真实高度(适配部分机型因为底部虚拟键问题导致录屏画面出现压缩问题)
     */
    public int getScreenRealHeigh() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels;
            //虚拟键高度
//            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    public int getScreenRealWidth() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.widthPixels;
            //虚拟键高度
//            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }
}
