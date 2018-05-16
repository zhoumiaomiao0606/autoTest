package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanRepayPlanDOKey {
    private Long orderId;

    private Date repayDate;


}