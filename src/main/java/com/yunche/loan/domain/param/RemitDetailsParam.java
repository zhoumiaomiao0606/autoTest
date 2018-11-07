package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class RemitDetailsParam
{
    private Long orderId;

    private String remit_bank;

    private String remit_account;

    private String remit_account_number;

    //打款开户行
    private String remit_business_id;

}
