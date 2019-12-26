/*
 * 文件名: MGBaseFragment.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-2
 */
package com.yyh.configtest.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yyh.configtest.utils.PermissionUtil;

import java.util.List;

/**
 * 基础Fragment
 *
 * @author Kelvin Van
 */
public abstract class BaseFragment extends Fragment implements PermissionUtil.PermissonCallBack {

    public static final String EXTRA_IN_DUAL_PANEL = "in_dual_panel";

    private boolean mInDualPanel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(EXTRA_IN_DUAL_PANEL)) {
            mInDualPanel = args.getBoolean(EXTRA_IN_DUAL_PANEL);
        }
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = onCreateViewInner(inflater, container, savedInstanceState);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isInDualPanel()) {
            view.setClickable(true);
        }
    }

    protected View onCreateViewInner(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public final void setInDualPanel(boolean inDualPanel) {
        this.mInDualPanel = inDualPanel;
    }

    public final boolean isInDualPanel() {
        return mInDualPanel;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 结束当前的fragment, 在startActivity之前调用
     */
    private /*final*/ void finishFragment() {
        getFragmentManager().popBackStack();
    }

    /**
     * 结束当前的fragment或者activity
     */
    public final void finishFragmentOrActivity() {
        if (isInDualPanel()) {
            finishFragment();
        } else {
            getActivity().finish();
        }
    }


    /**
     * 返回成功结果并结束, 支持Fragment和Activity
     *
     * @param data 结果
     */
    protected void okAndFinish(Intent data) {
        Fragment fragment = getTargetFragment();
        if (isInDualPanel()) {
            getFragmentManager().popBackStack();
            if (fragment != null) {
                fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
            }
        } else {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }

    public final void startActivityAndFinishSelf(Intent intent) {
        if (isInDualPanel()) {
            finishFragmentOrActivity();
            startActivity(intent);
        } else {
            startActivity(intent);
            finishFragmentOrActivity();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.requestCallBack(requestCode, permissions, grantResults, PermissionUtil.getInstance());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //子类实现
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionUtil.getInstance().showGoSettingDialogs(getActivity(), perms);
    }
}