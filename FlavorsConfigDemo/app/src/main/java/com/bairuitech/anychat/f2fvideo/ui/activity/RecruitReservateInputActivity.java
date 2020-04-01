package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.f2fvideo.AnyChatVideoApp;
import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.ApiManager;
import com.bairuitech.anychat.f2fvideo.logic.Config;
import com.bairuitech.anychat.f2fvideo.logic.model.AccessRouteModel;
import com.bairuitech.anychat.f2fvideo.logic.model.ParamDataModel;
import com.bairuitech.anychat.f2fvideo.ui.view.CustomLoadingDialog;
import com.bairuitech.anychat.f2fvideo.utils.DownloadAsyncTask;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;
import com.bairuitech.anychat.f2fvideo.utils.FileUtils;
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
import java.util.List;

/**
 * @describe: 远程招聘预约码录入界面
 * @author: yyh
 * @createTime: 2020/2/7 11:18
 * @className: RecruitReservateInputActivity
 */
public class RecruitReservateInputActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private EditText mInputReservateCodeView;//预约编码

    private CustomLoadingDialog mLoadingDialog;

    private AnyChatVideoApp mAnyChatVideoApp;

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
        setContentView(R.layout.activity_recruit_reservate);
        this.mAnyChatVideoApp = (AnyChatVideoApp) getApplication();

        initView();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        ImageButton titleButton = (ImageButton) findViewById(R.id.title_left_img_btn);
        titleView.setText("预约信息");
        titleButton.setImageResource(R.mipmap.ico_back);
        titleButton.setOnClickListener(this);

        this.findViewById(R.id.bt_next).setOnClickListener(this);
        this.mInputReservateCodeView = (EditText) findViewById(R.id.input_reservate_code);
        this.mInputReservateCodeView.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkInput();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.title_left_img_btn) {
            finish();
        } else if (viewId == R.id.bt_next) {
            checkInput();
        }
    }

    private void checkInput() {
        String reservateNo = mInputReservateCodeView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(reservateNo)) {
            Toast.makeText(this, R.string.input_reservate_code, Toast.LENGTH_SHORT).show();
            return;
        }

        //远程招聘根据appId和appTypeCode获取企业播报相关信息
        getRecruitCompanyInfo(reservateNo);
    }

    /**
     * 远程招聘根据appId和appTypeCode获取企业播报相关信息
     */
    private void getRecruitCompanyInfo(final String reservateNo) {
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
                                        if (object != null)
                                            if (object.has(Config.PROMOFILENO)) {
                                                if (!object.get(Config.PROMOFILENO).isJsonNull()) {
                                                    String promoFileNo = object.get(Config.PROMOFILENO).getAsString();
                                                    if (!EmptyUtils.isNullOrEmpty(promoFileNo)) {
                                                        String fileMd5 = null;
                                                        if (object.has(Config.FILEMD5) && !object.get(Config.FILEMD5).isJsonNull()) {
                                                            fileMd5 = object.get(Config.FILEMD5).getAsString();
                                                        }
                                                        downLoadCompanyVideo(reservateNo, promoFileNo, promoFileNo, fileMd5);
                                                        return;
                                                    }
                                                }
                                            }
                                    }
                                }
                            }
                        }
                        getRecruitUserInfo(reservateNo, "");//getRecruitCompanyInfo
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogTools.e("GetCompanyInfoFailure", e.toString());
                        getRecruitUserInfo(reservateNo, "");//onFailure
                    }
                });
    }

    /**
     * 下载企业播报视频文件
     */
    private void downLoadCompanyVideo(final String reservateNo, String fileNo, String fileName, String fileMd5) {
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
                getRecruitUserInfo(reservateNo, file.getAbsolutePath());//onDownloadComplete
                return;
            }
        }

        String fileUrl = ApiManager.getDownRecruitCompanyFileUrl(this, fileNo);
        LogTools.e("DownLoadCompanyVideoUrl:", fileUrl);
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(fileUrl);
        downloadAsyncTask.setDownloadListener(new DownloadAsyncTask.DownloadListener() {
            @Override
            public void onDownloading(float progress) {
            }

            @Override
            public void onDownloadComplete(String path) {
                LogTools.e("OnDownloadComplete", path);
                getRecruitUserInfo(reservateNo, path);//onDownloadComplete
            }

            @Override
            public void onDownloadError(String msg) {
                LogTools.e("OnDownloadError", msg);
                getRecruitUserInfo(reservateNo, "");//onDownloadError
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
    private void getRecruitUserInfo(final String reservateNo, final String recruitVideoPath) {
        String appId = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID);
        ApiManager.getRecruitUserInfo(this, appId, Config.APPTYPE_RECRUIT,
                reservateNo, new OkHttpUtils.BaseCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.destory();
                            mLoadingDialog = null;
                        }
                        String msg = null;
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
                                                        String custName = null;
                                                        String agentOrgName = null;
                                                        List<ParamDataModel.ContentEntity.GroupDataEntity> groupData = contentEntity.getGroupData();
                                                        if (groupData != null && !groupData.isEmpty()) {
                                                            for (ParamDataModel.ContentEntity.GroupDataEntity groupDataEntity : groupData) {
                                                                if (groupDataEntity != null) {
                                                                    if (EmptyUtils.equalsNotNull(groupDataEntity.getKey(), Config.CUST_NAME)) {
                                                                        custName = groupDataEntity.getValue();
                                                                    }
                                                                    if (EmptyUtils.equalsNotNull(groupDataEntity.getKey(), Config.AGENT_ORG_NAME)) {
                                                                        agentOrgName = groupDataEntity.getValue();
                                                                    }
                                                                    if (!EmptyUtils.isNullOrEmpty(custName) && !EmptyUtils.isNullOrEmpty(agentOrgName)) {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            if (!EmptyUtils.isNullOrEmpty(custName)) {
                                                                startLogin(custName, agentOrgName, reservateNo, recruitVideoPath);
                                                                return;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                msg = routeModel.getMsg();
                            }
                        }
                        handleErrorMsg(EmptyUtils.isNullOrEmpty(msg) ? "预约号不存在" : msg);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogTools.e("GetRecruitUserFailure", e.toString());
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
    private void startLogin(String clientName, String agentOrgName, String reservateNo, String recruitVideoPath) {

        //视频组件外部调用传参模型实体类
        RecruitTransferModel recruitTransferModel = new RecruitTransferModel();
        //面试预约码
        recruitTransferModel.setReservationNo(reservateNo);
        //登录用户昵称(非必传)
        recruitTransferModel.setNickName(clientName);
        //登录业务系统用户身份唯一标识（必传）
        recruitTransferModel.setStrUserId(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT));
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
                UIUtils.makeToast(RecruitReservateInputActivity.this,
                        result.errCode + "  " + result.errMsg, Toast.LENGTH_SHORT).show();
            }

            /**
             * 面试完成回调
             */
            @Override
            public void onRecruitCompleted(AnyChatResult result) {
                LogTools.e("OnRecruitCompleted", result.errCode + "  " + result.errMsg);
                UIUtils.makeToast(RecruitReservateInputActivity.this, result.errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.destory();
            mLoadingDialog = null;
        }
    }
}