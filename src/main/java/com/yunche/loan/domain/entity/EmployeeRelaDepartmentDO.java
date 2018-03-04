package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeRelaDepartmentDO extends EmployeeRelaDepartmentDOKey {
    private Date gmtCreate;

    private Date gmtModify;
}