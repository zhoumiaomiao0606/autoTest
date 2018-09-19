package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class ContractSetQuery extends BaseQuery{
    private String bank;

    private String userId;

    private String gmtCreateStart1;

    private String gmtCreateEnd1;
}
