package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class CustomersLoanFinanceInfoByPartnerParam
{
    /**
     * 当前页数  默认值：1
     */
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    private Integer pageSize = 10;

    private Long partnerId;

    //1 合伙人不良余额 2合伙人账户逾期 3在保余额 4.贷款总额
    private Integer code;
}
