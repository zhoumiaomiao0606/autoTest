package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubimitVisitDoorParam {
    private Long maxGroupLevel;

    private Long loginUserId;

    private String userName;

    @NotNull
    private Integer pageIndex;
    @NotNull
    private Integer pageSize;
}
