package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BankCodeParam
{

    private String name;
    @NotNull
    private Integer pageIndex = 1;
    @NotNull
    private Integer pageSize = 20;
}
