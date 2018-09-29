package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCreditInfoDO {

    private Long id;

    private Long customerId;

    /**
     * 征信结果: 0-不通过;1-通过;2-关注;
     */
    private Byte result;

    private String info;

    private Byte type;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}