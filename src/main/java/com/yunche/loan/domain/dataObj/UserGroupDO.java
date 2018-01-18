package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class UserGroupDO {
    private Long id;

    private String name;

    private Integer departmentId;

    private String description;

    private Date gmtCreate;

    private Date gmtModify;
}