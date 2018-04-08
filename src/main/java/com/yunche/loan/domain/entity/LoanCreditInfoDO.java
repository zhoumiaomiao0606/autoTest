package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCreditInfoDO {

    private Long id;

    private Long customerId;

    private Byte result;

    private String info;

    private Byte type;
    /**
     * 征信审核结果
     */
    private Byte approveResult;
    /**
     * 征信审核结果备注
     */
    private String approveInfo;
    /**
     * 附加条件
     */
    private String addCondition;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}