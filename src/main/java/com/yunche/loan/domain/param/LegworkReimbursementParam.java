package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LegworkReimbursementParam {

    private Long maxGroupLevel;

    private Long loginUserId;

    @NotNull
    private String taskDefinitionKey;
    @NotNull
    private String taskStatus;
    @NotNull
    private Integer pageIndex;
    @NotNull
    private Integer pageSize;

    private String customerName;

    private String bank;

    private String partnerId;

    private String salesmanName;
}
