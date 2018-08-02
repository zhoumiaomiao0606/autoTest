package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InsuranceDistributeRecordDO extends InsuranceDistributeRecordDOKey {
    private Long employeeId;

    private String employeeName;

    private Date distributeDate;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String remark;

    private Byte renewInsurStatus;
}