package com.bairuitech.anychat.recruitment.logic.interf;

import com.bairuitech.anychat.main.AnyChatResult;

/**
 * @describe: 组件回调接口
 * @author: yyh
 * @createTime: 2019/4/30 15:25
 * @className: RecruitRecordEvent
 */
public interface RecruitRecordEvent {

    /**
     * 登录完成回调
     *
     * @param userId AnyChat登录成功返回的用户id
     */
    void onLoginSuccess(int userId);

    /**
     * 异常错误回调
     *
     * @param result result.code: 错误码 result.msg: 错误描述
     */
    void onRecruitError(AnyChatResult result);

    /**
     * 完成回调
     *
     * @param result result.code: 错误码 result.msg: 错误描述
     */
    void onRecruitCompleted(AnyChatResult result);
}