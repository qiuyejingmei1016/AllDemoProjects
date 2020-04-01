package com.bairuitech.anychat.recruitment.logic.model.trans;

/**
 * @describe: 企业信息传参模型实体类
 * @author: yyh
 * @createTime: 2020/2/21 10:40
 * @className: RecruitCompanyModel
 */
public class RecruitCompanyModel {

    private String fullUrl;//企业播放视频文件地址
    private String companyLogo;//企业logo
    private String companyIntroduction;//企业介绍

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyIntroduction() {
        return companyIntroduction;
    }

    public void setCompanyIntroduction(String companyIntroduction) {
        this.companyIntroduction = companyIntroduction;
    }
}