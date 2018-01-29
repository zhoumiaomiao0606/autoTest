package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class MenuDO {
    private Long id;

    private String name;

    private Long parentId;

    private String uri;

    private Integer level;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;
}