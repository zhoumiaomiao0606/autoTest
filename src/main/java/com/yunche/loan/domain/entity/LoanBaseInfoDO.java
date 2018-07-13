package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBaseInfoDO {
    private Long id;

    private Long partnerId;

    private Long salesmanId;

    private Long areaId;

    private Byte carType;

    /**
     * 银行名称
     */
    private String bank;
    /**
     * 银行ID
     */
    private Long bankId;

    /**
     * 贷款额度档次：1 - 13W以下; 2 - 13至20W; 3 - 20W以上;
     */
    private Byte loanAmount;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}