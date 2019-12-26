package com.yyh.configtest.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
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
import com.yyh.configtest.utils.IAlertDialog;
import com.yyh.configtest.utils.OnBackPressedEventHandler;
import com.yyh.configtest.utils.PermissionUtil;
import com.yyh.configtest.utils.UIAction;

import java.util.List;

public class FragmentTest2 extends BaseFragment implements OnBackPressedEventHandler {

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
        PermissionUtil.getInstance().needToFinsh(false).request(this, Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.text_view)).setText(this.getClass().getName());
        mInputView = (EditText) view.findViewById(R.id.input_view);
        Button button = (Button) view.findViewById(R.id.test);
        if (!TextUtils.isEmpty(mText)) {
            button.setText("传递的数据:  " + mText);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mInputView.getText().toString())) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("test_data", mInputView.getText().toString());
                okAndFinish(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
}
