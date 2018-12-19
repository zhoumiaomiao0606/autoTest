package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CashierAccountConfDO {
    private Long id;

    private Long employeeId;

    private String companyAccount;

    private String companyAccountBank;

    private String companyAccountNumber;

    private String createUser;

    private Date createTime;


}