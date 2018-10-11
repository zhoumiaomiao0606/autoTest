package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RefundOrderInfoByPartnerVO extends CustomersLoanFinanceInfoByPartnerVO
{
    //垫款时间
    private Date remitDate;

    //退单时间
    private Date refundOrderDate;


}
