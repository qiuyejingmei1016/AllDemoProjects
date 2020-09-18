package com.newrecord.cloud.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.newrecord.cloud.R;

/**
 * @describe: 提示音操作封装类
 * @author: yyh
 * @createTime: 2019/5/20 15:07
 * @className: PlaySoundUtil
 */
public class PlaySoundUtil {

    private static PlaySoundUtil mPlaySoundUtil;

    private MediaPlayer mMediaPlayer;

    private PlaySoundUtil() {

    }

    public static PlaySoundUtil getInstance() {
        if (mPlaySoundUtil == null) {
            mPlaySoundUtil = new PlaySoundUtil();
        }
        return mPlaySoundUtil;
    }

    /***
     * 播放提示音
     */
    public void playMusic(Context context,int resourceId) {
        mMediaPlayer = MediaPlayer.create(context, resourceId);
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
            }
        });
        mMediaPlayer.start();
    }

    /***
     * 停止播放
     */
    public void stopMusic() {
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        } catch (Exception e) {
            LogUtils.e("StopMusic Exception", e.toString());
        }
    }

    /**
     * 释放资源
     */
    public void releaseMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                stopMusic();
            }
            mMediaPlayer = null;
        }
        mPlaySoundUtil = null;
    }
}