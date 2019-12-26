/*
 * 文件名: ClassRecorderFragment.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2016-6-6
 */
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
import android.support.annotation.Nullable;
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


/**
 * @describe: 录制测试
 * @author: yyh
 * @createTime: 2018/6/11 11:38
 * @className: ClassRecorderTestFragment
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener,
        Runnable, MediaRecorder.OnErrorListener,
        Mp4Recorder.Callback {

    private static final int MSG_COUNT_DOWN = 1;
    private static final int MSG_1_MINUTES_LEFT = 2;
    private static final int MSG_SEVERAL_SECONDS_LEFT = 3;

    private static final String TAG = "ClassRecorderFragment";
    private boolean mLandscape;
    private TextView mTimeView;

    private MediaRecorder mMediaRecorder;
    private String mVideoPath;
    private boolean mInCapture;
    private Surface mMediaRecorderSurface;
    private Rect mRect;
    private Handler mHandler;
    private boolean mStartRecordOnResume;
    private CountDownTimer mCountDownTimer;
    private long mRecordStartTime;
    private File mDestDir;
    private boolean mStoped;
    private Mp4Recorder mMp4Recorder;
    private boolean mRecordDone;
    private long mVideoTime;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_recorder_test);

        mTimeView = (TextView) findViewById(R.id.time);
        mRootView = findViewById(R.id.root_view);

        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);

        File rootDir = Environment.getExternalStorageDirectory();
        File userDataDir = new File(rootDir, "V校录屏测试");
        userDataDir.mkdirs();

        mDestDir = new File(userDataDir, "/videorecord");
        mDestDir.mkdirs();
        UIAction.setNoMediaFlag(mDestDir);

    }

    /**
     * 开始录制
     *
     * @param onResume
     */
    private synchronized void startRecord(boolean onResume) {
        if (mInCapture) {
            return;
        }
        mInCapture = true;
        if (onResume) {
            mStartRecordOnResume = true;
        } else {
            startCountDown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStartRecordOnResume) {
            mStartRecordOnResume = false;
            startCountDown();
        }
    }

    /**
     * 倒计时
     */
    private void startCountDown() {
        mHandler = new Handler(new Callback());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
            }
        });
    }

    private void realStart() {
        int[] size = getSize();
        mRect = new Rect(0, 0, size[0], size[1]);
        Thread captureThread = new Thread(this);
        captureThread.start();
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
//            showLongToast(class_recorder_err_failed);
            mInCapture = false;
//            finishFragmentOrActivity();
            return;
        }
        mCountDownTimer = new CountDownTimer(10 * 60 * 1000, 1000);
        mCountDownTimer.start();
        mRecordStartTime = SystemClock.uptimeMillis();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.start) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!Mp4Recorder.checkRecordAudioPermission()) {
                        Toast.makeText(TestActivity.this, "检测到录音权限被禁用，请开启录音权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startRecord(false);
                }
            }, 200);
        } else if (viewId == R.id.stop) {
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

    @Override
    public void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                    Toast.makeText(TestActivity.this, "录制完成" + mVideoPath + "\n" + mVideoTime
                            , Toast.LENGTH_SHORT).show();
//                    Bundle args = getArguments();
//                    String groupId = args.getString(BundleKeys.EXTRA_GROUP_ID);
//                    String groupNumber = args.getString(BundleKeys.EXTRA_GROUP_NUMBER);
//                    String groupName = args.getString(BundleKeys.EXTRA_GROUP_NAME);
//                    String groupType = args.getString(BundleKeys.EXTRA_GROUP_TYPE);
//                    Intent intent = PublishMessage.actionPublish(getActivity(), groupId, groupNumber
//                            , groupName, groupType, Constants.AppType.RECORDED_CLASS);
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

    @Override
    public void run() {
        Bitmap bitmap;
        while (mInCapture) {
            if (mMediaRecorderSurface != null) {
//                mImagesPager.setDrawingCacheEnabled(true);
//                bitmap = mImagesPager.getDrawingCache(false);
//                drawSurface(bitmap);
//                mImagesPager.destroyDrawingCache();
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
                case MSG_COUNT_DOWN:
                    realStart();
                    return true;
                case MSG_1_MINUTES_LEFT:
                    Toast.makeText(TestActivity.this, "距离录制结束还有一分钟", Toast.LENGTH_SHORT).show();
                    return true;
                case MSG_SEVERAL_SECONDS_LEFT:
                    Toast.makeText(TestActivity.this, getString(R.string.class_recorder_several_seconds_left,
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
        mMediaRecorderSurface = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}