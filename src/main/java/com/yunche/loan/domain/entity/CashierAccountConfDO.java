package com.yunche.loan.domain.entity;

import java.util.Date;

public class CashierAccountConfDO {
    private Long id;

    private Long employeeId;

    private String companyAccount;

    private String companyAccountBank;

    private String companyAccountNumber;

    private String createUser;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount == null ? null : companyAccount.trim();
    }

    public String getCompanyAccountBank() {
        return companyAccountBank;
    }

    public void setCompanyAccountBank(String companyAccountBank) {
        this.companyAccountBank = companyAccountBank == null ? null : companyAccountBank.trim();
    }

    public String getCompanyAccountNumber() {
        return companyAccountNumber;
    }

    public void setCompanyAccountNumber(String companyAccountNumber) {
        this.companyAccountNumber = companyAccountNumber == null ? null : companyAccountNumber.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}