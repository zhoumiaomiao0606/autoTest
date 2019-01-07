package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ConfVideoFaceBankDO {

    private Long bankId;

    private Byte needLocation;

    private Byte artificialVideoFace;

    private Date gmtCreate;

    private Date gmtModify;
}