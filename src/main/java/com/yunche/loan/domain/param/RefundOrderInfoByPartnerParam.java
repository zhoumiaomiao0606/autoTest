package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class RefundOrderInfoByPartnerParam
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
}
