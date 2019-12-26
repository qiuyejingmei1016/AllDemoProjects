/*
 * 文件名: MGDialogLayout.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-8-15
 */
package com.yyh.configtest.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Dialog布局
 *
 * @author Kelvin Van
 */
public class MGDialogLayout extends LinearLayout {

    private int mMaxHeight;

    public MGDialogLayout(Context context) {
        super(context);
    }

    public MGDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MGDialogLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MGDialogLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void init() {
        this.mMaxHeight = getContext().getResources().getDisplayMetrics().heightPixels * 7 / 10;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() > mMaxHeight) {
            setMeasuredDimension(getMeasuredWidth(), mMaxHeight);
        }
    }*/
}