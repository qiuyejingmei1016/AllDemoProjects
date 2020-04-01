package com.bairuitech.anychat.recruitment.logic;

import android.content.Context;
import android.content.Intent;

import com.bairuitech.anychat.login.AnyChatLoginEvent;
import com.bairuitech.anychat.main.AnyChatInitOpt;
import com.bairuitech.anychat.main.AnyChatResult;
import com.bairuitech.anychat.main.AnyChatSDK;
import com.bairuitech.anychat.recruitment.logic.config.RecruitApiHelper;
import com.bairuitech.anychat.recruitment.logic.interf.RecruitRecordEvent;
import com.bairuitech.anychat.recruitment.logic.model.RecruitGlobalConfig;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitBusinessModel;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitTransferModel;
import com.bairuitech.anychat.recruitment.ui.activity.RecruitQueueActivity;
import com.bairuitech.anychat.recruitment.ui.view.RecruitLoadingDialog;
import com.bairuitech.anychat.recruitment.utils.RecruitFileUtils;
import com.bairuitech.anychat.recruitment.utils.RecruitLogUtils;
import com.bairuitech.anychat.recruitment.utils.StringUtil;

import java.util.Random;

/**
 * @describe: 视频组件唤起调用管理类
 * @author: yyh
 * @createTime: 2019/5/15 15:22
 * @className: BRRecruitSDK
 */
public class BRRecruitSDK {

    private String AAR_VERSION = "aarBuildTime_20200303";

    private static volatile BRRecruitSDK mInstance;

    private Context mContext;//上下文对象
    private RecruitLoadingDialog mRecruitLoadingDialog;//加载提示弹窗
    private RecruitGlobalConfig mRecruitGlobalModel;//单例全局保存传参数据信息

    private RecruitRecordEvent mRecruitRecordEvent;//组件回调接口

    public RecruitRecordEvent getRecruitRecordEvent() {
        return mRecruitRecordEvent;
    }

    private BRRecruitSDK() {
    }

    public static BRRecruitSDK getInstance() {
        if (mInstance == null) {
            synchronized (BRRecruitSDK.class) {
                if (mInstance == null) {
                    mInstance = new BRRecruitSDK();
                }
            }
        }
        return mInstance;
    }

    /**
     * 视频组件唤起调用接口
     *
     * @param context 上下文
     * @param model   传递参数对象
     * @param event   回调事件
     */
    public void startRecruit(Context context, RecruitTransferModel model, RecruitRecordEvent event) {
        if (event == null) {
            RecruitLogUtils.e("Start", "RecruitRecordEvent is null");
            return;
        }
        this.mRecruitRecordEvent = event;
        if (null == context || null == model) {
            handleErrorMsg("请检查传参是否正确");
            return;
        }
        this.mContext = context;
        //开始登录
        startLogin(context, model);
    }

    /**
     * 开始登录
     *
     * @param context              上下文
     * @param recruitTransferModel 外部调用传参模型实体类
     */
    private void startLogin(Context context, RecruitTransferModel recruitTransferModel) {
        RecruitFileUtils.deleteLogFile(RecruitLogUtils.LOGNAME);//清空日志文件
        String strUserId = recruitTransferModel.getStrUserId();
        if (StringUtil.isNullOrEmpty(strUserId)) {
            handleErrorMsg("登录唯一标识不能为空");
            return;
        }
        String loginIp = recruitTransferModel.getLoginIp();
        if (StringUtil.isNullOrEmpty(loginIp)) {
            handleErrorMsg("登录ip不能为空");
            return;
        }
        String loginPort = recruitTransferModel.getLoginPort();
        if (StringUtil.isNullOrEmpty(loginPort)) {
            handleErrorMsg("登录端口不能为空");
            return;
        }
        String loginAppId = recruitTransferModel.getLoginAppId();
        if (StringUtil.isNullOrEmpty(loginAppId)) {
            handleErrorMsg("应用Id不能为空");
            return;
        }
        String reservationNo = recruitTransferModel.getReservationNo();

        RecruitLogUtils.e("StartLogin RecruitTransferModel: ", AAR_VERSION + "  " + recruitTransferModel.toJson());
        if (mRecruitGlobalModel != null) {
            mRecruitGlobalModel.release();
        }
        if (mRecruitLoadingDialog == null) {
            mRecruitLoadingDialog = RecruitLoadingDialog.getInstance();
        }
        mRecruitLoadingDialog.showDialog(context, "", false);
        //保存全局配置信息
        this.mRecruitGlobalModel = RecruitGlobalConfig.getInstance();

        //业务信息相关
        RecruitBusinessModel recruitBusinessModel = recruitTransferModel.getRecruitBusinessModel();
        if (recruitBusinessModel != null) {
            mRecruitGlobalModel.setRecruitBusinessModel(recruitBusinessModel);
        }
        //或者外部传递的nickName
        String nickName = recruitTransferModel.getNickName();
        //校验相关参数信息
        if (checkParameter(reservationNo, nickName, strUserId, loginAppId)) {
            //登录AnyChat
            loginAnyChat(nickName, strUserId, loginIp, loginPort, loginAppId);
        }
    }

