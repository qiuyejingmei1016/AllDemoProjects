package com.bairuitech.anychat.recruitment.logic.model;

/**
 * @describe: 获取服务队列（即进线业务规则接口）实体类
 * @author: AnyChat
 * @createTime: 2019/3/9 16:55
 * @className: RecruitQueryRouteModel
 */
public class RecruitQueryRouteModel extends RecruitBaseModel {

    public static class ParamsEntity extends RecruitBaseModel.ParamsEntity {

        private String routeKey;//请求路由参数

        public void setRouteKey(String routeKey) {
            this.routeKey = routeKey;
        }
    }
}