/*
 * 文件名: MGAlertDialog.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-8-17
 */
package com.yyh.configtest.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yyh.configtest.R;
import com.yyh.configtest.utils.IAlertDialog;

/**
 * 弹框
 *
 * @author Kelvin Van
 */
public class MGAlertDialog extends Dialog implements IAlertDialog, View.OnClickListener, Handler.Callback {

    private static final int MSG_DISMISS_DIALOG = 0;
    private TextView mTitleView;
    private ImageView mTitleDividerView;
    private TextView mContentTextView;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private OnClickListener mPositiveBtnClickListener;
    private OnClickListener mNegativeBtnClickListener;
    private ViewStub mProgressLayoutStub;
    private ProgressBar mProgressBar;
    private TextView mProgressTextView;
    private TextView mProgressErrTextView;
    private Handler mHandler;
    private ImageView mBtnDividerView;
    private boolean mStyleHasSet;
    private int mRecommendButton = BUTTON_NEGATIVE;
    private LinearLayout mViewContainer;
    private OnDismissListener mMGDismissListener;
    private OnCancelListener mMGCancelListener;
    private boolean mDismissOnClickBtn = true;
    private View mContentView;

    public MGAlertDialog(Context context) {
        this(context, R.layout.mg_alert_dialog, null);
    }

    public MGAlertDialog(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    public MGAlertDialog(Context context, int layoutResId, ViewGroup.LayoutParams layoutParams) {
        super(context, R.style.dialog);
        setupViews(layoutResId, layoutParams);
        mHandler = new Handler(this);
    }

    @Override
    public void setRecommendButton(int button) {
        this.mRecommendButton = button;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case BUTTON_POSITIVE:
                return mPositiveBtn;
            case BUTTON_NEGATIVE:
            default:
                return mNegativeBtn;
        }
    }

