package com.yyh.okhttpretrofit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yyh.okhttpretrofit.R;
import com.yyh.okhttpretrofit.UiAction;
import com.yyh.okhttpretrofit.logic.ApiRequestManager;
import com.yyh.okhttpretrofit.logic.CallbackIds;
import com.yyh.okhttpretrofit.logic.transport.base.Request;
import com.yyh.okhttpretrofit.logic.transport.base.Response;
import com.yyh.okhttpretrofit.logic.transport.base.WeakRefResponseListener;
import com.yyh.okhttpretrofit.logic.transport.data.BaseResp;
import com.yyh.okhttpretrofit.logic.transport.data.ModelResp;

public class MainActivity extends AppCompatActivity implements Response.ResponseListener,
        View.OnClickListener {


    private ApiRequestManager mManager;
    private String mUserId = "123456789";//随便定义可不传
    private EditText mInputView;

    private ApiRequestManager getManager() {
        if (mManager == null) {
            mManager = new ApiRequestManager(getApplicationContext());
        }
        return mManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInputView = (EditText) findViewById(R.id.input_view);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
    }

    @Override
    public void onResponse(Response response) {
        Request request = response.getRequestInfo();
        switch (request.getRequestId()) {
            case CallbackIds.CREATE_TRADE: {
                ModelResp resp = (ModelResp) response.getData();
                if (resp == null || resp.getCode() != BaseResp.OK) {
                    UiAction.handleError(MainActivity.this, response, 0);
                    return;
                }
                break;
            }
            case CallbackIds.GET_VERIFICATION_CODE: {
                BaseResp resp = (BaseResp) response.getData();
                if (resp == null || resp.getCode() != BaseResp.OK) {
                    UiAction.handleError(MainActivity.this, response, 0);
                    return;
                }
                Toast.makeText(this, "验证码获取成功", Toast.LENGTH_SHORT).show();
                break;
            }
            case CallbackIds.SEND_MODIFY: {
                BaseResp resp = (BaseResp) response.getData();
                if (resp == null || resp.getCode() != BaseResp.OK) {
                    UiAction.handleError(MainActivity.this, response, 0);
                    return;
                }
                break;
            }
            case CallbackIds.SEND_SMS: {
                BaseResp resp = (BaseResp) response.getData();
                if (resp == null || resp.getCode() != BaseResp.OK) {
                    UiAction.handleError(MainActivity.this, response, 0);
                    return;
                }
                Log.e("============resp", resp.toString());
                break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt1) {
            ModelResp modelResp = new ModelResp("yyh", "1016", "yyhlove");
            getManager().createTrade(modelResp, mUserId, false, "",
                    new WeakRefResponseListener(this));
        } else if (id == R.id.bt2) {
            if (mInputView != null && !TextUtils.isEmpty(mInputView.getText().toString().trim())) {
                getManager().getVerificationCode(mUserId, mInputView.getText().toString().trim()
                        , new WeakRefResponseListener(this));
            } else {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.bt3) {
//            getManager().sendSMS(mUserId, new WeakRefResponseListener(this));
            getManager().createTrade("12345","test", new WeakRefResponseListener(this));

//            getManager().sendModify(mUserId, "789msg", "mcoc266", "908dsf", new WeakRefResponseListener(this));
        }
    }
}
