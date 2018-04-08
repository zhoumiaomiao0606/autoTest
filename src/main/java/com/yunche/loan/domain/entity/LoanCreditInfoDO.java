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

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}