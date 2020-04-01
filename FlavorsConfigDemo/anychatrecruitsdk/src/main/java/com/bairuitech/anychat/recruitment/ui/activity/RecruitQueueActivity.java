package com.bairuitech.anychat.recruitment.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.main.AnyChatResult;
import com.bairuitech.anychat.main.AnyChatSDK;
import com.bairuitech.anychat.queue.AnyChatCancelQueuingEvent;
import com.bairuitech.anychat.queue.AnyChatEnterAreaEvent;
import com.bairuitech.anychat.queue.AnyChatEnterQueueEvent;
import com.bairuitech.anychat.queue.AnyChatLeaveAreaEvent;
import com.bairuitech.anychat.queue.AnyChatQueueChangeEvent;
import com.bairuitech.anychat.queue.AnyChatQueueOpt;
import com.bairuitech.anychat.queue.AnyChatSyncAreasEvent;
import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.logic.BRRecruitSDK;
import com.bairuitech.anychat.recruitment.logic.RecruitBundleKeys;
import com.bairuitech.anychat.recruitment.logic.RecruitRequestField;
import com.bairuitech.anychat.recruitment.logic.config.RecruitApiHelper;
import com.bairuitech.anychat.recruitment.logic.model.RecruitGlobalConfig;
import com.bairuitech.anychat.recruitment.logic.model.RecruitQueryRouteModel;
import com.bairuitech.anychat.recruitment.logic.model.RecruitRouteInfoModel;
import com.bairuitech.anychat.recruitment.logic.model.RecruitVideoCallModel;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitBusinessModel;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitCompanyModel;
import com.bairuitech.anychat.recruitment.ui.view.RecruitCommonDialog;
import com.bairuitech.anychat.recruitment.ui.view.RecruitLoadingDialog;
import com.bairuitech.anychat.recruitment.ui.view.RecruitPrepareDialog;
import com.bairuitech.anychat.recruitment.utils.RecruitLogUtils;
import com.bairuitech.anychat.recruitment.utils.RecruitPlaySoundUtil;
import com.bairuitech.anychat.recruitment.utils.StringUtil;
import com.bairuitech.anychat.recruitment.utils.UIAction;
import com.bairuitech.anychat.transfer.AnyChatReceiveBufferEvent;
import com.bairuitech.anychat.transfer.BusinessTransferCallBack;
import com.bairuitech.anychat.util.json.JSONObject;
import com.bairuitech.anychat.video.AnyChatVideoCallEvent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @describe: 排队等待界面
 * @author: yyh
 * @createTime: 2018/8/11 14:06
 * @className: RecruitQueueActivity
 */
