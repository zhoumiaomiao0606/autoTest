package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class AddMoneyConfirm
{
    private Long orderId;

    private Byte keyRiskPremiumConfirm;
}
