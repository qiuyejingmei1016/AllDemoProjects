package com.bairuitech.anychat.recruitment.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.bairuitech.anychat.recruitment.R;

/**
 * @describe: 提示音操作封装类
 * @author: yyh
 * @createTime: 2019/5/20 15:07
 * @className: RecruitPlaySoundUtil
 */
public class RecruitPlaySoundUtil {

    private static RecruitPlaySoundUtil mRecruitPlaySoundUtil;

    private MediaPlayer mMediaPlayer;

    private RecruitPlaySoundUtil() {

    }

    public static RecruitPlaySoundUtil getInstance() {
        if (mRecruitPlaySoundUtil == null) {
            mRecruitPlaySoundUtil = new RecruitPlaySoundUtil();
        }
        return mRecruitPlaySoundUtil;
    }

    /***
     * 播放提示音
     */
    public void playMusic(Context context) {
        mMediaPlayer = MediaPlayer.create(context, R.raw.sdk_recruit_call);
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
            RecruitLogUtils.e("StopMusic Exception", e.toString());
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
        mRecruitPlaySoundUtil = null;
    }
}