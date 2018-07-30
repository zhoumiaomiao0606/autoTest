package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InsuranceDistributeRecordDO extends InsuranceDistributeRecordDOKey {
    private String employeeName;

    private Date distributeDate;

    private Date gmtCreate;

    private Date gmtModify;


}