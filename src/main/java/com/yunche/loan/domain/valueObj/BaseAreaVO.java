package com.yunche.loan.domain.valueObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class BaseAreaVO {

    private Integer id;

    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private Byte level;

}
