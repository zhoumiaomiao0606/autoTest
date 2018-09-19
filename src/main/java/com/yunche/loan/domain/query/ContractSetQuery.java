package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class ContractSetQuery extends BaseQuery{
    private String bank;

    private String userId;

    private String gmtCreateStart1;

    private String gmtCreateEnd1;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;
}
