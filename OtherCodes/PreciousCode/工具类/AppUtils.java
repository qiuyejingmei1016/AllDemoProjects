package com.newrecord.cloud.utils;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

public class AppUtils {


    public static Application getApplication() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppForeground() {
        ActivityManager am = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName.equals(getApplication().getPackageName());
            }
        }
        return false;
    }


    public static String getAppPackageName() {
        Application application = getApplication();
        if(application == null){
            return "";
        }
        return application.getPackageName();
    }



}
