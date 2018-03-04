package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BizAreaDO {
    private Long id;

    private String name;
    /**
     * 绑定的父级ID
     */
    private Long parentId;
    /**
     * 绑定的负责人ID
     */
    private Long employeeId;
    /**
     * 等级
     */
    private Integer level;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}