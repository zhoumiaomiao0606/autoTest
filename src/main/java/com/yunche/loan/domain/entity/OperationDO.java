package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperationDO {
    private Long id;

    private String name;

    private String uri;

    private Long pageId;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;
}