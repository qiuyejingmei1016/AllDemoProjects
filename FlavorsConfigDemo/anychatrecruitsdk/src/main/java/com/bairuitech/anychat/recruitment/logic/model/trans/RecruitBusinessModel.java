package com.bairuitech.anychat.recruitment.logic.model.trans;

/**
 * @describe: 远程招聘业务信息传参模型实体类
 * @author: yyh
 * @createTime: 2020/2/22 13:42
 * @className: RecruitBusinessModel
 */
public class RecruitBusinessModel {

    private String agentOrgName;//面试官职位信息

    private String address;//位置信息

    private RecruitCompanyModel recruitCompanyModel;

    public String getAgentOrgName() {
        return agentOrgName;
    }

    public void setAgentOrgName(String agentOrgName) {
        this.agentOrgName = agentOrgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RecruitCompanyModel getRecruitCompanyModel() {
        return recruitCompanyModel;
    }

    public void setRecruitCompanyModel(RecruitCompanyModel recruitCompanyModel) {
        this.recruitCompanyModel = recruitCompanyModel;
    }
}