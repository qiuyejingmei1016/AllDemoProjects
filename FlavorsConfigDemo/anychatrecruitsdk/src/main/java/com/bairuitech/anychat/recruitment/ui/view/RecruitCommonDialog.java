package com.bairuitech.anychat.recruitment.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.utils.StringUtil;

/**
 * @describe: 通用对话框管理类
 * @author: yyh
 * @createTime: 2018/8/7 9:46
 * @className: DialogUtils
 */
public class RecruitCommonDialog implements View.OnClickListener {

    private static volatile RecruitCommonDialog mInstance = null; // 单例模式对象

    public static final int DIALOG_STYLE_DEFAULT = 1;//默认样式(两按钮并列)
    public static final int DIALOG_STYLE_SINGLE = 2;//确定(单按钮)

    private Dialog mDialog;
    private ConfirmListener mConfirmListener;

    // 获取单例模式对象
    public static RecruitCommonDialog getInstance() {
        if (null == mInstance) {
            synchronized (RecruitCommonDialog.class) {
                if (mInstance == null) {
                    mInstance = new RecruitCommonDialog();
                }
            }
        }
        return mInstance;
    }

    /**
     * 默认样式(两按钮并列)
     *
     * @param context  上下文
     * @param title    标题
     * @param listener
     */
    public void showDialog(Context context, String title, ConfirmListener listener) {
        showDialog(context, DIALOG_STYLE_DEFAULT, title, "", "", "", listener);
    }

    /**
     * 确定(单按钮)
     *
     * @param context  上下文
     * @param title    标题内容
     * @param listener
     */
    public void showSingleDialog(Context context, String title, String confirmText, ConfirmListener listener) {
        showDialog(context, DIALOG_STYLE_SINGLE, title, "", "", confirmText, listener);
    }

    public void showDialog(Context context, int showStyle, String title, String leftBtnText,
                           String rightBtnText, String confirmText, ConfirmListener listener) {
        this.mConfirmListener = listener;
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
            view = LayoutInflater.from(context).inflate(R.layout.sdk_recruit_view_common_dialog, null);
            //标题
            TextView titleView = (TextView) view.findViewById(R.id.sdk_title_view);
            //并列按钮父布局
            View bottomDoubleView = view.findViewById(R.id.sdk_level_view);
            //单独按钮父布局
            View bottomSingleView = view.findViewById(R.id.sdk_bottom_view);
            if (showStyle == DIALOG_STYLE_SINGLE) {
                bottomDoubleView.setVisibility(View.GONE);
                bottomSingleView.setVisibility(View.VISIBLE);
            }
            TextView confirmView = (TextView) view.findViewById(R.id.sdk_btn_ok);
            TextView cancelView = (TextView) view.findViewById(R.id.sdk_btn_cancel);
            TextView singleConfirmView = (TextView) view.findViewById(R.id.sdk_btn_single_confirm);
            confirmView.setOnClickListener(this);
            cancelView.setOnClickListener(this);
            singleConfirmView.setOnClickListener(this);

            //标题填充
            if (StringUtil.isNullOrEmpty(title)) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(StringUtil.getNotNullString(title));
            }

            if (!StringUtil.isNullOrEmpty(leftBtnText)) {
                confirmView.setText(StringUtil.getNotNullString(leftBtnText));
            }
            if (!StringUtil.isNullOrEmpty(rightBtnText)) {
                cancelView.setText(StringUtil.getNotNullString(rightBtnText));
            }
            if (!StringUtil.isNullOrEmpty(confirmText)) {
                singleConfirmView.setText(confirmText);
            }
            mDialog.setContentView(view);
        }
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sdk_btn_ok || viewId == R.id.sdk_btn_single_confirm) {
            mConfirmListener.OnConfirmListener();
        } else if (viewId == R.id.sdk_btn_cancel) {
            mConfirmListener.OnCancelListener();
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

    public interface ConfirmListener {

        void OnConfirmListener();

        void OnCancelListener();
    }
}