package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankCreditInfoDO {
    private String serialNo;

    private Long customerId;

    private String custname;

    private String idno;

    private String relation;

    private String result;

    private String loancrdt;

    private String cardcrdt;

    private String leftnum;

    private String leftamount;

    private String note;

    private Date createTime;
}