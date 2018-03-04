package com.yunche.loan.domain.query;

import lombok.Data;

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
