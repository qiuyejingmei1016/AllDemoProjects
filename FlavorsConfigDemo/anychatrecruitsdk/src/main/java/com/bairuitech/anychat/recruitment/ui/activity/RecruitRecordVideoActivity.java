package com.bairuitech.anychat.recruitment.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.main.AnyChatCallbackEvent;
import com.bairuitech.anychat.main.AnyChatResult;
import com.bairuitech.anychat.main.AnyChatSDK;
import com.bairuitech.anychat.microphone.AnyChatMicrophone;
import com.bairuitech.anychat.queue.AnyChatLeaveAreaEvent;
import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.logic.BRRecruitSDK;
import com.bairuitech.anychat.recruitment.logic.RecruitBundleKeys;
import com.bairuitech.anychat.recruitment.logic.RecruitRequestField;
import com.bairuitech.anychat.recruitment.logic.config.RecruitApiHelper;
import com.bairuitech.anychat.recruitment.logic.model.RecruitChatMessageModel;
import com.bairuitech.anychat.recruitment.logic.model.RecruitGlobalConfig;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitBusinessModel;
import com.bairuitech.anychat.recruitment.ui.adapter.RecruitChatAdapter;
import com.bairuitech.anychat.recruitment.ui.view.RecruitCommonDialog;
import com.bairuitech.anychat.recruitment.ui.view.RecruitCompleteDialog;
import com.bairuitech.anychat.recruitment.utils.RecruitCustomInputFilter;
import com.bairuitech.anychat.recruitment.utils.RecruitLogUtils;
import com.bairuitech.anychat.recruitment.utils.StringUtil;
import com.bairuitech.anychat.recruitment.utils.UIAction;
import com.bairuitech.anychat.room.AnyChatRoomEvent;
import com.bairuitech.anychat.transfer.AnyChatReceiveBufferEvent;
import com.bairuitech.anychat.transfer.AnyChatTransBufferReceivedEvent;
import com.bairuitech.anychat.util.json.JSONObject;
import com.bairuitech.anychat.video.AnyChatCamera;
import com.bairuitech.anychat.video.AnyChatVideoCallEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @describe: 视频面试视频交互界面
 * @author: yyh
 * @createTime: 2018/8/7 11:53
 * @className: RecruitRecordVideoActivity
 */
