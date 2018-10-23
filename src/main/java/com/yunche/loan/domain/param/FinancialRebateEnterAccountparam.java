package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class FinancialRebateEnterAccountparam {

    private Long partnerId;
    private Integer periods;
    private List<String> ids;
}
