package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ConfVideoFaceArtificialDO {

    private Long bankId;

    private Byte needLocation;

    private Byte artificialVideoFaceStatus;

    private Date gmtCreate;

    private Date gmtModify;
}