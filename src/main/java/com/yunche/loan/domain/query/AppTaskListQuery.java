package com.yunche.loan.domain.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppTaskListQuery {

    @NotNull
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    @NotNull
    private Integer pageSize = 10;

    @NotNull
    private Integer multipartType;

    private String customer;

}
