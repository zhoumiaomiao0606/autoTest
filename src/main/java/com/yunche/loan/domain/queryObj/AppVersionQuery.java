package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
@Data
public class AppVersionQuery extends BaseQuery {

    private String versionCode;

    private String versionName;

    private Byte terminalType;

    private Byte updateType;

    private Byte isLatestVersion;
}
