package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 地址
 */
@Data
public class BaseAreaDO {
    /**
     * 区域ID
     * 也是主键ID
     */
    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private String parentAreaName;
    /**
     * 0:全国; 1:省; 2:市
     */
    private Byte level;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}