package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankRelaQuestionDO {
    private Long id;

    private Long bankId;

    private String question;

    private Byte require;

    private Byte type;

    private Date gmtCreate;

    private Date gmtModify;
}