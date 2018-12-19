package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryCashierAccountConfParam
{
    private Long employeeId;

    private String createUser;

    @NotNull
    private Integer pageIndex = 1;
    @NotNull
    private Integer pageSize = 20;
}
