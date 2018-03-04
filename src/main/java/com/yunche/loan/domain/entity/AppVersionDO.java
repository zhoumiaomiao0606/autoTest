package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AppVersionDO {
    private Long id;

    private String versionCode;

    private String versionName;

    private String downloadUrl;

    private Byte terminalType;

    private Byte updateType;

    private Date releaseDate;

    private Byte isLatestVersion;

    private Byte storeType;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;

    private String info;
}