    private void setupViews(int layoutResId, ViewGroup.LayoutParams layoutParams) {
        View contentView = LayoutInflater.from(getContext()).inflate(layoutResId, null, false);
        mTitleView = (TextView) contentView.findViewById(R.id.title);
        mTitleDividerView = (ImageView) contentView.findViewById(R.id.title_divider);
        mContentTextView = (TextView) contentView.findViewById(R.id.content);
        mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mBtnDividerView = (ImageView) contentView.findViewById(R.id.btn_divider);
        mPositiveBtn = (Button) contentView.findViewById(R.id.positive);
        if (mPositiveBtn != null) {
            mPositiveBtn.setOnClickListener(this);
        }
        mNegativeBtn = (Button) contentView.findViewById(R.id.negative);
        if (mNegativeBtn != null) {
            mNegativeBtn.setOnClickListener(this);
        }
        mProgressLayoutStub = (ViewStub) contentView.findViewById(R.id.progress_layout_stub);
        mViewContainer = (LinearLayout) contentView.findViewById(R.id.view);
        setContentView(contentView, layoutParams != null ? layoutParams : new ViewGroup.LayoutParams(getContext().getResources()
                .getDimensionPixelSize(R.dimen.alert_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentView = contentView;
    }

    public View findViewById(int viewId) {
        return mContentView.findViewById(viewId);
    }

    public void initProgressBar() {
        View progressLayout = mProgressLayoutStub.inflate();
        mProgressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_bar);
        mProgressTextView = (TextView) progressLayout.findViewById(R.id.progress_text);
        mProgressErrTextView = (TextView) progressLayout.findViewById(R.id.err_text);
    }

    public void setMax(int max) {
        mProgressBar.setMax(max);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void setProgressText(CharSequence text) {
        mProgressErrTextView.setVisibility(View.GONE);
        mProgressTextView.setVisibility(View.VISIBLE);
        mProgressTextView.setText(text);
    }

    public void setProgressErrText(CharSequence text) {
        mProgressTextView.setVisibility(View.GONE);
        mProgressErrTextView.setVisibility(View.VISIBLE);
        mProgressErrTextView.setText(text);
    }

    public ProgressBar getProgressBar() {
        return this.mProgressBar;
    }

    public TextView getProgressTextView() {
        return mProgressTextView;
    }

    public TextView getProgressErrTextView() {
        return mProgressErrTextView;
    }

    public View getProgressLayout() {
        return mProgressLayoutStub;
    }

    @Override
    public void setDismissOnClickBtn(boolean dismissOnClickBtn) {
        this.mDismissOnClickBtn = dismissOnClickBtn;
    }

    @Override
    public void setTitle(int textResId) {
        setTitle(getContext().getString(textResId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title != null && title.length() > 0) {
            mTitleView.setText(title);
            mTitleView.setVisibility(View.VISIBLE);
            mTitleDividerView.setVisibility(View.VISIBLE);
        } else {
            mTitleView.setVisibility(View.GONE);
            mTitleDividerView.setVisibility(View.GONE);
        }
    }


    @Override
    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            mContentTextView.setVisibility(View.VISIBLE);
            mContentTextView.setText(message);
        } else {
            mContentTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setMessage(int textResId) {
        setMessage(getContext().getString(textResId));
    }

    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        switch (whichButton) {
            case BUTTON_POSITIVE: {
                mPositiveBtn.setText(text);
                mPositiveBtn.setVisibility(View.VISIBLE);
                mPositiveBtnClickListener = listener;
                break;
            }
            case BUTTON_NEGATIVE: {
                mNegativeBtn.setText(text);
                mNegativeBtn.setVisibility(View.VISIBLE);
                mNegativeBtnClickListener = listener;
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.positive) {
            if (mPositiveBtnClickListener != null) {
                mHandler.sendEmptyMessage(BUTTON_POSITIVE);
            }
            if (mDismissOnClickBtn) {
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
            }
        } else if (viewId == R.id.negative) {
            if (mNegativeBtnClickListener != null) {
                mHandler.sendEmptyMessage(BUTTON_NEGATIVE);
            }
            if (mDismissOnClickBtn) {
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
            }
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case BUTTON_POSITIVE: {
                if (mPositiveBtnClickListener != null) {
                    mPositiveBtnClickListener.onClick(this, BUTTON_POSITIVE);
                }
                return true;
            }
            case BUTTON_NEGATIVE: {
                if (mNegativeBtnClickListener != null) {
                    mNegativeBtnClickListener.onClick(this, BUTTON_NEGATIVE);
                }
                return true;
            }
            case MSG_DISMISS_DIALOG: {
                dismiss();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mStyleHasSet) {
            return;
        }
        mStyleHasSet = true;

        if (mPositiveBtn == null || mNegativeBtn == null || mBtnDividerView == null) {
            return;
        }

        boolean positiveVisible = mPositiveBtn.getVisibility() == View.VISIBLE;
        boolean negativeVisible = mNegativeBtn.getVisibility() == View.VISIBLE;
        // 设置按钮背景
        if (positiveVisible && negativeVisible) {
            mBtnDividerView.setVisibility(View.VISIBLE);
            mPositiveBtn.setBackgroundResource(R.drawable.btn_mg_alert_dialog_right);
            mNegativeBtn.setBackgroundResource(R.drawable.btn_mg_alert_dialog_left);
        } else if (positiveVisible) {
            mPositiveBtn.setBackgroundResource(R.drawable.btn_mg_alert_dialog_single);
        } else if (negativeVisible) {
            mNegativeBtn.setBackgroundResource(R.drawable.btn_mg_alert_dialog_single);
        }
        // 设置按钮文字颜色
        if (positiveVisible && negativeVisible) {
            if (mRecommendButton == BUTTON_POSITIVE) {
                mPositiveBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_recommend_btn_text));
                mNegativeBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_normal_btn_text));
            } else {
                mPositiveBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_normal_btn_text));
                mNegativeBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_recommend_btn_text));
            }
        } else if (positiveVisible) {
            mPositiveBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_recommend_btn_text));
        } else {
            mNegativeBtn.setTextColor(getContext().getResources().getColorStateList(R.color.mg_alert_dialog_recommend_btn_text));
        }
    }

    @Override
    public void setView(View view) {
        mViewContainer.removeAllViews();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (!(lp instanceof LinearLayout.LayoutParams)) {
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mViewContainer.addView(view, lp);
    }

    @Override
    public void setMGDismissListener(OnDismissListener listener) {
        this.mMGDismissListener = listener;
    }

    @Override
    public void setMGCancelListener(OnCancelListener listener) {
        this.mMGCancelListener = listener;
    }

    @Override
    public void cancel() {
        if (mMGCancelListener != null) {
            mMGCancelListener.onCancel(this);
        }
        super.cancel();
    }

    @Override
    public void dismiss() {
        if (mMGDismissListener != null) {
            mMGDismissListener.onDismiss(this);
        }
        super.dismiss();
    }

    @Override
    public void setContentGravity(int gravity) {
        mContentTextView.setGravity(gravity);
    }

    @Override
    public void setButtonsInvisible() {
        View view = mContentView.findViewById(R.id.btn_layout_divider);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }
}