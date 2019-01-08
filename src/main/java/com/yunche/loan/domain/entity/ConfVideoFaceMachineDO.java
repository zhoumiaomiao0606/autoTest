package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ConfVideoFaceMachineDO {

    private Long bankId;

    private Long partnerId;

    private Date gmtCreate;

    private Date gmtModify;
}