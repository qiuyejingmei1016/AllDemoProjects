package com.newrecord.cloud.utils;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

/**
 * SDK 日志打印到服务器
 */
public class SDKLogUtils {
    public static void log(String msg){
        AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, msg);
    }
}
