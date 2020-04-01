package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.bairuitech.anychat.f2fvideo.AnyChatVideoApp;
import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.ApiManager;
import com.bairuitech.anychat.f2fvideo.logic.Config;
import com.bairuitech.anychat.f2fvideo.logic.model.AccessRouteModel;
import com.bairuitech.anychat.f2fvideo.logic.model.ParamDataModel;
import com.bairuitech.anychat.f2fvideo.logic.model.WaitRecruitModel;
import com.bairuitech.anychat.f2fvideo.ui.adapter.MessageAdapter;
import com.bairuitech.anychat.f2fvideo.ui.view.CustomLoadingDialog;
import com.bairuitech.anychat.f2fvideo.utils.DownloadAsyncTask;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;
import com.bairuitech.anychat.f2fvideo.utils.FileUtils;
import com.bairuitech.anychat.f2fvideo.utils.LocationUtil;
import com.bairuitech.anychat.f2fvideo.utils.LogTools;
import com.bairuitech.anychat.f2fvideo.utils.SharedPreferencesUtils;
import com.bairuitech.anychat.f2fvideo.utils.UIUtils;
import com.bairuitech.anychat.f2fvideo.utils.net.OkHttpUtils;
import com.bairuitech.anychat.main.AnyChatResult;
import com.bairuitech.anychat.recruitment.logic.BRRecruitSDK;
import com.bairuitech.anychat.recruitment.logic.interf.RecruitRecordEvent;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitBusinessModel;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitCompanyModel;
import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitTransferModel;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @describe: 应用办理选择界面
 * @author: yyh
 * @createTime: 2019/7/30 17:47
 * @className: SelectAppTypeActivity
 */
public class SelectAppTypeActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, LocationUtil.LocationChangeCallBack {

    private AnyChatVideoApp mAnyChatVideoApp;

    private ImageButton mTitleRightButton;

    private View mMessageTipRootView;
    private ListView mMessageListView;

    private List<WaitRecruitModel.WaitRecruitMessage> mMessageLists;
    private MessageAdapter mMessageAdapter;

    private String mLoginAccount;
    private String mLoginAppId;
    private CustomLoadingDialog mLoadingDialog;
    private ArrayList<String> mAppTypeList;

