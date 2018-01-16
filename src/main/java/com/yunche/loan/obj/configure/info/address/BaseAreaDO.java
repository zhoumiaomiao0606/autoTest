package com.yunche.loan.obj.configure.info.address;

import lombok.Data;

/**
 * 地址
 */
@Data
public class BaseAreaDO {
    private Integer id;

    private Long codeId;

    private Long parentCodeId;

    private String positionName;

    private Byte level;

}