package com.bairuitech.anychat.f2fvideo.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;

/**
 * @describe: 加载loading对话框
 * @author: yyh
 * @createTime: 2020/2/18 10:02
 * @className: CustomLoadingDialog
 */
public class CustomLoadingDialog {

    private static volatile CustomLoadingDialog mInstance = null;

    private Dialog mDialog;

    public static CustomLoadingDialog getInstance() {
        if (null == mInstance) {
            synchronized (CustomLoadingDialog.class) {
                if (mInstance == null) {
                    mInstance = new CustomLoadingDialog();
                }
            }
        }
        return mInstance;
    }

    public void showLoadingDialog(Context context, String msg) {
        showLoadingDialog(context, msg, true);
    }

    public void showLoadingDialog(Context context, String msg, boolean isCancel) {
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.loading_round_dialog_style);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(isCancel);
        }
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
        View view = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.view_loading_round_dialog, null);
            TextView tipTextView = (TextView) view.findViewById(R.id.loading_tip_view);
            if (!EmptyUtils.isNullOrEmpty(msg)) {
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