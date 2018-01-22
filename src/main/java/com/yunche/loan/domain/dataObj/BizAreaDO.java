package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class BizAreaDO {
    private Long id;

    private String name;

    private Long parentId;

    private Long employeeId;

    private Integer level;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}