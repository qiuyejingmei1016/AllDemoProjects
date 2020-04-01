package com.bairuitech.anychat.f2fvideo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.ApiManager;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;
import com.bairuitech.anychat.f2fvideo.utils.SharedPreferencesUtils;

/**
 * @describe: 设置界面
 * @author: yyh
 * @createTime: 2019/8/2 9:56
 * @className: SetActivity
 */
public class SetActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mInputAnyChatIpView;
    private EditText mInputAnyChatPortView;
    private EditText mInputJavaIpView;
    private EditText mInputJavaPortView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        ImageButton titleButton = (ImageButton) findViewById(R.id.title_left_img_btn);
        titleView.setText(R.string.save_title);
        titleButton.setImageResource(R.mipmap.ico_back);
        titleButton.setOnClickListener(this);

        this.findViewById(R.id.bt_save).setOnClickListener(this);
        this.findViewById(R.id.clear_anychat_ip).setOnClickListener(this);
        this.findViewById(R.id.clear_anychat_port).setOnClickListener(this);
        this.findViewById(R.id.clear_java_ip).setOnClickListener(this);
        this.findViewById(R.id.clear_java_port).setOnClickListener(this);
        this.mInputAnyChatIpView = (EditText) this.findViewById(R.id.input_anychat_ip);
        this.mInputAnyChatPortView = (EditText) this.findViewById(R.id.input_anychat_port);
        this.mInputJavaIpView = (EditText) this.findViewById(R.id.input_java_ip);
        this.mInputJavaPortView = (EditText) this.findViewById(R.id.input_java_port);
        readSaveData();
    }

    private void readSaveData() {
        String anyChatIp = SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_ANYCHAT_IP, ApiManager.ANYCHAT_LOGIN_IP);
        String anyChatPort = SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_ANYCHAT_PORT, ApiManager.ANYCHAT_LOGIN_PORT);
        String javaIp = SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_JAVA_IP, ApiManager.ANYCHAT_JAVA_IP);
        String javaPort = SharedPreferencesUtils.get(this,
                SharedPreferencesUtils.LOGIN_JAVA_PORT, ApiManager.ANYCHAT_JAVA_PORT);

        mInputAnyChatIpView.setText(EmptyUtils.getNotNullString(anyChatIp));
        mInputAnyChatPortView.setText(EmptyUtils.getNotNullString(anyChatPort));
        mInputJavaIpView.setText(EmptyUtils.getNotNullString(javaIp));
        mInputJavaPortView.setText(EmptyUtils.getNotNullString(javaPort));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.title_left_img_btn) {
            finish();
        } else if (viewId == R.id.bt_save) {
            if (EmptyUtils.isNullOrEmpty(mInputAnyChatIpView.getText().toString().trim())) {
                Toast.makeText(this, "请输入AnyChat服务器IP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (EmptyUtils.isNullOrEmpty(mInputAnyChatPortView.getText().toString().trim())) {
                Toast.makeText(this, "请输入AnyChat服务器端口", Toast.LENGTH_SHORT).show();
                return;
            }
            if (EmptyUtils.isNullOrEmpty(mInputJavaIpView.getText().toString().trim())) {
                Toast.makeText(this, "请输入Java服务器IP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (EmptyUtils.isNullOrEmpty(mInputJavaPortView.getText().toString().trim())) {
                Toast.makeText(this, "请输入Java服务器端口", Toast.LENGTH_SHORT).show();
                return;
            }
            saveLoginData();
            finish();
        } else if (viewId == R.id.clear_anychat_ip) {
            mInputAnyChatIpView.setText("");
        } else if (viewId == R.id.clear_anychat_port) {
            mInputAnyChatPortView.setText("");
        } else if (viewId == R.id.clear_java_ip) {
            mInputJavaIpView.setText("");
        } else if (viewId == R.id.clear_java_port) {
            mInputJavaPortView.setText("");
        }
    }

    /**
     * 保存设置信息
     */
    private void saveLoginData() {
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_ANYCHAT_IP,
                EmptyUtils.getNotNullString(mInputAnyChatIpView.getText().toString()));
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_ANYCHAT_PORT,
                EmptyUtils.getNotNullString(mInputAnyChatPortView.getText().toString()));
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_JAVA_IP,
                EmptyUtils.getNotNullString(mInputJavaIpView.getText().toString()));
        SharedPreferencesUtils.save(this, SharedPreferencesUtils.LOGIN_JAVA_PORT,
                EmptyUtils.getNotNullString(mInputJavaPortView.getText().toString()));
    }
}