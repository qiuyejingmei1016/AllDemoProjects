package com.yyh.okhttpretrofit;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.yyh.okhttpretrofit.logic.transport.base.HttpErrorCode;
import com.yyh.okhttpretrofit.logic.transport.base.Response;
import com.yyh.okhttpretrofit.logic.transport.data.BaseResp;

/**
 * Created by Administrator on 2018/5/18.
 */

public class UiAction {

    /**
     * 网络请求完成提示信息
     *
     * @param context
     * @param response
     * @param otherErrMsgResId
     */
    public static void handleError(Context context, Response response, int otherErrMsgResId) {
        CharSequence text;
        Object responseData = response.getData();
        if (responseData != null && responseData instanceof BaseResp) {
            BaseResp parsed = (BaseResp) responseData;
            text = parsed.getMessage();
        } else {
            text = response.getMessage();
        }
        if (text == null) {
            int code = response.getCode();
            if (code == HttpErrorCode.NO_NETWORK) {
                text = context.getString(R.string.no_network);
            } else if (code == HttpErrorCode.ACTION_FAILED) {
                text = context.getString(R.string.action_failed);
            } else if (code == HttpErrorCode.NETWORK_BROKEN) {
                text = context.getString(R.string.network_broken);
            } else if (code == HttpErrorCode.NETWORK_EXCEPTION) {
                text = context.getString(R.string.network_exception);
            } else if (code == HttpErrorCode.NETWORK_TIMEOUT) {
                text = context.getString(R.string.network_timeout);
            }
        }
        if (text == null && otherErrMsgResId != 0) {
            text = context.getString(otherErrMsgResId);
        }
        if (text != null && !TextUtils.isEmpty(text.toString())) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }

}