public class RecruitQueueActivity extends RecruitAnyChatBaseActivity implements View.OnClickListener,
        AnyChatQueueChangeEvent, AnyChatVideoCallEvent, AnyChatReceiveBufferEvent {

    private static final int MSG_TIME_UPDATE = 1;//handler msg 排队时间展示
    private static final int RECURIT_PENDE_REQUEST_CODE = 2; // 返回的结果码

    private TextView mWaitTimeView;//排队时间
    private TextView mWaitTipView;//展示文字提示
    private View mRecruitVideoRootView;
    private VideoView mRecruitVideoView;
    private ProgressBar mRecruitVideoLoadingView;
    private View mRecruitVideoPlayTryView;
    private boolean mIsVideoPlayStop;
    private int mCurrentVideoPlayPosition;

    private Timer mTimer;
    private TimerTask mTimerTask;

    private final Handler mWeakHandler = new WeakHandler(this);

    private RecruitLoadingDialog mRecruitLoadingDialog;
    private RecruitCommonDialog mRecruitCommonDialog;
    private RecruitPrepareDialog mRecruitPrepareDialog;

    private String mReservationNo;//预约编码
    private String mVideoCallParameter;//呼叫参数
    private RecruitBusinessModel mRecruitBusinessModel;//业务信息传参模型实体类

    private AnyChatSDK mAnyChatSDK;
    private String mAreaId;//营业厅id
    private String mQueueId;//队列id
    private int mAgentId;//坐席id

    /**
     * 弹窗
     */
    private RecruitCommonDialog getDialog() {
        if (mRecruitCommonDialog == null) {
            mRecruitCommonDialog = RecruitCommonDialog.getInstance();
        }
        //展示挂起|退出|正在呼叫弹窗提示时,如果面试准备提示弹窗正在展示则将其隐藏
        if (mRecruitPrepareDialog != null) {
            mRecruitPrepareDialog.destory();
            mRecruitPrepareDialog = null;
        }
        return mRecruitCommonDialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_recruit_activity_queue);
        initData();
        initView();
        initSDK();
    }

    private void initData() {
        RecruitGlobalConfig config = RecruitGlobalConfig.getInstance();
        this.mReservationNo = config.getReservationNo();
        this.mRecruitBusinessModel = config.getRecruitBusinessModel();
    }

    private void initView() {
//        UIAction.setBackgroundResourceSafety(findViewById(R.id.sdk_recruit_title_bar), R.color.sdk_trans_color);
        UIAction.setTitle(this, R.string.sdk_recruit_queue_wait_title);
        UIAction.setTitleBarLeftImgBtn(getWindow().getDecorView(), R.drawable.sdk_ico_back_white, this);

        this.findViewById(R.id.sdk_recruit_end_queue_view).setOnClickListener(this);
        this.findViewById(R.id.sdk_recruit_temp_pende_view).setOnClickListener(this);
        this.mWaitTimeView = (TextView) findViewById(R.id.sdk_recruit_queue_time);

        this.mWaitTipView = (TextView) findViewById(R.id.sdk_recruit_wait_tip_view);
        this.mRecruitVideoRootView = findViewById(R.id.sdk_recruit_root_video_view);
        this.mRecruitVideoView = (VideoView) findViewById(R.id.sdk_recruit_video_view);
        this.mRecruitVideoLoadingView = (ProgressBar) findViewById(R.id.sdk_recruit_video_loading_view);
        this.mRecruitVideoPlayTryView = (View) findViewById(R.id.sdk_recruit_video_play_try_view);
        this.mRecruitVideoPlayTryView.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mIsVideoPlayStop) {
            mRecruitVideoView.seekTo(mCurrentVideoPlayPosition);
            mRecruitVideoView.start();
            mRecruitVideoLoadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecruitVideoView.isPlaying()) {
            mRecruitVideoView.pause();
            mCurrentVideoPlayPosition = mRecruitVideoView.getCurrentPosition();
        }
    }

    /**
     * 开始企业播报播放
     */
    private void startPlay() {
        if (mRecruitBusinessModel == null) {
            return;
        }
        RecruitCompanyModel recruitCompanyModel = mRecruitBusinessModel.getRecruitCompanyModel();
        if (recruitCompanyModel == null) {
            return;
        }
        String companyVideoPath = recruitCompanyModel.getFullUrl();
        if (StringUtil.isNullOrEmpty(companyVideoPath)) {
            return;
        }
        File file = new File(companyVideoPath);
        if (!file.exists()) {
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "CompanyVideoFile is not exists");
            return;
        }
        mRecruitVideoPlayTryView.setVisibility(View.GONE);
        mRecruitVideoRootView.setVisibility(View.VISIBLE);
        MediaController mediaController = new MediaController(this);//Video是我类名，是你当前的类
        mRecruitVideoView.setMediaController(mediaController);//设置VedioView与MediaController相关联
        mediaController.setBackgroundColor(Color.LTGRAY);
        mRecruitVideoLoadingView.setVisibility(View.VISIBLE);

//        Uri uri = Uri.parse(companyVideoUrl);
//        if (uri == null) {
//            mIsVideoPlayStop = true;//uri = null
//            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "Start Play Video Uri is null");
//            return;
//        }
//        mRecruitVideoView.setVideoURI(uri);
        mRecruitVideoView.setVideoPath(companyVideoPath);
        mRecruitVideoView.start();
        mRecruitVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mRecruitVideoLoadingView.setVisibility(View.GONE);
                RecruitLogUtils.e("Start Play Video", " onPrepared");
            }
        });
        mRecruitVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                RecruitLogUtils.e("Start Play Video", " onCompletion");
