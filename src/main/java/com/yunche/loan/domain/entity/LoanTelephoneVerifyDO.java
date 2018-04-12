package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanTelephoneVerifyDO {

    private Long orderId;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;
}