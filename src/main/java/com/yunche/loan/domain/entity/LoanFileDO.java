package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanFileDO {
    private Long id;

    private String name;

    private String path;

    private Byte type;

    private Long customerId;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}