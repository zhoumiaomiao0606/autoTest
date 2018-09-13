package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportApplyLoanPushVO {
    String rowId;
    String customerName;
    String customerIdCard;
    String carDetailName;
    String carPrice;
    String downPaymentMoney;
    String remitAmount;
    String loanTime;
    String interestRate;
    String loanForm;
    String bank;
}
