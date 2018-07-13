package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreditAutomaticCommitParam {
    @NotEmpty
    private String orderId;
}