    private boolean mIsFirstEnter = true;

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
        setContentView(R.layout.activity_select_app_type);
        initData();
        initView();
    }

    private void initData() {
        this.mAnyChatVideoApp = (AnyChatVideoApp) getApplication();

        this.mLoginAccount = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT);
        this.mLoginAppId = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID);

        Intent intent = getIntent();
        if (intent.hasExtra(Config.EXTRA_APPTYPE_LIST)) {
            mAppTypeList = intent.getStringArrayListExtra(Config.EXTRA_APPTYPE_LIST);
        }

        //初始化定位信息
        LocationUtil.getInstance(this).setCallBack(this);
        AMapLocation location = LocationUtil.getInstance(this).showLocation();
        saveAddress(location);
    }

    private void initView() {
        View view = findViewById(R.id.title_bar_view);
        view.setBackgroundResource(R.color.transparent);
        ImageButton titleButton = (ImageButton) findViewById(R.id.title_left_img_btn);
        titleButton.setImageResource(R.mipmap.ico_back);
        titleButton.setOnClickListener(this);

        if (mAppTypeList == null || mAppTypeList.isEmpty()) {
            finish();
            return;
        }

        this.mTitleRightButton = (ImageButton) findViewById(R.id.title_right_img_btn);
        this.mTitleRightButton.setOnClickListener(this);

        View recordAPPView = this.findViewById(R.id.select_remote_record_view);
        View recruitAPPView = this.findViewById(R.id.select_remote_recruit_view);
        View meetingAPPView = this.findViewById(R.id.select_remote_meeting_view);
        //远程招聘
        if (mAppTypeList.contains(Config.APPTYPE_RECRUIT)) {
            recruitAPPView.setVisibility(View.VISIBLE);
            recruitAPPView.setOnClickListener(this);
            //待办消息关闭view
            this.findViewById(R.id.message_close_view).setOnClickListener(this);
            this.mMessageTipRootView = findViewById(R.id.message_tip_root_view);
            this.mMessageListView = (ListView) findViewById(R.id.list_view);
            mMessageListView.setOnItemClickListener(this);
        }
        //远程双录
        if (mAppTypeList.contains(Config.APPTYPE_RECORD)) {
            recordAPPView.setVisibility(View.VISIBLE);
            recordAPPView.setOnClickListener(this);
        }
        //视频会议
        if (mAppTypeList.contains(Config.APPTYPE_MEETING)) {
            meetingAPPView.setVisibility(View.VISIBLE);
            meetingAPPView.setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAppTypeList.contains(Config.APPTYPE_RECRUIT)) {
            //获取用户待办面试信息
            getRecruitWaitMessage();
        }
    }

    private void showRecruitMessageData(List<WaitRecruitModel.WaitRecruitMessage> messages) {
        if (mMessageAdapter == null) {
            mMessageAdapter = new MessageAdapter(this, messages);
            mMessageListView.setAdapter(mMessageAdapter);
        } else {
            mMessageAdapter.setLists(messages);
        }
    }

    /**
     * 获取用户待办面试信息
     */
    private void getRecruitWaitMessage() {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.getInstance();
        }
        mLoadingDialog.showLoadingDialog(this, "", false);
        ApiManager.getRecruitWaitMessage(this, mLoginAccount, mLoginAppId,
                new OkHttpUtils.BaseCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.destory();
                            mLoadingDialog = null;
                        }
                        WaitRecruitModel waitRecruitModel = WaitRecruitModel.fromJson(jsonObject.toString());
                        if (waitRecruitModel != null) {
                            int errorcode = waitRecruitModel.getErrorcode();
                            if (errorcode == 0) {
                                List<WaitRecruitModel.WaitRecruitMessage> messages = waitRecruitModel.getContent();
                                if (messages != null && !messages.isEmpty()) {
                                    if (mMessageLists == null) {
                                        mMessageLists = new ArrayList<WaitRecruitModel.WaitRecruitMessage>();
                                    }
                                    mMessageLists = messages;
                                    //获取面试待办信息
                                    mTitleRightButton.setVisibility(View.VISIBLE);
                                    if (mIsFirstEnter) {
                                        mTitleRightButton.setImageResource(R.mipmap.ico_no_upcoming);
                                        mMessageTipRootView.setVisibility(View.VISIBLE);
                                    } else {
                                        mTitleRightButton.setImageResource(mMessageTipRootView.getVisibility() == View.VISIBLE ?
                                                R.mipmap.ico_no_upcoming : R.mipmap.ico_has_upcoming);
                                    }
                                    showRecruitMessageData(mMessageLists);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.destory();
                            mLoadingDialog = null;
                        }
                        LogTools.e("GetRecruitWaitMessageOnFailure ", e.toString());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.title_left_img_btn) {
            finish();
        } else if (viewId == R.id.title_right_img_btn) {
            mTitleRightButton.setImageResource(R.mipmap.ico_no_upcoming);
            mMessageTipRootView.setVisibility(View.VISIBLE);
        } else if (viewId == R.id.message_close_view) {//待办消息关闭view
            mMessageTipRootView.setVisibility(View.GONE);
            if (mIsFirstEnter) {
                mIsFirstEnter = false;
            }
        } else if (viewId == R.id.select_remote_record_view) {//远程双录
            Intent intent = new Intent(this, SelectServiceActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.select_remote_recruit_view) {//远程招聘
            Intent intent = new Intent(this, RecruitReservateInputActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.select_remote_meeting_view) {//视频会议

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WaitRecruitModel.WaitRecruitMessage recruitMessage = mMessageLists.get(position);
        if (recruitMessage != null) {
            //远程招聘根据appId和appTypeCode获取企业播报相关信息
            getRecruitCompanyInfo(recruitMessage);
        }
    }

    /**
     * 远程招聘根据appId和appTypeCode获取企业播报相关信息
     */
    private void getRecruitCompanyInfo(final WaitRecruitModel.WaitRecruitMessage recruitMessage) {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.getInstance();
        }
        mLoadingDialog.showLoadingDialog(this, "正在加载资源文件", false);
        String appId = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID);
        ApiManager.getRecruitCompanyInfo(this, appId, Config.APPTYPE_RECRUIT,
                new OkHttpUtils.BaseCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            if (jsonObject.has(Config.ERROR_CODE_LOW)) {
                                int errorcode = jsonObject.get(Config.ERROR_CODE_LOW).getAsInt();
                                if (errorcode == 0) {
                                    if (jsonObject.has(Config.CONTENT)) {
                                        JsonObject object = null;
                                        try {
                                            object = jsonObject.getAsJsonObject(Config.CONTENT);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (object != null && object.has(Config.PROMOFILENO)) {
                                            if (!object.get(Config.PROMOFILENO).isJsonNull()) {
                                                String promoFileNo = object.get(Config.PROMOFILENO).getAsString();
                                                if (!EmptyUtils.isNullOrEmpty(promoFileNo)) {
                                                    String fileMd5 = null;
                                                    if (object.has(Config.FILEMD5) && !object.get(Config.FILEMD5).isJsonNull()) {
                                                        fileMd5 = object.get(Config.FILEMD5).getAsString();
                                                    }
                                                    downLoadCompanyVideo(recruitMessage, promoFileNo, promoFileNo, fileMd5);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //远程招聘根据面试预约码、appId和apptypecode获取用户信息
                        getRecruitUserInfo(recruitMessage, "");//onSuccess
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogTools.e("GetCompanyInfoFailure", e.toString());
                        //远程招聘根据面试预约码、appId和apptypecode获取用户信息
                        getRecruitUserInfo(recruitMessage, "");//onFailure
                    }
                });
    }

    /**
     * 下载企业播报视频文件
     */
    private void downLoadCompanyVideo(final WaitRecruitModel.WaitRecruitMessage recruitMessage,
                                      String fileNo, String fileName, String fileMd5) {
        String savePath = FileUtils.getDiskCacheDir(this) + "/AnyChat/DownLoad";
        if (EmptyUtils.isNullOrEmpty(fileName)) {
            fileName = "companyVideo.mp4";
        }
        if (!fileName.endsWith(".mp4") && !fileName.contains(".mp4")) {
            fileName = fileName + ".mp4";
        }
        //根据MD5校验文件是否下载过
        File file = new File(savePath + "/" + fileName);
        if (file.exists()) {
            String localFileMD5 = FileUtils.getFileMD5(file);
            LogTools.e("===FileMD5 ", "LocalMD5:" + localFileMD5 + "  FileMd5:" + fileMd5);
            if (EmptyUtils.equalsIgnoreNotNull(localFileMD5, fileMd5)) {
                getRecruitUserInfo(recruitMessage, file.getAbsolutePath());//onDownloadComplete
                return;
            }
        }
        String fileUrl = ApiManager.getDownRecruitCompanyFileUrl(this, fileNo);
        LogTools.e("DownLoadCompanyVideo:", fileUrl);
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(fileUrl);
        downloadAsyncTask.setDownloadListener(new DownloadAsyncTask.DownloadListener() {
            @Override
            public void onDownloading(float progress) {
            }

            @Override
            public void onDownloadComplete(String path) {
                LogTools.e("OnDownloadComplete", path);
                getRecruitUserInfo(recruitMessage, path);//onDownloadComplete
            }

            @Override
            public void onDownloadError(String msg) {
                LogTools.e("OnDownloadError", msg);
                getRecruitUserInfo(recruitMessage, "");//onDownloadError
            }

            @Override
            public void onDownloadCancel() {
                LogTools.e("OnDownloadCancel", "===");
            }
        });
        downloadAsyncTask.execute(fileName, savePath);
    }

    /**
     * 远程招聘根据面试预约码、appId和apptypecode获取用户信息
     */
    private void getRecruitUserInfo(final WaitRecruitModel.WaitRecruitMessage recruitMessage, final String recruitVideoPath) {
        //预约编号
        String reservateNo = recruitMessage.getReservationNo();
        //应用id
        String appId = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID);
        ApiManager.getRecruitUserInfo(this, appId, Config.APPTYPE_RECRUIT,
                reservateNo, new OkHttpUtils.BaseCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.destory();
                            mLoadingDialog = null;
                        }

                        String agentOrgName = null;
                        if (jsonObject != null) {
                            AccessRouteModel routeModel = AccessRouteModel.fromJson(jsonObject.toString());
                            if (routeModel != null) {
                                int errorcode = routeModel.getErrorcode();
                                if (errorcode == 0) {
                                    AccessRouteModel.ContentEntity content = routeModel.getContent();
                                    if (content != null) {
                                        String paramData = content.getParamData();
                                        ParamDataModel dataModel = ParamDataModel.fromJson(paramData);
                                        if (dataModel != null) {
                                            List<ParamDataModel.ContentEntity> paramContent = dataModel.getContent();
                                            if (paramContent != null && !paramContent.isEmpty()) {
                                                for (ParamDataModel.ContentEntity contentEntity : paramContent) {
                                                    if (contentEntity != null) {
                                                        List<ParamDataModel.ContentEntity.GroupDataEntity> groupData = contentEntity.getGroupData();
                                                        if (groupData != null && !groupData.isEmpty()) {
                                                            for (ParamDataModel.ContentEntity.GroupDataEntity groupDataEntity : groupData) {
                                                                if (groupDataEntity != null) {
                                                                    if (EmptyUtils.equalsNotNull(groupDataEntity.getKey(), Config.AGENT_ORG_NAME)) {
                                                                        agentOrgName = groupDataEntity.getValue();
                                                                    }
                                                                    if (!EmptyUtils.isNullOrEmpty(agentOrgName)) {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        startRecruit(agentOrgName, recruitMessage, recruitVideoPath);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogTools.e("GetRecruitUserFailure", e.toString());
                        if (mLoadingDialog != null) {
                            mLoadingDialog.destory();
                            mLoadingDialog = null;
                        }
                        handleErrorMsg("客户信息获取异常");
                    }
                });
    }

    private void handleErrorMsg(String errorMsg) {
        if (mLoadingDialog != null) {
            mLoadingDialog.destory();
            mLoadingDialog = null;
        }
        UIUtils.makeToast(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 开始调用
     */
    private void startRecruit(String agentOrgName, WaitRecruitModel.WaitRecruitMessage recruitMessage, String recruitVideoPath) {
        //预约编号
        String reservateNo = recruitMessage.getReservationNo();
        //客户姓名
        String custName = recruitMessage.getCustName();
        //客户手机号
        String custPhone = recruitMessage.getCustPhone();

        //视频组件外部调用传参模型实体类
        RecruitTransferModel recruitTransferModel = new RecruitTransferModel();
        //面试预约码（必传）
        recruitTransferModel.setReservationNo(reservateNo);
        //登录用户昵称(非必传)
        recruitTransferModel.setNickName(custName);
        //登录业务系统用户身份唯一标识（必传）
        recruitTransferModel.setStrUserId(custPhone);
        //登录ip(必传)
        recruitTransferModel.setLoginIp(SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_ANYCHAT_IP, ApiManager.ANYCHAT_LOGIN_IP));
        //登录端口(必传)
        recruitTransferModel.setLoginPort(SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_ANYCHAT_PORT, ApiManager.ANYCHAT_LOGIN_PORT));
        //登录appId(必传)
        recruitTransferModel.setLoginAppId(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID));

        //业务信息相关（非必传）
        RecruitBusinessModel businessModel = new RecruitBusinessModel();
        //面试官职位
        if (!EmptyUtils.isNullOrEmpty(agentOrgName)) {
            businessModel.setAgentOrgName(agentOrgName);
        }
        //应聘者地理位置信息
        String address = mAnyChatVideoApp.getAddress();
        if (!EmptyUtils.isNullOrEmpty(address)) {
            businessModel.setAddress(address);
        }
        if (!EmptyUtils.isNullOrEmpty(recruitVideoPath)) {
            //企业信息
            RecruitCompanyModel companyModel = new RecruitCompanyModel();
            //设置企业播报视频文件路径信息
            companyModel.setFullUrl(recruitVideoPath);
            businessModel.setRecruitCompanyModel(companyModel);
        }
        //设置业务信息对象
        recruitTransferModel.setRecruitBusinessModel(businessModel);

        //视频组件唤起调用接口
        BRRecruitSDK.getInstance().startRecruit(this, recruitTransferModel, new RecruitRecordEvent() {

            /**
             * 登录完成回调
             */
            @Override
            public void onLoginSuccess(int userId) {
                LogTools.e("OnLoginSuccess", userId + "");
            }

            /**
             * 异常错误回调
             */
            @Override
            public void onRecruitError(AnyChatResult result) {
                LogTools.e("OnRecruitError", result.errCode + "  " + result.errMsg);
                UIUtils.makeToast(SelectAppTypeActivity.this,
                        result.errCode + "  " + result.errMsg, Toast.LENGTH_SHORT).show();
            }

            /**
             * 面试完成回调
             */
            @Override
            public void onRecruitCompleted(AnyChatResult result) {
                LogTools.e("OnRecruitCompleted", result.errCode + "  " + result.errMsg);
                UIUtils.makeToast(SelectAppTypeActivity.this, result.errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位
        LocationUtil.getInstance(this).destroyLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            if (0 == location.getErrorCode()) {
                LogTools.e("OnLocationChanged", location.toString());
                saveAddress(location);
            } else {
                Log.e("===定位失败", "错误码:" + location.getErrorCode() + "\n"
                        + "错误信息:" + location.getErrorInfo() + "\n"
                        + "错误描述:" + location.getLocationDetail() + "\n");
            }
        }
    }

    private void saveAddress(AMapLocation location) {
        if (mAnyChatVideoApp != null && location != null) {
            String address = location.getAddress();
            mAnyChatVideoApp.setAddress(!EmptyUtils.isNullOrEmpty(address) ? address :
                    EmptyUtils.getNotNullString(location.getPoiName()));
        }
    }
}