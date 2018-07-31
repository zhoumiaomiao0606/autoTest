package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class RiskQuery extends BaseQuery {

    private String customerName;

    private String idCard;

}
