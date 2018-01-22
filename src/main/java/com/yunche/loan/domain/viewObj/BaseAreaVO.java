package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class BaseAreaVO {

    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private Byte level;

    private Date gmtCreate;

    private Date gmtModify;
}
