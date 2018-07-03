package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankFileListDO {
    private Long id;

    private String fileType;

    private String fileName;

    private String operator;

    private Byte status;

    private String fileKey;

    private Date gmtCreate;


}