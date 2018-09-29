package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class CreditPicExportVO {
    private Long orderId;
    private Long loanCustomerId;
    private String customerName;
    private Long employeeId;
    private String idCard;
}
