package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessOrderDO {

    private String id;

    private String processInstId;
    /**
     * 关联的-贷款贷款基本信息
     */
    private Long loanBaseInfoId;
    /**
     * 关联的-贷款客户信息(主贷人/共贷人/担保人/紧急联系人)
     */
    private Long loanCustomerId;
    /**
     * 贷款车辆信息
     */
    private Long loanCarInfoId;
    /**
     * 贷款金融方案
     */
    private Long loanFinancialPlanId;
    /**
     * 上门家访资料
     */
    private Long loanHomeVisitId;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}