package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankInterfaceFileSerialDO {
    private String id;

    private String serialNo;

    private String fileName;

    private String filePath;

    private String fileType;

    private Byte error;

    private Byte success;

    private Date requestTime;


}