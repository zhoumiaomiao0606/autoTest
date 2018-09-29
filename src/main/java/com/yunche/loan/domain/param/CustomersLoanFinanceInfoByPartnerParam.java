package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class CustomersLoanFinanceInfoByPartnerParam
{
    private Long partnerId;

    //1 合伙人不良余额 2合伙人账户逾期 3在保余额 4.贷款总额
    private Integer code;
}
