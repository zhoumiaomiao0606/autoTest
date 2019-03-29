package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class LoanapplyMutiparam {

    private Long orderId;
    /**
     * 主贷人
     */
    private CustomerParam principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerParam> commonLenderList;
    /**
     * 担保人列表
     */
    private List<CustomerParam> guarantorList;
    /**
     * 紧急联系人列表
     */
    private List<CustomerParam> emergencyContactList;

    /**
     * 车辆详情
     */
    LoanCarInfoParam loanCarInfoParam;


    /**
     * 金融方案
     */
    LoanFinancialPlanParam loanFinancialPlanParam;
}
