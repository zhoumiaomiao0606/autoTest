package com.yunche.loan.domain.dataObj;

import lombok.Data;

@Data
public class UserGroupDO {
    private Long id;

    private String name;

    private Integer departmentId;

    private String description;
}