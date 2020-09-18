package com.bairuitech.anychat.testanychatsigndialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class CustomSwitchView extends RelativeLayout {

    private Context mContext;

    private View mOpenView;
    private View mCloseView;

    public CustomSwitchView(Context context) {
        this(context, null, 0);
    }

    public CustomSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context, attrs);
    }

    //初始化
    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.custom_switch_view, this);
        mOpenView = findViewById(R.id.open);
        mCloseView = findViewById(R.id.close);
    }

    public void setOpen() {
        mCloseView.setVisibility(GONE);
        mOpenView.setVisibility(VISIBLE);
    }

    public void setClose() {
        mOpenView.setVisibility(GONE);
        mCloseView.setVisibility(VISIBLE);
    }
}