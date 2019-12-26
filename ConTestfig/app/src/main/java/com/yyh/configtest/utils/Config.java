/*
 * 文件名: Config.java
 * 版 权： Copyright Co. Ltd. All Rights Reserved.
 * 创建时间: 2015-6-15
 */
package com.yyh.configtest.utils;


import com.yyh.configtest.BuildConfig;

/**
 * 全局配置
 *
 * @author Kelvin Van
 */
public class Config {

    /**
     * 是否正式版本
     */
    public static final boolean OFFICAL_VERSION = BuildConfig.OFFICAL_VERSION;
    /**
     * 是否输出调试日志
     */
    public static final boolean LOG_DEBUG = BuildConfig.LOG_DEBUG;
    /**
     * 用户数据存放文件夹名称
     */
    public static final String USER_DATA_DIR = BuildConfig.USER_DATA_DIR;


    /**
     * 是否开放版本
     */
    public static final boolean OPEN_VERSION = false;
    /**
     * 开放版本用户id
     */
    public static final String OPEN_VERSION_USER_ID = "-100";
}