public class RecruitRecordVideoActivity extends RecruitAnyChatBaseActivity implements View.OnClickListener,
        AnyChatVideoCallEvent, AnyChatRoomEvent, AnyChatReceiveBufferEvent {

    private static final int MSG_HIDE_AUDIO_MODE_TIP = 1;//handler msg 隐藏语音模式切换提示view

    private LinearLayout mLocalView;//本地视频展示view
    private LinearLayout mOtherView;//远程视频展示view
    private ImageView mVoiceView;//本地麦克风控制view
    private View mChatRootView;//聊天父布局view
    private EditText mChatInputView;//聊天输入view
    private ListView mChatListView;//聊天展示view
    private View mRecruitAudioModeView;//音频模式展示view
    private TextView mRecruitModeSwitchView;//视频音频切换展示提示view

    private AnyChatSDK mAnyChatSDK;
    private AnyChatMicrophone mMicrophone;//本地麦克风控制
    private AnyChatCamera mCamera;//本地摄像头控制

    private int mSelfUserId;//anychat登录后自己的id

    private int mAgnetId = -1;//对方远程用户id
    private String mRoomId;//房间id

    //软引用避免内存泄露
    private final Handler mHandler = new WeakHandler(this);
    private boolean mVoiceHasOpen = true;//语音开关标识

    private int mStreamIndex = 0;//流号(远程画面)
    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度
    private boolean mIsLandScape;//是否横屏
    private int mLocalViewWidth; //本地视频view宽度
    private int mLocalViewHeight;//本地视频view高度

    private RecruitCommonDialog mComDialog;//退出提示弹窗
    private RecruitCompleteDialog mRecruitCompleteDialog;//面试完成提示弹窗

    private List<RecruitChatMessageModel> mChatJsonLists = new ArrayList<RecruitChatMessageModel>();//聊天数据集合
    private RecruitChatAdapter mRecruitChatAdapter;//聊天内容展示适配器
    private String mAgentOrgName;//面试官职位信息
    private boolean mEndVideoBySelf;//自己手动结束挂断视频

    private RecruitCommonDialog getComDialog() {
        if (mComDialog == null) {
            mComDialog = RecruitCommonDialog.getInstance();
        }
        return mComDialog;
    }

    private RecruitCompleteDialog getCompleteDialog() {
        if (mRecruitCompleteDialog == null) {
            mRecruitCompleteDialog = RecruitCompleteDialog.getInstance();
        }
        return mRecruitCompleteDialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启全屏模式
//        UIAction.setFullScreenOn(getWindow());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //关闭全屏模式
//        UIAction.setFullScreenOff(getWindow());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_recruit_activity_record_video);
        initData();
        initView();
        initSDK();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra(RecruitBundleKeys.EXTRA_REMOTE_USER_ID)) {
            this.mAgnetId = intent.getIntExtra(RecruitBundleKeys.EXTRA_REMOTE_USER_ID, -1);
        }
        if (intent.hasExtra(RecruitBundleKeys.EXTRA_ROOM_ID)) {
            this.mRoomId = intent.getStringExtra(RecruitBundleKeys.EXTRA_ROOM_ID);
        }
        //获取面试官职位信息
        RecruitGlobalConfig config = RecruitGlobalConfig.getInstance();
        RecruitBusinessModel model = config.getRecruitBusinessModel();
        if (model != null) {
            mAgentOrgName = model.getAgentOrgName();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        this.mScreenWidth = UIAction.getScreenRealWidth(this);
        this.mScreenHeight = UIAction.getScreenRealHeigh(this);

        this.findViewById(R.id.sdk_title_left_img_btn).setOnClickListener(this);
        this.findViewById(R.id.sdk_switch_view).setOnClickListener(this);
        this.findViewById(R.id.sdk_end_recruit_view).setOnClickListener(this);
        //音频模式展示view
        this.mRecruitAudioModeView = findViewById(R.id.sdk_audio_mode_root_view);
        //视频音频切换展示提示view
        this.mRecruitModeSwitchView = (TextView) findViewById(R.id.sdk_recruit_mode_switch_view);
        //标题栏提示view
        TextView recruitVideoTitleView = (TextView) findViewById(R.id.sdk_recruit_video_title_text);
        if (StringUtil.isNullOrEmpty(mAgentOrgName)) {
            mAgentOrgName = "面试官";
        }
        recruitVideoTitleView.setText(getString(R.string.sdk_recruit_video_interviewer_jod, mAgentOrgName));
        //本地视频展示view
        this.mLocalView = (LinearLayout) findViewById(R.id.sdk_ll_local_view);
        this.mLocalView.setOnTouchListener(mTouchListener);
        //远程视频展示view
        this.mOtherView = (LinearLayout) findViewById(R.id.sdk_ll_other_view);
        //本地麦克风控制view
        this.mVoiceView = (ImageView) findViewById(R.id.sdk_voice_view);
        this.mVoiceView.setOnClickListener(this);

        //聊天父布局
        this.mChatRootView = findViewById(R.id.sdk_chat_root_view);
        //聊天折起view
        this.findViewById(R.id.sdk_hide_chat_view).setOnClickListener(this);
        //聊天输入框
        this.mChatInputView = (EditText) findViewById(R.id.sdk_edit_view);
        //设置输入框过滤(过滤不可输入表情、特殊字符)
        this.mChatInputView.setFilters(new InputFilter[]{new RecruitCustomInputFilter(this)});
        //EditText点击展示聊天内容
        this.mChatInputView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //ACTION_DOWN ACTION_MOVE ACTION_UP
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mChatRootView != null && mChatRootView.getVisibility() != View.VISIBLE) {
                        mChatRootView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        //发送
        this.findViewById(R.id.sdk_send_view).setOnClickListener(this);
        //聊天展示
        this.mChatListView = (ListView) findViewById(R.id.sdk_list_view);
        this.mRecruitChatAdapter = new RecruitChatAdapter(this, mChatJsonLists);
        this.mChatListView.setAdapter(mRecruitChatAdapter);

        //获取本地视频view宽高
        if (mLocalView.getLayoutParams() != null) {
            mLocalViewWidth = mLocalView.getLayoutParams().width;
            mLocalViewHeight = mLocalView.getLayoutParams().height;
        }
        adjustRemoteVideo(false);//initview初始化
    }

    private void initSDK() {
        mAnyChatSDK = AnyChatSDK.getInstance();
        this.mSelfUserId = mAnyChatSDK.myUserid;
        //注册LinkCloseEvent事件
        mAnyChatSDK.registerLinkCloseEvent(this);
        //注册视频呼叫事件
        mAnyChatSDK.registerVideoCallEvent(this);
        //注册Room房间管理事件
        mAnyChatSDK.registerRoomEvent(this);
        //注册接受透明通道通知事件
        mAnyChatSDK.registerTransBufferEvent(this);
        //进入房间
        mAnyChatSDK.enterRoom(mRoomId, "", new AnyChatCallbackEvent() {
            @Override
            public void onCallbackEvent(AnyChatResult result, JSONObject JsonData) {
                if (result != null && 0 == result.errCode) {
                    openRemoteDevice(mStreamIndex);//进入房间打开远程设备
                    openLocalDevice();//进入房间打开本地设备
                } else {
                    BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(
                            new AnyChatResult(100907, "进入房间失败"));
                    hungupAndFinish();//进入房间失败
                }
            }
        });
    }

    /**
     * 打开远程设备
     */
    private void openRemoteDevice(int streamIndex) {
        if (mAnyChatSDK != null) {
            List<Integer> roomUsers = mAnyChatSDK.getRoomUsers();
            for (int userId : roomUsers) {
                if (userId == mAgnetId) {
                    if (streamIndex == 0) {
                        mAnyChatSDK.getRemoteVideoStream(this, mAgnetId, mOtherView, false);
                        mAnyChatSDK.getRemoteAudioStream(mAgnetId);
                        //坐席画面裁剪铺满展示
                        AnyChatCoreSDK.getInstance(this).mVideoHelper.setMaxCutScale(mAgnetId, 0.7f);
                    } else {
                        mAnyChatSDK.getRemoteVideoStreamEx(this, mAgnetId, streamIndex, mOtherView, false);
                        mAnyChatSDK.getRemoteAudioStream(mAgnetId);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 关闭远程设备
     */
    private void closeRemoteDevice(int streamIndex, boolean closeRemoteAudio) {
        if (mAnyChatSDK != null) {
            if (streamIndex == 0) {
                mAnyChatSDK.cancelRemoteVideoStream(mAgnetId, mStreamIndex);
            } else {
                mAnyChatSDK.cancelRemoteVideoStreamEx(mAgnetId, mStreamIndex);
            }
            if (closeRemoteAudio) {
                mAnyChatSDK.cancelRemoteAudioStream(mAgnetId);
            }
        }
    }

    /**
     * 打开本地摄像和麦克风
     */
    private void openLocalDevice() {
        // 本地视频
        if (mCamera == null) {
            List<AnyChatCamera> cameras = mAnyChatSDK.getCameras(this.getApplicationContext());
            mCamera = cameras.get(0);
        }
        mCamera.prepare(mLocalView, true);
        mCamera.open();

        //本地音频
        if (mMicrophone == null) {
            List<AnyChatMicrophone> microphones = mAnyChatSDK.getMicrophones();
            mMicrophone = microphones.get(0);
        }
        if (mVoiceHasOpen) {
            mMicrophone.open();
        }
    }

    /**
     * 关闭本地摄像和麦克风
     */
    private void closeLocalDevice() {
        if (mCamera != null) {
            mCamera.close();
        }
        if (mMicrophone != null) {
            mMicrophone.close();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sdk_voice_view) {//本地语音开关
            if (mMicrophone != null) {
                if (mVoiceHasOpen) {
                    mMicrophone.close();
                    mVoiceView.setImageResource(R.drawable.sdk_ico_voice_close);
                } else {
                    mMicrophone.open();
                    mVoiceView.setImageResource(R.drawable.sdk_ico_voice_open);
                }
                mVoiceHasOpen = !mVoiceHasOpen;
            }
        } else if (viewId == R.id.sdk_switch_view) {//摄像头切换
            if (mCamera != null) {
                mCamera.switchCamera();
            }
        } else if (viewId == R.id.sdk_title_left_img_btn || viewId == R.id.sdk_end_recruit_view) {//返回按钮
            showAlertDialog();//返回|结束按钮
        } else if (viewId == R.id.sdk_send_view) {//聊天发送按钮
            if (mChatInputView == null) {
                return;
            }
            String msg = mChatInputView.getText().toString().trim();
            if (StringUtil.isNullOrEmpty(msg)) {
                UIAction.makeToast(this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            sendChatMsg(msg);
            mChatInputView.setText("");
        } else if (viewId == R.id.sdk_hide_chat_view) {//聊天折起设置
            if (mChatRootView != null) {
                mChatRootView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 发送文字消息
     */
    private void sendChatMsg(String msg) {
        if (mAnyChatSDK == null) {
            return;
        }
        List<Integer> targetUsers = new ArrayList<Integer>();
        targetUsers.add(mAgnetId);
        mAnyChatSDK.sendMsg(msg, targetUsers);
        addChatListInfo(msg, RecruitChatMessageModel.TYPE_SEND);
    }

    /**
     * 聊天消息数据填充
     */
    private void addChatListInfo(String content, int type) {
        RecruitChatMessageModel model = new RecruitChatMessageModel(content, type);
        mChatJsonLists.add(model);
        if (mChatRootView != null && mChatRootView.getVisibility() != View.VISIBLE) {
            mChatRootView.setVisibility(View.VISIBLE);
        }
        if (mChatListView != null && mChatListView.getVisibility() != View.VISIBLE) {
            mChatListView.setVisibility(View.VISIBLE);
        }
        if (null != mRecruitChatAdapter) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecruitChatAdapter.notifyDataSetChanged();
                    mChatListView.smoothScrollToPosition(mRecruitChatAdapter.getCount() - 1);// 移动到尾部
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //屏蔽返回按键
//            showAlertDialog();//onKeyDown
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出提示弹窗
     */
    private void showAlertDialog() {
        getComDialog().showDialog(this, "是否确定结束面试？", new RecruitCommonDialog.ConfirmListener() {
            @Override
            public void OnConfirmListener() {
                mEndVideoBySelf = true;
                hungupAndFinish();//退出弹窗
            }

            @Override
            public void OnCancelListener() {

            }
        });
    }

    /**
     * 接受到透明通道通知事件
     */
    @Override
    public void onReceiveBuffer(JSONObject json) {
        if (json == null) {
            return;
        }
        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "OnReceiveBuffer Data:" + json.toString());
        processReceiveBuffer(json);//解析收到的透明通道数据,并做业务处理
    }

    /**
     * 处理透明通道业务数据
     */
    private void processReceiveBuffer(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.has(RecruitRequestField.MSG)) {
            return;
        }
        //获取透明通道业务数据内容
        String msg = jsonObject.getString(RecruitRequestField.MSG);
        if (StringUtil.isNullOrEmpty(msg)) {
            return;
        }
        JSONObject jsContent = null;
        try {
            jsContent = new JSONObject(msg);
        } catch (Exception e) {
            e.printStackTrace();
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "ReceiveBuffer Json Exception:" + e.fillInStackTrace());
        }
        if (jsContent == null) {
            return;
        }
        if (!jsContent.has(RecruitRequestField.CMD)) {
            return;
        }
        //获取对应业务指令信息(如：ppt、mp4播放指令；屏幕共享指令；坐席请求手写签名指令等)
        String cmd = jsContent.getString(RecruitRequestField.CMD);
        //判断是否为播放ppt或者mp4或者屏幕共享相关指令
        if (StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_STREAM_PLAY_PPT)
                || StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_STREAM_PLAY_MP4)
                || StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_SHARE_MY_DESKTOP)) {
            if (!jsContent.has(RecruitRequestField.DATA)) {
                return;
            }
            JSONObject data = jsContent.getJSONObject(RecruitRequestField.DATA);
            if (data == null) {
                return;
            }
            if (data.has(RecruitRequestField.STREAM_INDEX)) {
                if (data.has(RecruitRequestField.STATUS)) {
                    int status = data.getInt(RecruitRequestField.STATUS);
                    closeRemoteDevice(mStreamIndex, false);//先关闭 然后处理其他操作
                    if (0 == status) {
                        //获取MP4或者PPT或者屏幕共享视频流流号
                        this.mStreamIndex = data.getInt(RecruitRequestField.STREAM_INDEX);
                        //设置界面切换为横屏
                        UIAction.setLandscape(this);
                    } else if (1 == status) {
                        //设置界面切换为竖屏
                        UIAction.setPortrait(this);
                        mStreamIndex = 0;
                    }
                    openRemoteDevice(mStreamIndex);//播放ppt mp4 屏幕共享坐席画面
                }
            }
        } else if (StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_CHAT_MODE_VIDEO)) {//坐席要求切换视频模式
            if (mRecruitModeSwitchView != null) {
                mRecruitModeSwitchView.setVisibility(View.VISIBLE);
                mRecruitModeSwitchView.setText(R.string.sdk_recruit_mode_switch_video);
            }
            if (mRecruitAudioModeView != null) {
                mRecruitAudioModeView.setVisibility(View.GONE);
            }
            //打开本地视频画面及坐席画面
            mLocalView.setVisibility(View.VISIBLE);
            mOtherView.setVisibility(View.VISIBLE);
            openLocalDevice();//坐席要求切换视频模式
            openRemoteDevice(mStreamIndex);//坐席要求切换视频模式
            //延迟执行隐藏语音切换提示view
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_AUDIO_MODE_TIP, 5000);
        } else if (StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_CHAT_MODE_AUDIO)) {//坐席要求切换音频模式
            if (mRecruitModeSwitchView != null) {
                mRecruitModeSwitchView.setVisibility(View.VISIBLE);
                mRecruitModeSwitchView.setText(R.string.sdk_recruit_mode_switch_audio);
            }
            if (mRecruitAudioModeView != null) {
                mRecruitAudioModeView.setVisibility(View.VISIBLE);
            }
            //关闭本地摄像头
            if (mCamera != null) {
                mCamera.close();
            }
            mLocalView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.GONE);
            //关闭坐席视频画面
            if (mStreamIndex == 0) {
                mAnyChatSDK.cancelRemoteVideoStream(mAgnetId, mStreamIndex);
            } else {
                mAnyChatSDK.cancelRemoteVideoStreamEx(mAgnetId, mStreamIndex);
            }
            //延迟执行隐藏语音切换提示view
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_AUDIO_MODE_TIP, 5000);
        }
    }

    /**
     * 坐席画面调整
     */
    private void adjustRemoteVideo(boolean bLandScape) {
        //远程视频画面
        ViewGroup.LayoutParams params = mOtherView.getLayoutParams();
        if (mScreenWidth == 0 || mScreenHeight == 0 || params == null) {
            return;
        }
//        params.width = bLandScape ? mScreenWidth * 4 / 3 : mScreenWidth;
//        params.height = bLandScape ? mScreenWidth : mScreenWidth * 3 / 4;
        params.width = bLandScape ? mScreenWidth * 4 / 3 : mScreenWidth;
        params.height = bLandScape ? mScreenWidth : mScreenHeight;
        mOtherView.setLayoutParams(params);

        //本地视频画面
        ViewGroup.LayoutParams localViewparams = mLocalView.getLayoutParams();
        if (mLocalViewWidth == 0 || mLocalViewHeight == 0 || localViewparams == null) {
            return;
        }
        localViewparams.width = bLandScape ? mLocalViewHeight : mLocalViewWidth;
        localViewparams.height = bLandScape ? mLocalViewWidth : mLocalViewHeight;
        mLocalView.setLayoutParams(localViewparams);
    }

    //横竖屏切换方法
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //(适应手机屏幕 因为坐席画面是4:3比例可能会展示不全)
            adjustRemoteVideo(true);//横屏时设置
            AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
        } else {
            adjustRemoteVideo(false);//竖屏时设置
            AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
        }
        mIsLandScape = !mIsLandScape;
    }

    /**
     * 发送透明通道(发送签名数据给web坐席)
     */
    private void sendTransBuffer(String msg) {
        if (StringUtil.isNullOrEmpty(msg) || mAnyChatSDK == null) {
            return;
        }
        List<Integer> targetUsers = new ArrayList<Integer>();
        targetUsers.add(mAgnetId); //接收方用户id (获取方式：在排队界面向坐席用户发送视频呼叫时可获取到对应服务坐席用户的id)
        mAnyChatSDK.transBufferEx(msg, targetUsers, 10, new AnyChatTransBufferReceivedEvent() {
            @Override
            public void transBufferStatus(boolean status) {
            }
        });
    }

    @Override
    public void onReceiveVideoCallRequest(JSONObject json) {

    }

    @Override
    public void onReceiveVideoCallStart(JSONObject json) {

    }

    @Override
    public void onReceiveVideoCallFinish(JSONObject json) {
        if (mEndVideoBySelf) {
            return;
        }
        if (json == null) {
            return;
        }
        RecruitLogUtils.e("onReceiveVideoCallFinish", json.toString());
        if (json.has(RecruitRequestField.ERROR_CODE)) {
            int errorCode = json.getInt(RecruitRequestField.ERROR_CODE);
            if (errorCode == 100106) {
                return;
            }
        }
        getCompleteDialog().showDialog(this, new RecruitCompleteDialog.RecruitCompleteListener() {
            @Override
            public void OnCompleteListener() {
                BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitCompleted(
                        new AnyChatResult(0, "面试已完成"));
                unnormalFinish();//视频通话结束退出
            }
        });
    }

    @Override
    public void onReceiveVideoCallError(JSONObject json) {
        if (json == null) {
            return;
        }
        RecruitLogUtils.e("onReceiveVideoCallError", json.toString());
        AnyChatResult result = null;
        if (json.has(RecruitRequestField.ERROR_CODE)) {
            int errorCode = json.getInt(RecruitRequestField.ERROR_CODE);
            if (errorCode == 100104) {
                result = new AnyChatResult(errorCode, "对方拒绝视频请求");
            } else if (errorCode == 100105) {
                result = new AnyChatResult(errorCode, "会话请求超时");
            } else if (errorCode == 100106) {
                result = new AnyChatResult(errorCode, "对方网络断线");
            } else {
                result = new AnyChatResult(errorCode, json.getString(RecruitRequestField.ERROR_MSG));
            }
        }
        BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(result);//onReceiveVideoCallError
        unnormalFinish();//视频异常退出
    }

    /**
     * 销毁并结束
     */
    private void unnormalFinish() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    @Override
    public void onRoomUserInAndOut(int userId, int action) {
        if (action == 1 && userId == mAgnetId) {
            openRemoteDevice(mStreamIndex);//房间监听打开远程设备
        }
    }

    @Override
    public void onRoomUserChanged(int userNum, String roomId) {

    }

    @Override
    public void onRoomUserMsgReceived(int userId, String msg) {
        if (userId == mAgnetId && !StringUtil.isNullOrEmpty(msg)) {
            JSONObject json = mAnyChatSDK.getUserInfo(userId);
            if (json != null && json.has(RecruitRequestField.USER_NAME)) {
                addChatListInfo(msg, RecruitChatMessageModel.TYPE_RECEIVED);
            }
        }
    }

    /**
     * 挂断并退出
     */
    private void hungupAndFinish() {
        if (mAnyChatSDK != null) {
            mAnyChatSDK.hungupVideoCall(mAgnetId);
        }
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAnyChatSDK != null) {
            openLocalDevice();//重新获取视频流
            if (mStreamIndex == 0) {
                mAnyChatSDK.getRemoteVideoStreamRestart(this, mAgnetId, mOtherView, false);
                mAnyChatSDK.getRemoteAudioStream(mAgnetId);
                //坐席画面裁剪铺满展示
                AnyChatCoreSDK.getInstance(this).mVideoHelper.setMaxCutScale(mAgnetId, 0.7f);
            } else {
                mAnyChatSDK.getRemoteVideoStreamRestartEx(this, mAgnetId, mStreamIndex, mOtherView, false);
                mAnyChatSDK.getRemoteAudioStream(mAgnetId);
            }
        }
    }

    @Override
    public void onLinkCloseStatus(int errorCode, String errorMsg) {
        RecruitApiHelper.isRecruitLogin = false;
        BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(new AnyChatResult(errorCode, errorMsg));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnyChatSDK != null) {
            //关闭本地摄像和麦克风
            closeLocalDevice();
            //关闭远程设备
            closeRemoteDevice(mStreamIndex, true);//onDestroy
            mAnyChatSDK.leaveRoom();//退出房间
            //退出营业厅
            mAnyChatSDK.leaveArea(new AnyChatLeaveAreaEvent() {
                @Override
                public void onLeaveAreaDone(AnyChatResult result, JSONObject JsonData) {

                }
            });
            mAnyChatSDK.unregisterRoomEvent(this);//注销房间监听事件
            mAnyChatSDK.unregisterVideoCallEvent(this);//注销视频呼叫监听事件
            mAnyChatSDK.unregisterTransBufferEvent(this);//注销接受透明通道通知事件
            mAnyChatSDK.unregisterLinkCloseEvent(this);//注销LinkCloseEvent
        }
        BRRecruitSDK.getInstance().release();
        //handler释放
        if (mHandler != null) {
            mHandler.removeMessages(MSG_HIDE_AUDIO_MODE_TIP);
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mComDialog != null) {
            mComDialog.destory();
            mComDialog = null;
        }
        if (mRecruitCompleteDialog != null) {
            mRecruitCompleteDialog.destory();
            mRecruitCompleteDialog = null;
        }
    }

    /**
     * 软引用Handler
     */
    private static class WeakHandler extends Handler {
        private final WeakReference<RecruitRecordVideoActivity> mActivity;

        public WeakHandler(RecruitRecordVideoActivity activity) {
            mActivity = new WeakReference<RecruitRecordVideoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().handMessage(msg);
        }
    }

    private void handMessage(Message msg) {
        switch (msg.what) {
            case MSG_HIDE_AUDIO_MODE_TIP://隐藏语音模式切换提示view
                if (mRecruitModeSwitchView != null) {
                    mRecruitModeSwitchView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 本地视频画面随手指滑动事件监听
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        private int lastX;

        private int lastY;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            //屏幕高度(不包含虚拟按键)
            int screenHeight = dm.heightPixels;
            //屏幕高度(减去了状态栏高度,因为本界面不是全屏展示的,如果界面设置了全屏模式则不需要减去状态栏高度)
//            int screenHeight = dm.heightPixels - UIAction.getStatusBarHeight(RecruitRecordVideoActivity.this);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int left = view.getLeft() + dx;
                    int bottom = view.getBottom() + dy;
                    int right = view.getRight() + dx;
                    int top = view.getTop() + dy;
                    //判断是否超出屏幕
                    if (left < 0) {
                        left = 0;
                        right = left + view.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + view.getHeight();
                    }
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - view.getWidth();
                    }
                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - view.getHeight();
                    }
                    view.layout(left, top, right, bottom);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    view.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}