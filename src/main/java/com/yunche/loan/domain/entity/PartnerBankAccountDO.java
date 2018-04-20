package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PartnerBankAccountDO {
    private Long id;

    private Long partnerId;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Date gmtCreate;

    private Date gmtModify;
}