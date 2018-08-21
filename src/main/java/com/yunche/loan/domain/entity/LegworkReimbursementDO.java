package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LegworkReimbursementDO {
    private Long id;

    private String transFee;

    private String hotelFee;

    private String eatFee;

    private String busiFee;

    private String otherFee;

    private Long gmtUser;

    private String gmtUserName;

    private Date gmtCreateTime;

    private Date gmtUpdateTime;

    private Byte status;

    private String reimbursementAmount;

    private String bank;

    private String remitAccount;

    private String collectionBank;

    private String collectionAccount;

    private String collectionAccountNumber;

    private Date busiTime;


}