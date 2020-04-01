package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.component.logic.BRRecordCloudSDK;
import com.bairuitech.anychat.component.logic.RequestField;
import com.bairuitech.anychat.component.logic.interf.VideoRecordEvent;
import com.bairuitech.anychat.component.logic.model.TransferModel;
import com.bairuitech.anychat.component.logic.model.VideoCallModel;
import com.bairuitech.anychat.f2fvideo.AnyChatVideoApp;
import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.ApiManager;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;
import com.bairuitech.anychat.f2fvideo.utils.SharedPreferencesUtils;
import com.bairuitech.anychat.f2fvideo.utils.UIUtils;
import com.bairuitech.anychat.main.AnyChatResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @describe: 预约入口选择信息录入界面
 * @author: yyh
 * @createTime: 2019/7/30 17:56
 * @className: ReservateBusinessActivity
 */
public class ReservateBusinessActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mInputClientNameView;//客户名
    private EditText mInputClientPhoneView;//客户手机号
    private EditText mInputIdcardAddressView;//证件地址
    private EditText mInputIdcardView;//身份证信息
    private RadioButton mSelectmale;//性别 男
    private RadioButton mSelectFemale;//性别 女

    private EditText mInputThirdTradeNumView;//第三方业务流水号
    private EditText mInputReservateCodeView;//预约编码

    private AnyChatVideoApp mAnyChatVideoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservate_business);
        this.mAnyChatVideoApp = (AnyChatVideoApp) getApplication();
        initView();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        ImageButton titleButton = (ImageButton) findViewById(R.id.title_left_img_btn);
        titleView.setText(R.string.buisness_title);
        titleButton.setImageResource(R.mipmap.ico_back);
        titleButton.setOnClickListener(this);

        this.findViewById(R.id.bt_next).setOnClickListener(this);
        this.mInputClientNameView = (EditText) findViewById(R.id.input_client);
        this.mInputClientPhoneView = (EditText) findViewById(R.id.input_phone);
        this.mInputIdcardAddressView = (EditText) findViewById(R.id.input_idcard_address);
        this.mInputIdcardView = (EditText) findViewById(R.id.input_idcard_num);
        this.mSelectmale = (RadioButton) findViewById(R.id.select_male);
        this.mSelectFemale = (RadioButton) findViewById(R.id.select_female);

        this.mInputThirdTradeNumView = (EditText) findViewById(R.id.input_thirdtrade_num);
        this.mInputReservateCodeView = (EditText) findViewById(R.id.input_reservate_code);

        String loginAccount = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT);
        if (!EmptyUtils.isNullOrEmpty(loginAccount)) {
            this.mInputClientPhoneView.setText(loginAccount);
        }
        //读取保存的客户信息
        readSaveData();
    }

    /**
     * 读取保存信息
     */
    private void readSaveData() {
        String customerInfo = SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_CUSTOMER_RESERVATE_INFO);
        if (!EmptyUtils.isNullOrEmpty(customerInfo)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(customerInfo);
                if (jsonObject.has("thirdTradeNo")) {
                    mInputThirdTradeNumView.setText(EmptyUtils.getNotNullString(jsonObject.getString("thirdTradeNo")));
                }
                if (jsonObject.has("userName")) {
                    mInputClientNameView.setText(EmptyUtils.getNotNullString(jsonObject.getString("userName")));
                }
                if (jsonObject.has("userSex")) {
                    if (EmptyUtils.equalsIgnoreNotNull(jsonObject.getString("userSex"), "0")) {
                        mSelectmale.setChecked(true);
                    } else {
                        mSelectFemale.setChecked(true);
                    }
                }
                if (jsonObject.has("idcardAddress")) {
                    mInputIdcardAddressView.setText(EmptyUtils.getNotNullString(jsonObject.getString("idcardAddress")));
                }
                if (jsonObject.has("idcardNum")) {
                    mInputIdcardView.setText(EmptyUtils.getNotNullString(jsonObject.getString("idcardNum")));
                }
                if (jsonObject.has("reservationNo")) {
                    mInputReservateCodeView.setText(EmptyUtils.getNotNullString(jsonObject.getString("reservationNo")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存信息
     */
    private void saveBusinessData(String thirdTradeNumber, String clientName, String clientPhone,
                                  String clientUserSex, String clientIdAddress, String clientIdcardNum,
                                  String reservateCode) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("thirdTradeNo", thirdTradeNumber);//第三方业务流水
            jsonObject.put("userName", clientName);//客户姓名
            jsonObject.put("userPhone", clientPhone);//客户手机
            jsonObject.put("userSex", clientUserSex);//男0，女1
            jsonObject.put("idcardAddress", clientIdAddress);//证件地址
            jsonObject.put("idcardNum", clientIdcardNum);//证件号码
            jsonObject.put("reservationNo", reservateCode);//预约编码
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_CUSTOMER_RESERVATE_INFO,
                jsonObject.toString());
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
        String clientName = mInputClientNameView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(clientName)) {
            Toast.makeText(this, R.string.longin_input_name, Toast.LENGTH_SHORT).show();
            return;
        }
        String clientPhone = mInputClientPhoneView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(clientPhone)) {
            Toast.makeText(this, R.string.longin_input_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        String clientIdAddress = mInputIdcardAddressView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(clientIdAddress)) {
            Toast.makeText(this, R.string.longin_input_idcard_address, Toast.LENGTH_SHORT).show();
            return;
        }
        String clientIdcardNum = mInputIdcardView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(clientIdcardNum)) {
            Toast.makeText(this, R.string.longin_input_idcard_num, Toast.LENGTH_SHORT).show();
            return;
        }
        String clientUserSex = mSelectmale.isChecked() ? "0" : "1";
        String thirdTradeNumber = mInputThirdTradeNumView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(thirdTradeNumber)) {
            Toast.makeText(this, R.string.input_thirdtrade_num, Toast.LENGTH_SHORT).show();
            return;
        }
        String reservateCode = mInputReservateCodeView.getText().toString();
        if (EmptyUtils.isNullOrEmpty(reservateCode)) {
            Toast.makeText(this, R.string.input_reservate_code, Toast.LENGTH_SHORT).show();
            return;
        }

        //保存客户业务信息下次登陆直接读取
        saveBusinessData(thirdTradeNumber, clientName, clientPhone, clientUserSex,
                clientIdAddress, clientIdcardNum, reservateCode);
        //业务视频呼叫参数信息
        String videoCallParameter = getVideoCallParameter(thirdTradeNumber, clientName, clientPhone,
                clientUserSex, clientIdAddress, clientIdcardNum, reservateCode);
        //开始调用
        startLogin(videoCallParameter);
    }

    /**
     * 开始调用
     *
     * @param videoCallParameter 视频呼叫参数
     */
    private void startLogin(String videoCallParameter) {
        //视频组件外部调用传参模型实体类
        TransferModel model = new TransferModel();
        //登录用户昵称(非必传)
        model.setNickName(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT));
        //登录业务系统用户身份唯一标识（必传）
        model.setStrUserId(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT));
        //登录ip(必传)
        model.setLoginIp(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_ANYCHAT_IP, ApiManager.ANYCHAT_LOGIN_IP));
        //登录端口(必传)
        model.setLoginPort(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_ANYCHAT_PORT, ApiManager.ANYCHAT_LOGIN_PORT));
        //登录appId(必传)
        model.setLoginAppId(SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_APPID));
        //视频呼叫参数(必传)
        model.setVideoCallParameter(videoCallParameter);
        //签名图片文件绝对路径信息(非必传，不传时，坐席端发起手写签名时,app端将无法进行手写签名)
        model.setSignImagePath(mAnyChatVideoApp.getSignaturePath());
        //手写签名展示位置在模板图片的坐标信息
        // (可不传，不传或者参数值不在要求范围值时，手写签名默认合成展示在签名模板图的右下角位置
        // (距离签名模板图右边距40，下边距40的位置))
        //model.setSignPositionX(530);
        // model.setSignPositionY(440);

        //视频组件唤起调用接口
        BRRecordCloudSDK.getInstance().start(this, model, new VideoRecordEvent() {

            /**
             * 登录完成回调
             */
            @Override
            public void onLoginSuccess(int userId) {
                Log.e("===onLoginSuccess", userId + "");
            }

            /**
             * 异常错误回调
             */
            @Override
            public void onError(AnyChatResult result) {
                Log.e("===Error", result.errCode + "  " + result.errMsg);
                UIUtils.makeToast(ReservateBusinessActivity.this,
                        result.errCode + "  " + result.errMsg, Toast.LENGTH_SHORT).show();
            }

            /**
             * 双录完成回调
             */
            @Override
            public void onRecordCompleted(AnyChatResult result) {
                Log.e("===onRecordCompleted", result.errCode + "  " + result.errMsg);
                UIUtils.makeToast(ReservateBusinessActivity.this, result.errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取业务视频呼叫参数信息
     *
     * @param thirdTradeNumber 第三方业务流水号
     * @param clientName       客户名称
     * @param clientPhone      客户手机号
     * @param clientUserSex    客户性别
     * @param clientIdAddress  证件地址
     * @param clientIdcardNum  证件号码
     * @param reservateCode    预约编码
     */
    private String getVideoCallParameter(String thirdTradeNumber, String clientName, String clientPhone,
                                         String clientUserSex, String clientIdAddress, String clientIdcardNum,
                                         String reservateCode) {
        //组件内部封装的视频呼叫参数Bean实体类
        VideoCallModel videoCallModel = new VideoCallModel();
        videoCallModel.setThirdTradeNo(thirdTradeNumber);//业务流水号

        //扩展参数相关信息 数据格式：json字符串格式(非必传)
        try {
            JSONObject expansion = new JSONObject();
            String address = mAnyChatVideoApp.getAddress();
            if (!EmptyUtils.isNullOrEmpty(address)) {
                //添加位置信息
                expansion.put(RequestField.EX_ADDRESS, address);
            }
            //设置扩展参数信息(json字符串格式)
            videoCallModel.setExpansion(expansion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //业务信息展示内容
        ArrayList<VideoCallModel.Content> contents = new ArrayList<>();
        videoCallModel.setContent(contents);

        //客户信息相关
        VideoCallModel.Content userInfo = new VideoCallModel.Content();
        //分组展示名称
        userInfo.setGroupName("客户信息");
        //分组排序号
        userInfo.setGroupOrder(1);
        //分组展示内容
        ArrayList<VideoCallModel.Content.GroupData> groupUserInfos = new ArrayList<>();
        //客户名称
        VideoCallModel.Content.GroupData userName = new VideoCallModel.Content.GroupData();
        userName.setName("客户名称");//属性名称
        userName.setKey("userName");//属性标识
        userName.setValue(clientName);//属性值
        groupUserInfos.add(userName);
        //客户手机号
        VideoCallModel.Content.GroupData userPhone = new VideoCallModel.Content.GroupData();
        userPhone.setName("客户手机");
        userPhone.setKey("userPhone");
        userPhone.setValue(clientPhone);
        groupUserInfos.add(userPhone);
        //客户性别
        VideoCallModel.Content.GroupData userSex = new VideoCallModel.Content.GroupData();
        userSex.setName("客户性别");
        userSex.setKey("userSex");
        userSex.setValue(clientUserSex);
        groupUserInfos.add(userSex);
        //证件类型
        VideoCallModel.Content.GroupData address = new VideoCallModel.Content.GroupData();
        address.setName("证件地址");
        address.setKey("idcardAddress");
        address.setValue(clientIdAddress);
        groupUserInfos.add(address);
        //证件号码
        VideoCallModel.Content.GroupData idcardNum = new VideoCallModel.Content.GroupData();
        idcardNum.setName("证件号码");
        idcardNum.setKey("idcardNum");
        idcardNum.setValue(clientIdcardNum);
        groupUserInfos.add(idcardNum);
        //设置客户信息集合
        userInfo.setGroupData(groupUserInfos);
        //添加客户信息对象
        contents.add(userInfo);

        //预约信息相关
        VideoCallModel.Content businessInfo = new VideoCallModel.Content();
        //分组展示名称
        businessInfo.setGroupName("预约信息");
        //分组排序号
        businessInfo.setGroupOrder(2);
        //分组展示内容
        ArrayList<VideoCallModel.Content.GroupData> groupBusinessInfos = new ArrayList<>();
        //预约编码
        VideoCallModel.Content.GroupData reservationNo = new VideoCallModel.Content.GroupData();
        reservationNo.setName("预约编码");
        reservationNo.setKey("reservationNo");
        reservationNo.setValue(reservateCode);
        groupBusinessInfos.add(reservationNo);
        //设置业务信息集合
        businessInfo.setGroupData(groupBusinessInfos);
        //添业务信息对象
        contents.add(businessInfo);
        return videoCallModel.toJson();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}