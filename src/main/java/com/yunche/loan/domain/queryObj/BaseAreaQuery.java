package com.yunche.loan.domain.queryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class BaseAreaQuery extends BaseQuery {

    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private Byte level;
}
