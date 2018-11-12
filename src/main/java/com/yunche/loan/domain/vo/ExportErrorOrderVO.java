package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportErrorOrderVO {
    String customerName;
    String customerIdCard;
    String carDetailName;
    String carPrice;
    String downPaymentMoney;
    String remitAmount;
    String lendAmount;
    String loanTime;
    String bank;
}
