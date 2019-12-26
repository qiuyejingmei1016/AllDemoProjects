/*
 * 文件名: Config.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-15
 */
package com.yyh.buildconfigdemo.utils;


import com.yyh.buildconfigdemo.BuildConfig;

/**
 * 全局配置
 *
 * @author Kelvin Van
 */
public class Config {

    /**
     * 是否输出调试日志
     */
    public static final boolean LOG_DEBUG = BuildConfig.LOG_DEBUG;
    /**
     * 用户数据存放文件夹名称
     */
    public static final String USER_DATA_DIR = BuildConfig.USER_DATA_DIR;


    public static final String USER_LOG_DIR = BuildConfig.USER_LOG_DIR;
    public static final String LOG_FILENAME = "log.txt";

}