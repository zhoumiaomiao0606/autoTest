package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
@Data
public class AppVersionVO {

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

    private Byte status;

    private String info;

    @Data
    public static class Update {
        /**
         * 是否需要升级
         */
        private Boolean needUpdate;
        /**
         *
         */
        private AppVersionVO latestVersion;
    }
}
