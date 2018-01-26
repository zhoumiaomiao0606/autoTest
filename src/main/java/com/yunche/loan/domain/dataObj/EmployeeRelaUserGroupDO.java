package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeRelaUserGroupDO extends EmployeeRelaUserGroupDOKey {
    private Date gmtCreate;

    private Date gmtModify;
}