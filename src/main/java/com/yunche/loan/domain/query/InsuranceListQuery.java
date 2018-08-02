package com.yunche.loan.domain.query;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class InsuranceListQuery {

    /**
     * 当前页数  默认值：1
     */
    @NotNull
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    @NotNull
    private Integer pageSize = 10;
    @NotBlank
    private String taskDefinitionKey;

    private  Byte taskStatus; // 1:未分配列表  2：已分配列表

    private Long employeeId;

    private String customerName;

    private String bankName;

    private Long partnerId;

}
