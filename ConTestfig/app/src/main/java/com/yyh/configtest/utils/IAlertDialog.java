/*
 * 文件名: IAlertDialog.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-8-17
 */
package com.yyh.configtest.utils;

import android.content.DialogInterface;
import android.view.View;

/**
 * 弹框接口
 *
 * @author Kelvin Van
 */
public interface IAlertDialog extends DialogInterface {

    void setTitle(int textResId);

    void setTitle(CharSequence title);

    void setMessage(CharSequence message);

    void setMessage(int textResId);

    void setButton(int whichButton, CharSequence text, OnClickListener listener);

    void show();

    boolean isShowing();

    void setCanceledOnTouchOutside(boolean cancel);

    void setOnCancelListener(OnCancelListener listener);

    void setOnDismissListener(OnDismissListener listener);

    void setRecommendButton(int button);

    void setView(View view);

    void setMGDismissListener(OnDismissListener listener);

    void setMGCancelListener(OnCancelListener listener);

    void setContentGravity(int gravity);

    void setDismissOnClickBtn(boolean dismissOnClickBtn);

    void setButtonsInvisible();
}