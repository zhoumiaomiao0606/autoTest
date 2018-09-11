package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class JinTouHangRepayInfoVO {
    String lendDate;
    String repayDate;
    String remitAmount;
    String name;
    String idCard;
    String bankPeriodPrincipal;
    String repayType;
    String repayRemark;
}
