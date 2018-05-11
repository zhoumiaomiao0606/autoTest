package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class BankRepayParam {

    private Long orderId;
    private String idCard;
    private String repayCard;
    private String bank;
}
