package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.component.logic.BRRecordCloudSDK;
import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.ApiManager;
import com.bairuitech.anychat.f2fvideo.logic.Config;
import com.bairuitech.anychat.f2fvideo.ui.view.CustomLoadingDialog;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;
import com.bairuitech.anychat.f2fvideo.utils.FileUtils;
import com.bairuitech.anychat.f2fvideo.utils.LogTools;
import com.bairuitech.anychat.f2fvideo.utils.SharedPreferencesUtils;
import com.bairuitech.anychat.f2fvideo.utils.UIUtils;
import com.bairuitech.anychat.f2fvideo.utils.net.OkHttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * @describe: 登录界面
 * @author: yyh
 * @createTime: 2019/5/16 10:51
 * @className: LoginActivity
 */
public class LoginActivity extends AppCompatActivity implements View.OnTouchListener,
        View.OnClickListener, View.OnFocusChangeListener {

    private View mView;
    private EditText mInputAccountView;
    private EditText mInputLoginIdView;
    private View mClearAccount;
    private View mClearLoginId;

    private CustomLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FileUtils.deleteLogFile(LogTools.LOGNAME);//清空日志文件
        initView();

        String channel_value = getMetaData(this, "CHANNEL_VALUE");
        Log.e("=====", channel_value);
    }

    /**
     * 从Manifest中获取meta-data值
     *
     * @param key 为<meta-data>标签中的name
     */
    public static String getMetaData(Context context, String key) {
        String value = null;
        try {
            PackageManager manager = context.getPackageManager();
            ApplicationInfo appInfo = manager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    private void initView() {
        this.mView = getWindow().getDecorView();
        this.mView.setOnTouchListener(this);

        this.mInputAccountView = (EditText) findViewById(R.id.input_account);
        this.mInputLoginIdView = (EditText) findViewById(R.id.input_appid);

        this.mClearAccount = findViewById(R.id.clear_account);
        this.mClearLoginId = findViewById(R.id.clear_appid);

        this.findViewById(R.id.bt_login).setOnClickListener(this);
        this.findViewById(R.id.set_view).setOnClickListener(this);
        this.mClearAccount.setOnClickListener(this);
        this.mClearLoginId.setOnClickListener(this);

        this.mInputAccountView.setOnFocusChangeListener(this);
        this.mInputLoginIdView.setOnFocusChangeListener(this);

        this.mInputAccountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mInputAccountView.hasFocus()) {
                    if (mInputAccountView.getText().length() > 0) {
                        mClearAccount.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                mClearAccount.setVisibility(View.GONE);
            }
        });
        this.mInputLoginIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mInputLoginIdView.hasFocus()) {
                    if (mInputLoginIdView.getText().length() > 0) {
                        mClearLoginId.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                mClearLoginId.setVisibility(View.GONE);
            }
        });

        //登录账号
        this.mInputAccountView.setText(EmptyUtils.getNotNullString(
                SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT)));
        //登录商户编码信息
        this.mInputLoginIdView.setText(EmptyUtils.getNotNullString(
                SharedPreferencesUtils.get(this, SharedPreferencesUtils.LOGIN_MERCHANT_ID)));

        //sdk版本信息
        String versionInfo = BRRecordCloudSDK.getInstance().getVersionInfo();
        if (!EmptyUtils.isNullOrEmpty(versionInfo)) {
            if (versionInfo.contains("  Build time: ")) {
                versionInfo = versionInfo.replace("  Build time: ", ", ");
            }
        }
        String versionName = UIUtils.getAppVersionName(this);
        if (!EmptyUtils.isNullOrEmpty(versionName)) {
            StringBuilder builder = new StringBuilder();
            builder.append(versionName).append("(").append("kernel").append(versionInfo).append(")");
            versionInfo = builder.toString();
        }
        ((TextView) findViewById(R.id.version_info_view)).setText(EmptyUtils.getNotNullString(versionInfo));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //隐藏输入法
            UIUtils.hideSoftKeyboard(this, mView);
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mInputAccountView) {
            mClearAccount.setVisibility(hasFocus && mInputAccountView.getText().length() > 0 ?
                    View.VISIBLE : View.GONE);
        } else if (v == mInputLoginIdView) {
            mClearLoginId.setVisibility(hasFocus && mInputLoginIdView.getText().length() > 0 ?
                    View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.bt_login) {
            if (checkInput()) {
                doNext();
            }
        } else if (viewId == R.id.clear_account) {
            mInputAccountView.setText("");
        } else if (viewId == R.id.clear_appid) {
            mInputLoginIdView.setText("");
        } else if (viewId == R.id.set_view) {
            Intent intent = new Intent(this, SetActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkInput() {
        String loginPhone = mInputAccountView.getText().toString().trim();
        if (EmptyUtils.isNullOrEmpty(loginPhone)) {
            Toast.makeText(this, R.string.longin_input_tip, Toast.LENGTH_SHORT).show();
            return false;
        }

        String num = "[1][35678]\\d{9}";
        if (!loginPhone.matches(num)) {
            Toast.makeText(this, R.string.login_input_phone_error, Toast.LENGTH_LONG).show();
            return false;
        }
        if (EmptyUtils.isNullOrEmpty(mInputLoginIdView.getText().toString().trim())) {
            Toast.makeText(this, R.string.longin_input_appid_tip, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doNext() {
        //保存登录账号
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_USER_ACCOUNT,
                EmptyUtils.getNotNullString(mInputAccountView.getText().toString().trim()));
        //保存登录商户编码信息
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_MERCHANT_ID,
                EmptyUtils.getNotNullString(mInputLoginIdView.getText().toString()));

        /**
         * 以下接口的调用(根据商户简称获取AnyChat登录appid),
         * 主要是为了便于测试使用,因为appid的全称是一个很长的字符串(如:9716ce33-0baf-41c2-b256-d34e315bfd9a)
         * 这里通过手动输入商户简称调用接口来获取登录appid
         */
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.getInstance();
        }
        mLoadingDialog.showLoadingDialog(this, "", false);
        //获取商户登录信息(根据商户编码获取登录appid信息)
        getMerchantAppId(EmptyUtils.getNotNullString(mInputLoginIdView.getText().toString().trim()));
    }

    /**
     * 获取商户登录信息(根据商户编码获取登录appid信息)
     */
    private void getMerchantAppId(String merchantName) {
        ApiManager.getMerchantAppId(this, merchantName, new OkHttpUtils.BaseCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.destory();
                    mLoadingDialog = null;
                }
                String msg = null;
                if (jsonObject != null) {
                    if (jsonObject.has(Config.ERROR_CODE_LOW)) {
                        if (0 == jsonObject.get(Config.ERROR_CODE_LOW).getAsInt()) {
                            if (jsonObject.has(Config.CONTENT)) {
                                JsonObject contentObject = jsonObject.getAsJsonObject(Config.CONTENT);
                                if (contentObject != null) {
                                    if (contentObject.has(Config.APPID)) {
                                        String appId = contentObject.get(Config.APPID).getAsString();
                                        if (!EmptyUtils.isNullOrEmpty(appId)) {
                                            ArrayList<String> appTypeList = new ArrayList<>();
                                            if (contentObject.has(Config.APPTYPE_MODEL_LIST)) {
                                                JsonArray jsonArray = contentObject.getAsJsonArray(Config.APPTYPE_MODEL_LIST);
                                                if (jsonArray != null && !jsonArray.isJsonNull() && jsonArray.size() > 0) {
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        JsonObject appTypeObject = jsonArray.get(i).getAsJsonObject();
                                                        if (appTypeObject != null) {
                                                            if (appTypeObject.has(Config.APPTYPE_CODE)) {
                                                                String appTypeCode = appTypeObject.get(Config.APPTYPE_CODE).getAsString();
                                                                if (!EmptyUtils.isNullOrEmpty(appTypeCode)) {
                                                                    appTypeList.add(appTypeCode);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (!appTypeList.isEmpty()) {
                                                //保存登录appid
                                                SharedPreferencesUtils.save(LoginActivity.this,
                                                        SharedPreferencesUtils.LOGIN_APPID, appId);
                                                Intent intent = new Intent(LoginActivity.this, SelectAppTypeActivity.class);
                                                intent.putStringArrayListExtra(Config.EXTRA_APPTYPE_LIST, appTypeList);
                                                startActivity(intent);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //获取失败时 先获取后台返回的错误信息
                    if (jsonObject.has(Config.MSG) && !jsonObject.get(Config.MSG).isJsonNull()) {
                        msg = jsonObject.get(Config.MSG).getAsString();
                    }
                }
                handleErrorMsg(EmptyUtils.isNullOrEmpty(msg) ? "商户信息获取异常" : msg);
            }

            @Override
            public void onFailure(Exception e) {
                LogTools.e("GetAppIdOnFailure ", e.toString());
                handleErrorMsg("商户信息获取失败");
            }
        });
    }

    /**
     * 提示信息处理
     */
    private void handleErrorMsg(String errorMsg) {
        if (mLoadingDialog != null) {
            mLoadingDialog.destory();
            mLoadingDialog = null;
        }
        UIUtils.makeToast(this, errorMsg, Toast.LENGTH_SHORT).show();
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