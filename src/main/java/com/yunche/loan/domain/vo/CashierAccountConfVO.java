package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CashierAccountConfVO
{
    private Long id;

    private Long employeeId;

    private String employeeName;

    private String companyAccount;

    private String companyAccountBank;

    private String companyAccountNumber;

    private String createUser;

    private Date createTime;
}
