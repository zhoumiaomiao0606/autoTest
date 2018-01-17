package com.yunche.loan.domain.dataObj;

import lombok.Data;

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

    private Byte level;
}