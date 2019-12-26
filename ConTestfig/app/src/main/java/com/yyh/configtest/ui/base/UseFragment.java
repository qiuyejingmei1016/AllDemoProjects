package com.yyh.configtest.ui.base;

import android.support.v4.app.Fragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用单个Fragment
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseFragment {

    /**
     * @return Fragment类
     */
    Class<? extends Fragment> value();

    /**
     * @return 是否支持Pad, 默认true
     */
    boolean pad() default true;
}