package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDO {
    private Long id;

    private String name;

    private String idCard;

    private String phone;

    private String email;

    private String dingDing;

    private String departmentId;

    private String leader;

    private String title;

    private Date entryDate;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}