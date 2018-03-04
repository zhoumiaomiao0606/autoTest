package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBaseInfoDO {
    private Long id;

    private Long partnerId;

    private Long salesmanId;

    private Long areaId;

    private Byte carType;

    private String bank;

    private Byte loanAmount;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}