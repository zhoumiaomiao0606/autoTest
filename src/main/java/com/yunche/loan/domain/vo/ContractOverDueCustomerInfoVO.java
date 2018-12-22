package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractOverDueCustomerInfoVO
{
    //业务编号
    private String orderId;

    //主贷人姓名
    private String customerName;
    //联系电话
    private String customerMobile;
    //身份证号码
    private String customerIdCard;
    //业务员
    private String salesmanName;
    //业务团队
    private String partnerName;
    //贷款银行
    private String bank;
    //贷款金额
    private BigDecimal loanAmount;
    //垫款金额
    private BigDecimal remitAmount;

    //垫款时间
    private Date remitDate;

    //超期天数
    private String overDueDays;
}
