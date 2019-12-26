package com.yyh.mediarecorderdemo;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * MP4录制
 */
@TargetApi(16)
public class Mp4Recorder {

    public static final String TAG = "Mp4Recorder";
    private static final String VIDEO_MIME_TYPE;
    private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;

    static {
        // if (MiUIUtil.isMiUI()) {
        VIDEO_MIME_TYPE = "video/avc";
        /*} else {
            VIDEO_MIME_TYPE = "video/mp4v-es";
        }*/
    }

    private int mVideoWidth;
    private int mVideoHeight;
    private String mSaveFilePath;
    private MediaCodec mVideoEncoder;
    private MediaCodec mAudioEncoder;
    private AudioRecordTask mAudioRecordTask;
    private boolean mRecording;
    private MediaMuxer mMuxer;
    private MediaCodec.BufferInfo mVideoBufferInfo;
    private MediaCodec.BufferInfo mAudioBufferInfo;
    private ExecutorService mAudioEncodeService;
    private int mVideoTrackIndex;
    private int mAudioTrackIndex;
    private int mNumsTrackAdded;
    private volatile boolean mMuxerStarted;
    private Surface mInputSurface;
    private Callback mCallback;
    private int mWriteSampleDataSize;
    private boolean mVideoStopped;
    private boolean mAudioStopped;
    private boolean mFinished;
    private boolean mVideoStarted;
    private int mFixedNumsTrackAdded;

    private void printEncoderInfo() {
        int count = MediaCodecList.getCodecCount();
        MediaCodecInfo info;
        StringBuilder buf;
        for (int i = 0; i < count; i++) {
            info = MediaCodecList.getCodecInfoAt(i);
            if (info.isEncoder()) {
                buf = new StringBuilder();
                buf.append("Encoder:");
                buf.append(info.getName());
                buf.append(",");
                buf.append(Arrays.toString(info.getSupportedTypes()));
                MediaCodecInfo.CodecCapabilities cp = info.getCapabilitiesForType(info.getSupportedTypes()[0]);
                buf.append(",");
                buf.append(Arrays.toString(cp.colorFormats));
                Log.e(TAG, buf.toString());

                for (int j = 0; j < cp.colorFormats.length; j++) {
                    if (cp.colorFormats[j] == MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface) {
                        Log.e(TAG, "Encoder:supports COLOR_FormatSurface:" + info.getName());
                    }
                }
            }
        }
    }

    public void setVideoWidth(int videoWidth) {
        this.mVideoWidth = videoWidth;
    }

