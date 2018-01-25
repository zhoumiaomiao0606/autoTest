package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class DepartmentDO {
    private Long id;

    private String name;

    private Long parentId;

    private Long employeeId;

    private Integer level;

    private String tel;

    private String fax;

    private Long areaId;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}