package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ConfVideoFaceBankPartnerDO {

    private Long bankId;

    private Long partnerId;

    private Date gmtCreate;

    private Date gmtModify;
}