    /**
     * 校验相关参数信息
     *
     * @param reservationNo 面试预约码
     * @param nickName      登录用户昵称
     * @param strUserId     业务系统用户身份唯一标识
     * @param loginAppId    商户登录应用ID
     */
    private boolean checkParameter(String reservationNo, String nickName, String strUserId, String loginAppId) {
        if (!StringUtil.isNullOrEmpty(nickName)) {
            if (nickName.length() > 20) {
                handleErrorMsg("登录用户昵称超出长度限制");
                return false;
            }
        }
        if (strUserId.length() > 50) {
            handleErrorMsg("登录唯一标识超出长度限制");
            return false;
        }
        if (loginAppId.length() > 50) {
            handleErrorMsg("应用Id超出长度限制");
            return false;
        }

        if (!StringUtil.isNullOrEmpty(reservationNo)) {
            //保存预约编码
            this.mRecruitGlobalModel.setReservationNo(reservationNo);
        } else {
            handleErrorMsg("预约编码不存在");
            return false;
        }
        return true;
    }

    /**
     * 登录AnyChat
     *
     * @param loginNickName  登录账号昵称
     * @param loginStrUserId 登录strUserId
     * @param loginIp        登录ip
     * @param loginPort      登录端口
     * @param loginAppId     登录应用id
     */
    private void loginAnyChat(String loginNickName, String loginStrUserId, String loginIp, String loginPort, String loginAppId) {
        if (RecruitApiHelper.isRecruitLogin) {
            release();
        }
        RecruitLogUtils.e("LoginAnyChat ", loginNickName + "  " + loginStrUserId + " " + loginIp + "  " + loginPort + "  " + loginAppId);
        AnyChatInitOpt initOpt = new AnyChatInitOpt(StringUtil.isNullOrEmpty(loginNickName) ? loginStrUserId : loginNickName,
                loginStrUserId + new Random().nextInt(100), "", loginIp,
                Integer.valueOf(loginPort), new AnyChatLoginEvent() {
            @Override
            public void onLogin(int userId) {
                if (mRecruitLoadingDialog != null) {
                    mRecruitLoadingDialog.destory();
                    mRecruitLoadingDialog = null;
                }
                RecruitApiHelper.isRecruitLogin = true;
                if (mRecruitRecordEvent != null) {
                    mRecruitRecordEvent.onLoginSuccess(userId);
                }
                if (mContext != null) {
                    //登录成功后进入排队界面
                    Intent intent = new Intent(mContext, RecruitQueueActivity.class);
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onDisconnect(AnyChatResult result) {
                AnyChatSDK.getInstance().release();
                RecruitApiHelper.isRecruitLogin = false;
                if (mRecruitLoadingDialog != null) {
                    mRecruitLoadingDialog.destory();
                    mRecruitLoadingDialog = null;
                }
                if (mRecruitRecordEvent != null) {
                    mRecruitRecordEvent.onRecruitError(result);
                }
            }
        });
        initOpt.setAppId(loginAppId);
        AnyChatSDK.getInstance().sdkInit(initOpt);
    }

    /**
     * 提示信息处理
     */
    private void handleErrorMsg(String errorMsg) {
        if (mRecruitLoadingDialog != null) {
            mRecruitLoadingDialog.destory();
            mRecruitLoadingDialog = null;
        }
        if (mRecruitRecordEvent == null) {
            return;
        }
        mRecruitRecordEvent.onRecruitError(new AnyChatResult(100901, errorMsg));
    }

    /**
     * 注销登出
     */
    public void release() {
        AnyChatSDK.getInstance().release();
        RecruitApiHelper.isRecruitLogin = false;
        mRecruitRecordEvent = null;
        if (mRecruitGlobalModel != null) {
            mRecruitGlobalModel.release();
            mRecruitGlobalModel = null;
        }
        mContext = null;
        mInstance = null;
    }
}