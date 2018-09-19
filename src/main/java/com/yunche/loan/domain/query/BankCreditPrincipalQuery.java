package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class BankCreditPrincipalQuery extends BaseQuery{
    private String bank;

    private String partnerId;
    //1银行0社会
    private int selectType;

    private String gmtCreateStart1;

    private String gmtCreateEnd1;

    private String bizAreaId;
}
