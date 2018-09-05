package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanDataFlowDO {

    private Long id;

    private Long orderId;

    private Long flowOutDeptId;

    private String flowOutDeptName;

    private Long flowInDeptId;

    private String flowInDeptName;

    private Byte expressCom;

    private String expressNum;

    private Date expressSendDate;

    private Date expressReceiveDate;

    private String expressReceiveMan;

    private Byte hasMortgageContract;

    private String info;

    private String contractNum;

    private Byte type;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}