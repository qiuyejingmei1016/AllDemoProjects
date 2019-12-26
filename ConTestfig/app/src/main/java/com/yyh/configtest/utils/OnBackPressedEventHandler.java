package com.yyh.configtest.utils;

/**
 * 返回按键事件处理
 *
 * @author Kelvin Van
 */
public interface OnBackPressedEventHandler {

    /**
     * @return true表示已经处理了此事件, false表示没有处理
     */
    boolean handleBackPressedEvent();
}
