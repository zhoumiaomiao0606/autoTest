package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JtxReturnFileDO {
    private String jtxid;

    private String filePath;

    private String fileName;

    private Date createDate;


}