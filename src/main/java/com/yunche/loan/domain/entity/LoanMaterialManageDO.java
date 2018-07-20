package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanMaterialManageDO {

    private Long orderId;

    private String materialNum;

    private Date completeDate;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;
}