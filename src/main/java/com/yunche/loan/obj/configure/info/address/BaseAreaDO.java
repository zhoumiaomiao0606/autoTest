package com.yunche.loan.obj.configure.info.address;

import lombok.Data;

/**
 * 地址
 */
@Data
public class BaseAreaDO {
    private Integer id;

    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private Byte level;
}