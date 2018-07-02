package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialDownHisDO {
    private String serialNo;

    private String fileType;

    private String fileName;

    private Byte status;

    private Date gmtCreate;

    private String info;

    private String fileKey;
}