    public void setVideoHeight(int videoHeight) {
        this.mVideoHeight = videoHeight;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.mSaveFilePath = saveFilePath;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public static boolean checkRecordAudioPermission() {
        final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
        final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        final int samplesPerFrame = 2048; // codec-specific
        final int framesPerBuffer = 24; // 1 sec @ 1024 samples/frame (aac)
        int bufferSize = samplesPerFrame * framesPerBuffer;

        // Ensure buffer is adequately sized for the AudioRecord
        // object to initialize
        if (bufferSize < minBufferSize) {
            bufferSize = ((minBufferSize / samplesPerFrame) + 1) * samplesPerFrame * 2;
        }

        try {
            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,  // source
                    SAMPLE_RATE,                    // sample rate, hz
                    CHANNEL_CONFIG,                 // channels
                    AUDIO_FORMAT,                   // audio format
                    bufferSize);
            // buffer size (bytes)
            try {
                audioRecord.startRecording();
                boolean hasPermission = audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
                Log.e(TAG, "checkRecordAudioPermission: " + hasPermission);
                return hasPermission;
            } catch (Exception e) {
                Log.e(TAG, "checkRecordAudioPermission error", e);
                return false;
            } finally {
                try {
                    audioRecord.stop();
                } catch (Exception e) {
                    // DO NOTHING
                }
                try {
                    audioRecord.release();
                } catch (Exception e) {
                    // DO NOTHING
                }
            }
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "checkRecordAudioPermission: IllegalArgumentException ", ex);
            return false;
        }

    }

    public synchronized void start() throws Exception {
//        Logcat.clear();
        Log.e(TAG, "start");
        try {
            boolean hasAudioPermission = checkRecordAudioPermission();

            initVideoCodec();
            if (hasAudioPermission) {
                initAudioCodec();
                mFixedNumsTrackAdded = 2;
            } else {
                mFixedNumsTrackAdded = 1;
            }
            initMuxer();

            mRecording = true;
            mVideoBufferInfo = new MediaCodec.BufferInfo();
            if (hasAudioPermission) {
                mAudioBufferInfo = new MediaCodec.BufferInfo();
                mAudioRecordTask = new AudioRecordTask();
                mAudioEncodeService = Executors.newSingleThreadExecutor();
            } else {
                mAudioStopped = true;
            }
//            Logcat.capture(2000);
            new Thread(new VideoOutputTask()).start();
            if (hasAudioPermission) {
                new Thread(mAudioRecordTask).start();
            }
            Log.e(TAG, "start ok");
        } catch (Exception e) {
            Log.e(TAG, "start error", e);
            releaseVideoCodec();
            releaseAudioCodec();
            releaseMuxer(false);
            throw e;
        }
    }

    public synchronized void stop() {
        Log.e(TAG, "stop");
        mRecording = false;
        stopRecordingAudio();
    }

    private void initVideoCodec() throws IOException {
        Log.e(TAG, "initVideoCodec");
        if (Config.LOG_DEBUG) {
            printEncoderInfo();
        }
        final int FRAME_RATE = 25;
        //video/avc,这里的avc是高级视频编码Advanced Video Coding
        // mWidth和mHeight是视频的尺寸，这个尺寸不能超过视频采集时采集到的尺寸，否则会直接crash
        MediaFormat videoFormat = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, mVideoWidth, mVideoHeight);
        ////COLOR_FormatSurface这里表明数据将是一个graphicbuffer元数据
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        int bitrate = (int) (mVideoWidth * mVideoHeight * FRAME_RATE * 1 * 0.07f * 0.25f);
        Log.e(TAG, String.format("size: %d, %d; bitrate: %d", mVideoWidth, mVideoHeight, bitrate));
        //码流 码率，通常码率越高，视频越清晰，但是对应的视频也越大，
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        //帧数 帧率，通常这个值越高，视频会显得越流畅，一般默认我设置成30，你最低可以设置成24，不要低于这个值，低于24会明显卡顿
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        //关键帧 帧间隔，这是个很有意思的值，它指的是，关键帧的间隔时间。通常情况下，你设置成多少问题都不大。
        // 比如你设置成10，那就是10秒一个关键帧。但是，如果你有需求要做视频的预览，那你最好设置成1
        // 因为如果你设置成10，那你会发现，10秒内的预览都是一个截图
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //创建对应格式的编码器
        mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
        Log.e(TAG, "createEncoderByType " + mVideoEncoder.getName());
        mVideoEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        //获取Surface作为input，这句必须放在configure()之后，start()之前。
        mInputSurface = mVideoEncoder.createInputSurface();
        mVideoEncoder.start();
    }

    private void initAudioCodec() throws IOException {
        Log.e(TAG, "initAudioCodec");
        MediaFormat audioFormat = MediaFormat.createAudioFormat(AUDIO_MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
        mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
        mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
    }

    public Surface createVideoInputSurface() {
        return mInputSurface;
    }

    @TargetApi(18)
    private void initMuxer() throws IOException {
        mMuxer = new MediaMuxer(mSaveFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    private void stopRecordingAudio() {
        if (mAudioEncodeService != null && !mAudioEncodeService.isShutdown()) {
            mAudioEncodeService.submit(new AudioEncodeTask(this));
        }
    }

    private void releaseVideoCodec() {
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
    }

    private void releaseAudioCodec() {
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }
    }

    @TargetApi(18)
    private void releaseMuxer(boolean stop) {
        if (mMuxer != null) {
            if (stop) {
                mMuxer.stop();
            }
            mMuxer.release();
            mMuxer = null;
        }
    }

    private synchronized void checkFinish() {
        if (!mVideoStopped || !mAudioStopped || mFinished) {
            return;
        }
        Log.e(TAG, "finish, filesize: " + new File(mSaveFilePath).length());
        mFinished = true;
        releaseVideoCodec();
        releaseAudioCodec();
        releaseMuxer(mWriteSampleDataSize > 0);
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        generateThumbnail();
        if (mCallback != null) {
            mCallback.onFinishRecordMP4();
        }
    }

    private void onReadAudioData(byte[] buffer, long pts, int byteCount) {
        if (!mAudioEncodeService.isShutdown()) {
            mAudioEncodeService.submit(new AudioEncodeTask(this, buffer, pts, byteCount));
        }
    }

    private void offerAudioEncoder(byte[] audioData, long pts, int byteCount) {
        if (mAudioStopped) {
            return;
        }
        /*if (BuildConfig.DEBUG) {
            Log.debug(TAG, "offerAudioEncoder(" + pts + "," + byteCount + ")");
        }*/
        try {
            ByteBuffer[] inputBuffers = mAudioEncoder.getInputBuffers();
            int inputBufferIndex = mAudioEncoder.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (byteCount > 0) {
                    inputBuffer.put(audioData, 0, byteCount);
                    mAudioRecordTask.recycleInputBuffer(audioData);
                    mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, byteCount, pts, 0);
                    drainAudioEncoder(false);
                } else {
                    mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, 0, pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "offerAudioEncoder error", e);
        }
    }

    protected long getPTSUs() {
        return System.nanoTime() / 1000L;
    }

    private void finalizeAudioEncoder() {
        offerAudioEncoder(null, getPTSUs(), 0);
        drainAudioEncoder(true);
        mAudioEncodeService.shutdown();
        mAudioStopped = true;
        SystemClock.sleep(500);
        checkFinish();
    }

    @TargetApi(18)
    private void drainVideoEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 100;

        if (endOfStream) {
            mVideoEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] outputBuffers = mVideoEncoder.getOutputBuffers();
        int encoderStatus;
        ByteBuffer outputBuffer;
        while (true) {
            encoderStatus = mVideoEncoder.dequeueOutputBuffer(mVideoBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                // if (!endOfStream) {
                break;
                // }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                outputBuffers = mVideoEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                MediaFormat newFormat = mVideoEncoder.getOutputFormat();
                Log.e(TAG, "drainVideoEncoder INFO_OUTPUT_FORMAT_CHANGED: " + newFormat);
                // newFormat.setString("mime", VIDEO_MIME_TYPE);
                mVideoTrackIndex = mMuxer.addTrack(newFormat);
                mNumsTrackAdded++;
                if (mNumsTrackAdded == mFixedNumsTrackAdded) {
                    mMuxer.start();
                    mMuxerStarted = true;
                    Log.e(TAG, "Muxer started");
                }
            } else if (encoderStatus < 0) {
                Log.e(TAG, "unexpected result from drainVideoEncoder: " +
                        encoderStatus);
                // let's ignore it
            } else {
                if (!mMuxerStarted) {
                    mVideoEncoder.releaseOutputBuffer(encoderStatus, false);
                    break;
                }

                outputBuffer = outputBuffers[encoderStatus];
                if (outputBuffer == null) {
                    throw new RuntimeException("drainVideoEncoder encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    mVideoBufferInfo.size = 0;
                }

                /*if (BuildConfig.DEBUG) {
                    Log.debug(TAG, "drainVideoEncoder " + encoderStatus + ": " + mVideoBufferInfo.size + ", " + mVideoBufferInfo.flags);
                }*/

                if (mVideoBufferInfo.size != 0) {
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    outputBuffer.position(mVideoBufferInfo.offset);
                    outputBuffer.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);
                    mMuxer.writeSampleData(mVideoTrackIndex, outputBuffer, mVideoBufferInfo);
                    mWriteSampleDataSize += mVideoBufferInfo.size;
                }

                mVideoEncoder.releaseOutputBuffer(encoderStatus, false);

                mVideoStarted = true;

                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.e(TAG, "drainVideoEncoder reached end of stream unexpectedly");
                    }
                    break;      // out of while
                }
            }
        }
    }

    @TargetApi(18)
    private void drainAudioEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 100;

        /*if (endOfStream) {
            mAudioEncoder.signalEndOfInputStream();
        }*/

        ByteBuffer[] outputBuffers = mAudioEncoder.getOutputBuffers();
        int encoderStatus;
        ByteBuffer outputBuffer;
        while (true) {
            encoderStatus = mAudioEncoder.dequeueOutputBuffer(mAudioBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                // if (!endOfStream) {
                break;
                // }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                outputBuffers = mAudioEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                MediaFormat newFormat = mAudioEncoder.getOutputFormat();
                Log.e(TAG, "drainAudioEncoder INFO_OUTPUT_FORMAT_CHANGED: " + newFormat);
                mAudioTrackIndex = mMuxer.addTrack(newFormat);
                mNumsTrackAdded++;
                if (mNumsTrackAdded == mFixedNumsTrackAdded) {
                    mMuxer.start();
                    mMuxerStarted = true;
                    Log.e(TAG, "Muxer started");
                }
            } else if (encoderStatus < 0) {
                Log.e(TAG, "unexpected result from drainAudioEncoder: " +
                        encoderStatus);
                // let's ignore it
            } else {
                if (!mMuxerStarted) {
                    mAudioEncoder.releaseOutputBuffer(encoderStatus, false);
                    break;
                }

                outputBuffer = outputBuffers[encoderStatus];
                if (outputBuffer == null) {
                    throw new RuntimeException("drainAudioEncoder encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mAudioBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    mAudioBufferInfo.size = 0;
                }

                /*if (BuildConfig.DEBUG) {
                    Log.debug(TAG, "drainAudioEncoder " + encoderStatus + ": " + mAudioBufferInfo.size + ", " + mAudioBufferInfo.flags);
                }*/

                if (mAudioBufferInfo.size != 0 && mVideoStarted) {
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    outputBuffer.position(mAudioBufferInfo.offset);
                    outputBuffer.limit(mAudioBufferInfo.offset + mAudioBufferInfo.size);
                    mMuxer.writeSampleData(mAudioTrackIndex, outputBuffer, mAudioBufferInfo);
                    mWriteSampleDataSize += mAudioBufferInfo.size;
                }

                mAudioEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mAudioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.e(TAG, "drainAudioEncoder reached end of stream unexpectedly");
                    }
                    break;      // out of while
                }
            }
        }
    }

    private void generateThumbnail() {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(mSaveFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
        } catch (Exception e) {
            Log.e(TAG, "generateThumbnail error", e);
        } catch (OutOfMemoryError oom) {
            Log.e(TAG, "generateThumbnail error", oom);
            System.gc();
        }
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(getThumbnailPath(mSaveFilePath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            } catch (Exception e) {
                Log.e(TAG, "generateThumbnail compress error", e);
            } finally {
                UIAction.close(fos);
            }
            bitmap.recycle();
        }
    }

    public static String getThumbnailPath(String filePath) {
        return filePath + ".thumb";
    }

    private class AudioRecordTask implements Runnable {

        private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
        private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        private ArrayBlockingQueue<byte[]> mDataBuffer = new ArrayBlockingQueue<byte[]>(50);

        @Override
        public void run() {
            int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            final int samplesPerFrame = 2048; // codec-specific
            final int framesPerBuffer = 24; // 1 sec @ 1024 samples/frame (aac)
            int bufferSize = samplesPerFrame * framesPerBuffer;

            // Ensure buffer is adequately sized for the AudioRecord
            // object to initialize
            if (bufferSize < minBufferSize) {
                bufferSize = ((minBufferSize / samplesPerFrame) + 1) * samplesPerFrame * 2;
            }

            for (int x = 0; x < 25; x++) {
                mDataBuffer.add(new byte[samplesPerFrame]);
            }

            drainAudioEncoder(false);

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,  // source
                    SAMPLE_RATE,                    // sample rate, hz
                    CHANNEL_CONFIG,                 // channels
                    AUDIO_FORMAT,                   // audio format
                    bufferSize);                    // buffer size (bytes)
            audioRecord.startRecording();

            long audioPresentationTimeNs;
            byte[] buffer;
            int read;
            while (mRecording) {
                if (!mVideoStarted) {
                    continue;
                }
                audioPresentationTimeNs = getPTSUs();
                if (mDataBuffer.isEmpty()) {
                    buffer = new byte[samplesPerFrame];
                } else {
                    buffer = mDataBuffer.poll();
                }
                read = audioRecord.read(buffer, 0, samplesPerFrame);
                if (read == AudioRecord.ERROR_BAD_VALUE || read == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "AudioRecord read error: " + read);
                }
                onReadAudioData(buffer, audioPresentationTimeNs, read);
            }

            audioRecord.stop();
            audioRecord.release();
        }

        void recycleInputBuffer(byte[] buffer) {
            mDataBuffer.offer(buffer);
        }
    }

    private static class AudioEncodeTask implements Runnable {

        private static final int TYPE_FINALIZE_ENCODER = 0;
        private static final int TYPE_ENCODE_FRAME = 1;
        private Mp4Recorder mMp4Recoder;
        private byte[] mAudioData;
        private long mPresentationTimeNs;
        private int mType;
        private boolean mExecuted;
        private int mByteCount;

        AudioEncodeTask(Mp4Recorder m) {
            this.mMp4Recoder = m;
            this.mType = TYPE_FINALIZE_ENCODER;
        }

        AudioEncodeTask(Mp4Recorder m, byte[] audioData, long presentationTimeNs, int byteCount) {
            this.mMp4Recoder = m;
            this.mAudioData = audioData;
            this.mPresentationTimeNs = presentationTimeNs;
            this.mByteCount = byteCount;
            this.mType = TYPE_ENCODE_FRAME;
        }

        @Override
        public void run() {
            if (mExecuted) {
                return;
            }
            switch (mType) {
                case TYPE_ENCODE_FRAME:
                    if (mAudioData != null) {
                        mMp4Recoder.offerAudioEncoder(mAudioData, mPresentationTimeNs, mByteCount);
                    }
                    mAudioData = null;
                    break;
                case TYPE_FINALIZE_ENCODER:
                    mMp4Recoder.finalizeAudioEncoder();
                    break;
            }
            mExecuted = true;
        }
    }

    private class VideoOutputTask implements Runnable {

        @Override
        public void run() {
            while (mRecording) {
                drainVideoEncoder(false);
            }
            drainVideoEncoder(true);
            mVideoStopped = true;
            checkFinish();
        }
    }

    public interface Callback {
        void onFinishRecordMP4();
    }
}