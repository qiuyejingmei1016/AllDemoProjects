package com.bairuitech.anychat.recruitment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.utils.StringUtil;

/**
 * @describe: 加载loading对话框
 * @author: yyh
 * @createTime: 2018/6/6 10:59
 * @className: RecruitLoadingDialog
 */
public class RecruitLoadingDialog {

    private static volatile RecruitLoadingDialog mInstance = null;

    private Dialog mDialog;

    public static RecruitLoadingDialog getInstance() {
        if (null == mInstance) {
            synchronized (RecruitLoadingDialog.class) {
                if (mInstance == null) {
                    mInstance = new RecruitLoadingDialog();
                }
            }
        }
        return mInstance;
    }

    public void showDialog(Context context, String msg) {
        showDialog(context, msg, true);
    }

    public void showDialog(Context context, String msg, boolean isCancel) {
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.sdk_round_dialog_style);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(isCancel);
        }
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
        View view = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.sdk_recruit_view_round_dialog, null);
            TextView tipTextView = (TextView) view.findViewById(R.id.sdk_recruit_loading_tip_view);
            if (!StringUtil.isNullOrEmpty(msg)) {
                tipTextView.setText(msg);
            } else {
                tipTextView.setVisibility(View.GONE);
            }
            mDialog.setContentView(view);
        }
        mDialog.show();
    }

    public void destory() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
        }
        if (mInstance != null) {
            mInstance = null;
        }
    }
}