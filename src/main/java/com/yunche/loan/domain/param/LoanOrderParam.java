package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class LoanOrderParam {

    private Long id;

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
