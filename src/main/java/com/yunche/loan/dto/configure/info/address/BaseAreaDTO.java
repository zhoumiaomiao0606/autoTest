package com.yunche.loan.dto.configure.info.address;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class BaseAreaDTO {

    private Integer id;

    private Long areaId;

    private Long parentAreaId;

    private String areaName;

    private Byte level;

}