//                if (mp != null) {
//                    //设置循环播放
//                    mp.start();
//                    mp.setLooping(true);
//                }
                mRecruitVideoPlayTryView.setVisibility(View.VISIBLE);
            }
        });

        mRecruitVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                RecruitLogUtils.e("Start Play Video ", "onError");
                mRecruitVideoView.stopPlayback();
                mRecruitVideoLoadingView.setVisibility(View.GONE);
                mRecruitVideoPlayTryView.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    /**
     * 停止企业播报播放并释放资源
     */
    private void stopPlay() {
        mRecruitVideoView.stopPlayback();
        mIsVideoPlayStop = true;//stopPlay
    }

    private void initSDK() {
        if (StringUtil.isNullOrEmpty(mReservationNo)) {
            BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(
                    new AnyChatResult(100906, "进线路由参数错误"));
            cancelAndFinish(false, false);//进线路由参数错误
            return;
        }

        if (mRecruitLoadingDialog == null) {
            mRecruitLoadingDialog = RecruitLoadingDialog.getInstance();
        }
        mRecruitLoadingDialog.showDialog(this, "", false);

        if (mAnyChatSDK == null) {
            mAnyChatSDK = AnyChatSDK.getInstance();
        }
        //注册LinkCloseEvent事件
        mAnyChatSDK.registerLinkCloseEvent(this);
        //注册接受透明通道通知事件
        mAnyChatSDK.registerTransBufferEvent(this);

        //获取进线路由(营业厅id和队列id)
        RecruitQueryRouteModel model = new RecruitQueryRouteModel();
        model.setCommand(RecruitRequestField.QUERY_ROUTE_INFO);//业务接口标识
        model.setRequestId(String.valueOf(new Random().nextInt(90000000)));//请求id 8位请求随机数
        model.setUserId(mAnyChatSDK.myUserid);//Anychat登录返回id
        model.setAppId(mAnyChatSDK.mLoginAppid);//Anychat登录应用id
        model.setTimestamp(System.currentTimeMillis());//时间戳
        model.setVersion(RecruitApiHelper.VERSION_CODE);//版本号
        //路由参数
        RecruitQueryRouteModel.ParamsEntity entity = new RecruitQueryRouteModel.ParamsEntity();
        LinkedHashMap<String, String> routeKeyMap = new LinkedHashMap<String, String>();
        routeKeyMap.put(RecruitRequestField.APPTYPE_CODE, RecruitRequestField.APPTYPE_RECRUIT);//appTypeCode
        routeKeyMap.put(RecruitRequestField.RESERVATION_NO, mReservationNo);//预约编码

        JSONObject jsonObject = new JSONObject(routeKeyMap);
        entity.setRouteKey(jsonObject.toString());
        model.setParams(entity);

        RecruitLogUtils.e("===QueryRouteInfo ", model.toJson());
        //发起异步传输业务数据
        mAnyChatSDK.asyncTransfer(model.toJson(), new BusinessTransferCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                String errMsg = null;
                if (jsonObject != null) {
                    RecruitLogUtils.e("===asyncTransfer ", jsonObject.toString());
                    RecruitRouteInfoModel recruitRouteInfoModel = RecruitRouteInfoModel.fromJson(jsonObject.toString());
                    if (recruitRouteInfoModel != null) {
                        int errorCode = recruitRouteInfoModel.getErrorCode();
                        if (errorCode == 0) {
                            RecruitRouteInfoModel.DataEntity data = recruitRouteInfoModel.getData();
                            if (data != null) {
                                mAreaId = data.getBusinessHallId();
                                mQueueId = String.valueOf(data.getQueueId());
                                RecruitLogUtils.e("RecruitGetQueryRouteInfo ", " AreaId:" + mAreaId + "  QueueId:" + mQueueId);
                                AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                                        "RecruitQueryRouteInfo:" + " AreaId:" + mAreaId + "  QueueId:" + mQueueId);
                                if (!StringUtil.isNullOrEmpty(mAreaId) && !StringUtil.isNullOrEmpty(mQueueId)
                                        && !StringUtil.equals(mQueueId, "0")) {
                                    List<RecruitVideoCallModel.Content> content = data.getContent();
                                    if (content != null && !content.isEmpty()) {
                                        try {
                                            RecruitVideoCallModel recruitCallModel = new RecruitVideoCallModel();
                                            recruitCallModel.setContent(content);
                                            //添加扩展参数信息(地理位置、自身ip信息)
                                            addExpansionData(recruitCallModel);
                                            mVideoCallParameter = recruitCallModel.toJson();
                                            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                                                    "RecruitVideoCallParameter:" + mVideoCallParameter);
                                            getAreas();//获取营业厅列表
                                            return;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                                                    "RecruitGetVideoCallParameterException:" + e.toString());
                                            errMsg = "呼叫参数转换异常";
                                        }
                                    } else {
                                        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                                                "RecruitVideoCallParameter is null");
                                        errMsg = "呼叫参数返回有误";
                                    }
                                } else {
                                    errMsg = "进线参数返回有误";
                                }
                            } else {
                                errMsg = "参数返回有误";
                            }
                        }
                        if (StringUtil.isNullOrEmpty(errMsg)) {
                            errMsg = recruitRouteInfoModel.getMsg();
                        }
                    }
                }
                BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(new AnyChatResult(
                        100902, StringUtil.isNullOrEmpty(errMsg) ? "服务队列获取失败" : errMsg));
                cancelAndFinish(false, false);//获取服务队列异常
            }

            @Override
            public void onFailure(AnyChatResult result) {
                BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(result);
                cancelAndFinish(false, false);//获取服务队列异常
            }
        });
    }

    /**
     * 添加扩展参数信息(地理位置、自身ip信息)
     */
    private void addExpansionData(RecruitVideoCallModel recruitCallModel) {
        try {
            JSONObject expansion = null;
            String address = mRecruitBusinessModel.getAddress();
            if (!StringUtil.isNullOrEmpty(address)) {
                expansion = new JSONObject();
                //添加位置信息
                expansion.put(RecruitRequestField.EX_ADDRESS, address);
            }
            //获取自身ip地址信息(需在sdk登录完成后调用获取)
            String ipAddress = AnyChatSDK.getInstance().getUserIpAddress(-1);
            if (!StringUtil.isNullOrEmpty(ipAddress)) {
                if (expansion == null) {
                    expansion = new JSONObject();
                }
                //自身ip地址信息
                expansion.put(RecruitRequestField.EX_IP_INFO, ipAddress);
            }
            if (expansion != null && !StringUtil.isNullOrEmpty(expansion.toString())) {
                recruitCallModel.setExpansion(expansion.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                    "RecruitVideoCallParameter AddExpansionData Exception");
        }
    }

    /**
     * 获取营业厅列表
     */
    private void getAreas() {
        //注册排队变化事件
        mAnyChatSDK.registerQueueChangeEvent(this);
//        int priority = !StringUtil.isNullOrEmpty(mReservationNo) ? 6 : 5;
        int priority = 5;
        RecruitLogUtils.e("SetQueuePriority:", priority + "");
        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "SetQueuePriority:" + priority);
        //设置队列属性(角色及排队优先级)
        AnyChatQueueOpt queueOpt = new AnyChatQueueOpt(0, priority);
        //自动路由设置
        queueOpt.setIsAutoMode(true);
        //初始化队列模块
        mAnyChatSDK.initQueueOpt(queueOpt);
        //获取营业数据
        mAnyChatSDK.getAreas(new AnyChatSyncAreasEvent() {
            @Override
            public void onSyncAreasDone(AnyChatResult result, JSONObject json) {
                if (result != null && result.errCode == 0 && json != null) {
                    enterAreas();//进入营业厅
                } else {
                    BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(
                            new AnyChatResult(100903, "营业厅信息获取失败"));
                    cancelAndFinish(false, false);//获取营业厅信息
                }
            }
        });
    }

    /**
     * 进入营业厅
     */
    private void enterAreas() {
        mAnyChatSDK.enterArea(mAreaId, new AnyChatEnterAreaEvent() {
            @Override
            public void onEnterAreaDone(AnyChatResult result, JSONObject json) {
                if (result != null && result.errCode == 0 && json != null) {
                    enterQueue();//进入队列
                } else {
                    BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(
                            new AnyChatResult(100904, "进入营业厅失败"));
                    cancelAndFinish(false, false);//进入营业厅失败
                }
            }
        });
    }

    /**
     * 进入队列
     */
    private void enterQueue() {
        mAnyChatSDK.enterQueue(mQueueId, new AnyChatEnterQueueEvent() {
            @Override
            public void onProcessChanged(JSONObject json) {
                if (json != null) {
                    updataQueueInfo(json);
                }
            }

            @Override
            public void onEnqueueDone(AnyChatResult result, JSONObject json) {
                if (result != null && 0 == result.errCode) {
                    //注册视频呼叫事件
                    mAnyChatSDK.registerVideoCallEvent(RecruitQueueActivity.this);
                    if (json != null) {
                        initTimer();//初始化排队时间展示计时器
                        updataQueueInfo(json);
                        if (mRecruitLoadingDialog != null) {
                            mRecruitLoadingDialog.destory();
                            mRecruitLoadingDialog = null;
                        }
                        startPlay();//开始企业播报播放
                    }
                } else {
                    BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(
                            new AnyChatResult(100905, "进入队列失败"));
                    cancelAndFinish(true, false);//进入队列失败
                }
            }
        });
    }

    /**
     * 初始化排队时间展示计时器
     */
    private void initTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mWeakHandler.sendEmptyMessage(MSG_TIME_UPDATE);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 更新排队信息
     */
    private void updataQueueInfo(JSONObject json) {
        if (json == null) {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sdk_title_left_img_btn || viewId == R.id.sdk_recruit_end_queue_view) {//结束排队
            showAlertDialog();//onClick
        } else if (viewId == R.id.sdk_recruit_video_play_try_view) {
            startPlay();//播放完成后重新播放
        } else if (viewId == R.id.sdk_recruit_temp_pende_view) {//暂时挂起
            getDialog().showDialog(this, getString(R.string.sdk_recruit_pending_dialog_text_tip),
                    new RecruitCommonDialog.ConfirmListener() {
                        @Override
                        public void OnConfirmListener() {
                            //取消排队并退出营业厅
                            cacelAndRecruitPende();
                            stopPlay();//暂时挂起
                            Intent intent = new Intent(RecruitQueueActivity.this, RecruitPendingActivity.class);
                            startActivityForResult(intent, RECURIT_PENDE_REQUEST_CODE);
                        }

                        @Override
                        public void OnCancelListener() {

                        }
                    });
        }
    }

    private void cacelAndRecruitPende() {
        if (mAnyChatSDK != null) {
            //取消排队
            mAnyChatSDK.cancelQueuing(new AnyChatCancelQueuingEvent() {
                @Override
                public void onCancelQueuingDone(AnyChatResult result, String JsonData) {

                }
            });
            //退出营业厅
            mAnyChatSDK.leaveArea(new AnyChatLeaveAreaEvent() {
                @Override
                public void onLeaveAreaDone(AnyChatResult result, JSONObject JsonData) {

                }
            });
            //注销排队变化事件
            mAnyChatSDK.unregisterQueueChangeEvent(this);
            //注销视频呼叫监听
            mAnyChatSDK.unregisterVideoCallEvent(this);
            //注销LinkCloseEvent事件
            mAnyChatSDK.unregisterLinkCloseEvent(this);
            //注销接受透明通道通知事件
            mAnyChatSDK.unregisterTransBufferEvent(this);
        }
    }

    /**
     * 接受到透明通道通知事件
     */
    @Override
    public void onReceiveBuffer(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                "RecruitQueueReceiveBuffer Data:" + jsonObject.toString());
        if (!jsonObject.has(RecruitRequestField.MSG)) {
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
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                    "RecruitQueue ReceiveBuffer Json Exception:" + e.fillInStackTrace());
        }
        if (jsContent == null) {
            return;
        }
        if (!jsContent.has(RecruitRequestField.CMD)) {
            return;
        }
        //获取对应业务指令信息(如：面试接入准备)
        String cmd = jsContent.getString(RecruitRequestField.CMD);
        //判断是否为面试接入准备指令
        if (StringUtil.equalsNotNull(cmd, RecruitRequestField.CMD_RECRUIT_VIDEO_PREPARE)) {
            showRecruitPrepareDialog();//面试接入准备提示弹窗展示
        }
    }

    /**
     * 面试接入准备提示弹窗展示
     */
    private void showRecruitPrepareDialog() {
        if (mRecruitPrepareDialog == null) {
            mRecruitPrepareDialog = RecruitPrepareDialog.getInstance();
        }
        mRecruitPrepareDialog.showRecruitPrepareDialog(this,
                new RecruitPrepareDialog.RecruitPrepareListener() {
                    @Override
                    public void OnRecruitPrepareListener() {
                        if (mRecruitPrepareDialog != null) {
                            mRecruitPrepareDialog.destory();
                            mRecruitPrepareDialog = null;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECURIT_PENDE_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean hasLinkClose = false;
            if (data != null) {
                if (data.hasExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE)) {
                    hasLinkClose = data.getBooleanExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE, false);
                }
                if (hasLinkClose) {
                    int errorCode = 100;
                    String errorMsg = null;
                    if (data.hasExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_CODE)) {
                        errorCode = data.getIntExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_CODE, 100);
                    }
                    if (data.hasExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_MSG)) {
                        errorMsg = data.getStringExtra(RecruitBundleKeys.EXTRA_LINK_CLOSE_MSG);
                    }
                    RecruitApiHelper.isRecruitLogin = false;
                    BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(new AnyChatResult(errorCode, errorMsg));
                    BRRecruitSDK.getInstance().release();
                    finish();
                    return;
                }
            }
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
                    "RecruitQueueActivityResult: enter queue again");

            //执行重新进入队列操作
            mAnyChatSDK.registerLinkCloseEvent(this);
            //注册接受透明通道通知事件
            mAnyChatSDK.registerTransBufferEvent(this);
            getAreas();//执行重新进入队列操作
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showAlertDialog();//onKeyDown
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出提示弹窗
     */
    private void showAlertDialog() {
        getDialog().showDialog(this, "是否确定结束排队？",
                new RecruitCommonDialog.ConfirmListener() {
                    @Override
                    public void OnConfirmListener() {
                        //取消排队并退出营业厅
                        cancelAndFinish(true, false);//取消排队并退出营业厅
                    }

                    @Override
                    public void OnCancelListener() {

                    }
                });
    }

    @Override
    public void onLinkCloseStatus(int errorCode, String errorMsg) {
        RecruitApiHelper.isRecruitLogin = false;
        BRRecruitSDK.getInstance().getRecruitRecordEvent().onRecruitError(new AnyChatResult(errorCode, errorMsg));
        //取消排队并退出营业厅
        cancelAndFinish(true, false);//onLinkCloseStatus
    }

    /**
     * 退出队列 视频挂断 退出营业厅等事件处理
     *
     * @param cancelQueue     是否退出队列(取消排队)
     * @param hungupVideoCall 是否执行视频挂断操作
     */
    private void cancelAndFinish(boolean cancelQueue, boolean hungupVideoCall) {
        if (mRecruitLoadingDialog != null) {
            mRecruitLoadingDialog.destory();
            mRecruitLoadingDialog = null;
        }
        if (mAnyChatSDK != null) {
            if (cancelQueue) {
                //取消排队
                mAnyChatSDK.cancelQueuing(new AnyChatCancelQueuingEvent() {
                    @Override
                    public void onCancelQueuingDone(AnyChatResult result, String JsonData) {

                    }
                });
            }
            if (hungupVideoCall) {
                //视频挂断操作
                mAnyChatSDK.hungupVideoCall(mAgentId);
            }
            //退出营业厅
            mAnyChatSDK.leaveArea(new AnyChatLeaveAreaEvent() {
                @Override
                public void onLeaveAreaDone(AnyChatResult result, JSONObject JsonData) {

                }
            });
            //注销排队变化事件
            mAnyChatSDK.unregisterQueueChangeEvent(this);
            //注销视频呼叫监听
            mAnyChatSDK.unregisterVideoCallEvent(this);
            //注销LinkCloseEvent事件
            mAnyChatSDK.unregisterLinkCloseEvent(this);
            //注销接受透明通道通知事件
            mAnyChatSDK.unregisterTransBufferEvent(this);
//            mAnyChatSDK.release();
        }
        BRRecruitSDK.getInstance().release();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();//onDestroy
        //释放资源音乐
        RecruitPlaySoundUtil.getInstance().releaseMusic();
        if (mAnyChatSDK != null) {
            //注销排队变化事件
            mAnyChatSDK.unregisterQueueChangeEvent(this);
            //注销视频呼叫监听
            mAnyChatSDK.unregisterVideoCallEvent(this);
            //注销LinkCloseEvent事件
            mAnyChatSDK.unregisterLinkCloseEvent(this);
            //注销接受透明通道通知事件
            mAnyChatSDK.unregisterTransBufferEvent(this);
        }
        reset();
    }

    /**
     * 计时器重置及弹窗处理
     */
    private void reset() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mWeakHandler != null) {
            mWeakHandler.removeMessages(MSG_TIME_UPDATE);
            mWeakHandler.removeCallbacksAndMessages(null);
        }
        if (mRecruitCommonDialog != null) {
            mRecruitCommonDialog.destory();
            mRecruitCommonDialog = null;
        }
        if (mRecruitLoadingDialog != null) {
            mRecruitLoadingDialog.destory();
            mRecruitLoadingDialog = null;
        }
        if (mRecruitPrepareDialog != null) {
            mRecruitPrepareDialog.destory();
            mRecruitPrepareDialog = null;
        }
    }

    private void handMessage(Message msg) {
        if (msg == null) {
            return;
        }
        switch (msg.what) {
            case MSG_TIME_UPDATE:
                //排队时间
                if (mWaitTimeView != null && mAnyChatSDK != null) {
                    mWaitTimeView.setText(StringUtil.formatTime(mAnyChatSDK.getQueueTime(mQueueId)));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAreaChanged(JSONObject jsonObject) {

    }

    @Override
    public void onAgentStatusChanged(JSONObject jsonObject) {

    }

    @Override
    public void onAgentServiceInfoNotify(JSONObject jsonObject) {

    }

    @Override
    public void onServiceNotify(JSONObject json) {
        if (json != null && json.has(RecruitRequestField.AGENTID)) {
            //获取正在服务的坐席id
            mAgentId = json.getInt(RecruitRequestField.AGENTID);
            //获取服务坐席的名称
            String agentName = mAnyChatSDK.getUserName(mAgentId);
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "OnServiceNotify Data:" + json.toString() + "  AgentName:" + agentName);
            //停止企业播报播放并释放资源
            stopPlay();//onServiceNotify
            //开始播放音乐
            RecruitPlaySoundUtil.getInstance().playMusic(this);

            //呼叫目标用户(对坐席发起视频呼叫并传入呼叫参数)
            mAnyChatSDK.requestVideoCall(mAgentId, mVideoCallParameter);

            //正在呼叫提示弹窗
            getDialog().showSingleDialog(this, getString(R.string.sdk_recruit_calling), getString(R.string.sdk_recruit_end_call),
                    new RecruitCommonDialog.ConfirmListener() {
                        @Override
                        public void OnConfirmListener() {
                            //停止播放音乐
                            RecruitPlaySoundUtil.getInstance().stopMusic();
                            //取消视频呼叫
                            mAnyChatSDK.cancelVideoCall(mAgentId);//取消视频呼叫
                            cancelAndFinish(true, false);//取消视频呼叫
                        }

                        @Override
                        public void OnCancelListener() {

                        }
                    });

            //已经发起呼叫时注销排队变化事件
            mAnyChatSDK.unregisterQueueChangeEvent(this);
        } else {
            cancelAndFinish(true, false);//开始服务通知onServiceNotify
        }
    }

    @Override
    public void onReceiveVideoCallRequest(JSONObject jsonObject) {

    }

    @Override
    public void onReceiveVideoCallStart(JSONObject json) {
        if (mRecruitCommonDialog != null) {
            mRecruitCommonDialog.destory();
            mRecruitCommonDialog = null;
        }
        //停止播放音乐
        RecruitPlaySoundUtil.getInstance().stopMusic();
        stopPlay();//onReceiveVideoCallStart
        //获取对方用户id及对方接受视频呼叫后系统分配的房间id
        if (json != null && json.has(RecruitRequestField.USER_ID) && json.has(RecruitRequestField.ROOM_ID)) {
            AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "OnReceiveVideoCallStart Data:" + json.toString());

            //视频对话对方id
            int agentId = json.getInt(RecruitRequestField.USER_ID);
            //房间id
            String roomId = json.getString(RecruitRequestField.ROOM_ID);
            //跳转视频界面
            Intent intent = new Intent(this, RecruitRecordVideoActivity.class);
            intent.putExtra(RecruitBundleKeys.EXTRA_REMOTE_USER_ID, agentId);//坐席id
            intent.putExtra(RecruitBundleKeys.EXTRA_ROOM_ID, roomId);//房间id
            startActivity(intent);
            reset();
            finish();
        } else {
            cancelAndFinish(false, true);//呼叫开始通知onReceiveVideoCallStart
        }
    }

    @Override
    public void onReceiveVideoCallFinish(JSONObject jsonObject) {

    }

    @Override
    public void onReceiveVideoCallError(JSONObject json) {
        //停止播放音乐
        RecruitPlaySoundUtil.getInstance().stopMusic();
        stopPlay();//onReceiveVideoCallError
        if (json == null) {
            cancelAndFinish(true, false);//onReceiveVideoCallError
            return;
        }
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
        cancelAndFinish(true, false);//视频呼叫异常
    }

    private static class WeakHandler extends Handler {
        private final WeakReference<RecruitQueueActivity> mActivity;

        public WeakHandler(RecruitQueueActivity activity) {
            mActivity = new WeakReference<RecruitQueueActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().handMessage(msg);
        }
    }
}