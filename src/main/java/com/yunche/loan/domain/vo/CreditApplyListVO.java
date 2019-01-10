package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class CreditApplyListVO
{
    private String orderId;

    private String customerName;

    private String idCard;

    private String customerMobile;

    private String saleMan;

    private String partnerName;

    private String orderCreateTime;

    private String creditApplyStatus;
}
