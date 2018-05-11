package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankRepayImpRecordDO {
    private Long id;

    private String bankFileMark;

    private Date gmtCreate;

    private String operator;

    private Byte status;


}