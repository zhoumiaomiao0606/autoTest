package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanFileDO {
    private Long id;

    private Long customerId;

    private String path;

    private Byte type;

    private Byte uploadType;

    private Date gmtCreate;

    private Date gmtModify;
}