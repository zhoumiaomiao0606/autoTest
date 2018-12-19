package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportApplyLoanPushVO {
    String bridgeProcessId;
    String customerName;
    String customerIdCard;
    String carDetailName;
    String carPrice;
    String downPaymentMoney;
    String remitAmount;
    String lendAmount;
    String loanTime;
    String interestRate;
    String loanForm;
    String bank;
    String tel;
    String bankCard;
}
