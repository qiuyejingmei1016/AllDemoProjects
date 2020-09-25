package example.com.showdialog.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import example.com.showdialog.R;
import example.com.showdialog.utils.StringUtil;

/**
 * @describe: 通用对话框管理类
 * @author: yyh
 * @createTime: 2018/8/7 9:46
 * @className: DialogUtils
 */
public class CommonDialog implements View.OnClickListener {

    private static volatile CommonDialog mInstance = null; // 单例模式对象

    public static final int DIALOG_STYLE_DEFAULT = 1;
    public static final int DIALOG_CALLING = 3;//正在呼叫
    public static final int DIALOG_CONFIRM = 4;//确定(底部一个按钮)

    private Dialog mDialog;
    private ConfirmListener mConfirmListener;

    // 获取单例模式对象
    public static CommonDialog getInstance() {
        if (null == mInstance) {
            synchronized (CommonDialog.class) {
                if (mInstance == null) {
                    mInstance = new CommonDialog();
                }
            }
        }
        return mInstance;
    }

    //默认
    public void showDialog(Context context, String title, String content, ConfirmListener listener) {
        showDialog(context, DIALOG_STYLE_DEFAULT, 0, title, content, "", "", "", listener);
    }

    public void showDialog(Context context, String title, String content, String leftBtnText, String rightBtnText, ConfirmListener listener) {
        showDialog(context, DIALOG_STYLE_DEFAULT, 0, title, content, leftBtnText, rightBtnText, "", listener);
    }

    //确定(底部一个按钮)
    public void showConfirmDialog(Context context, String title, String confirmText, ConfirmListener listener) {
        showDialog(context, DIALOG_CONFIRM, 0, title, "", "", "", confirmText, listener);
    }

    //正在呼叫
    public void showCallDialog(Context context, String title, String confirmText, ConfirmListener listener) {
        showDialog(context, DIALOG_CALLING, 0, title, "", "", "", confirmText, listener);
    }

    public void showDialog(Context context, int showStyle, int resId, String title, String content,
                           String leftBtnText, String rightBtnText, String confirmText,
                           ConfirmListener okListener) {
        this.mConfirmListener = okListener;
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.aiselfrecord_commonDialog);
        } else if (mDialog.isShowing()) {
            mDialog.cancel();
        }
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        View view = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.aiselfrecord_view_common_dialog, null);
            //标题
            TextView titleView = (TextView) view.findViewById(R.id.title_view);
            //内容
            TextView contentView = (TextView) view.findViewById(R.id.content_view);
            //顶部确定取消
            View bottomDoubleView = view.findViewById(R.id.level_view);
            View bottomOneView = view.findViewById(R.id.bottom_view);

            TextView okView = (TextView) view.findViewById(R.id.btn_ok);
            TextView cancelView = (TextView) view.findViewById(R.id.btn_cancel);
            TextView OneConfirmView = (TextView) view.findViewById(R.id.btn_confirm);

            if (showStyle == DIALOG_CALLING) {
                bottomDoubleView.setVisibility(View.GONE);
                bottomOneView.setVisibility(View.VISIBLE);
            } else if (showStyle == DIALOG_CONFIRM) {
                bottomDoubleView.setVisibility(View.GONE);
                bottomOneView.setVisibility(View.VISIBLE);
            }

            okView.setOnClickListener(this);
            cancelView.setOnClickListener(this);
            OneConfirmView.setOnClickListener(this);

            //标题填充
            if (StringUtil.isNullOrEmpty(title)) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(StringUtil.getNotNullString(title));
            }
            //内容填充
            if (StringUtil.isNullOrEmpty(content)) {
                contentView.setVisibility(View.GONE);
            } else {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(StringUtil.getNotNullString(content));
            }

            if (!StringUtil.isNullOrEmpty(leftBtnText)) {
                okView.setText(StringUtil.getNotNullString(leftBtnText));
            }
            if (!StringUtil.isNullOrEmpty(rightBtnText)) {
                cancelView.setText(StringUtil.getNotNullString(rightBtnText));
            }
            if (!StringUtil.isNullOrEmpty(confirmText)) {
                OneConfirmView.setText(confirmText);
            }
            mDialog.setContentView(view);
        }
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_ok || viewId == R.id.btn_confirm) {
            mConfirmListener.OnConfirmListener();
        } else if (viewId == R.id.btn_cancel) {
            mConfirmListener.OnCancelListener();
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    public void destroy() {
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