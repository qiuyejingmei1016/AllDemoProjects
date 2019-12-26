package com.yyh.configtest.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yyh.configtest.R;
import com.yyh.configtest.ui.base.BaseFragment;
import com.yyh.configtest.ui.base.NormalActivity;
import com.yyh.configtest.utils.IAlertDialog;
import com.yyh.configtest.utils.OnBackPressedEventHandler;
import com.yyh.configtest.utils.PermissionUtil;
import com.yyh.configtest.utils.UIAction;

import java.util.List;

public class TestFragment extends BaseFragment implements OnBackPressedEventHandler {

    private static final int REQUEST_GET_DATA_CODE = 1;
    private String mText;
    private IAlertDialog mCancelAlertDlg;
    private EditText mInputView;

    @Nullable
    @Override
    protected View onCreateViewInner(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_view, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args.containsKey("string")) {
            mText = args.getString("string");
        }

        PermissionUtil.getInstance().needToFinsh(false).request(this, new String[]{
                Manifest.permission.RECORD_AUDIO});

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.text_view)).setText(this.getClass().getName());
        mInputView = (EditText) view.findViewById(R.id.input_view);
        Button button = (Button) view.findViewById(R.id.test);
        if (!TextUtils.isEmpty(mText)) {
            button.setText(mText);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NormalActivity.actionTest2Fragment(getActivity(), "传值 f --> f");
                startActivityForResult(intent, REQUEST_GET_DATA_CODE);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        // TODO: 2018/5/10
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Intent intent = (Intent) msg.obj;
                if (intent != null) {
                    okAndFinish(intent);
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isAdded()) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_GET_DATA_CODE:
                String test_data = data.getStringExtra("test_data");
                if (!TextUtils.isEmpty(test_data)) {
                    mInputView.setText("这是接收上个页面的数据(5s后关闭):  " + test_data);
                }
                Message message = new Message();
                message.what = 1;
                message.obj = data;
                mHandler.sendMessageDelayed(message, 5000);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        UIAction.makeToast(getActivity(), "获取权限成功,可以做操作了", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean handleBackPressedEvent() {
        if (hasInput()) {
            mCancelAlertDlg = UIAction.newCancelEditAlertDialogToFinishFragment(getActivity(), this);
            mCancelAlertDlg.show();
            return true;
        }
        return false;
    }

    private boolean hasInput() {
        if (!TextUtils.isEmpty(mInputView.getText().toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCancelAlertDlg != null) {
            if (mCancelAlertDlg.isShowing()) {
                mCancelAlertDlg.dismiss();
            }
            mCancelAlertDlg = null;
        }
    }
}
