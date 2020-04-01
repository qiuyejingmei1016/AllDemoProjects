package com.bairuitech.anychat.recruitment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bairuitech.anychat.recruitment.R;

/**
 * @describe: 面试完成展示对话框管理类
 * @author: yyh
 * @createTime: 2020/2/10 10:19
 * @className: RecruitCompleteDialog
 */
public class RecruitCompleteDialog implements View.OnClickListener {

    private static volatile RecruitCompleteDialog mInstance = null; // 单例模式对象

    private Dialog mDialog;
    private RecruitCompleteListener mCompleteListener;

    // 获取单例模式对象
    public static RecruitCompleteDialog getInstance() {
        if (null == mInstance) {
            synchronized (RecruitCompleteDialog.class) {
                if (mInstance == null) {
                    mInstance = new RecruitCompleteDialog();
                }
            }
        }
        return mInstance;
    }


    public void showDialog(Context context, RecruitCompleteListener listener) {
        this.mCompleteListener = listener;
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.sdk_common_dialog_style);
        }
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        View view = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.sdk_view_recruit_complete_dialog, null);
            TextView singleConfirmView = (TextView) view.findViewById(R.id.sdk_btn_single_confirm);
            singleConfirmView.setOnClickListener(this);
            mDialog.setContentView(view);
        }
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sdk_btn_single_confirm) {
            mCompleteListener.OnCompleteListener();
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    /**
     * 弹窗销毁释放
     */
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

    public interface RecruitCompleteListener {

        void OnCompleteListener();
    }
}