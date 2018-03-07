package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanCreditInfoDO {

    private Long id;

    private Byte result;

    private String info;

    private Byte type;

    private Long customerId;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}