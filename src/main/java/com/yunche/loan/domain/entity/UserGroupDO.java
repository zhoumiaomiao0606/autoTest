package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserGroupDO {
    private Long id;

    private String name;

    private Long departmentId;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}