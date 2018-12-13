package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

@Data
public class ContractOverDueParam
{
    private String customerName;

    private String loanBank;

    private String orderId;

    private Long partnerId;

    private String remitTimeStart;

    private String remitTimeEnd;


    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();


    private Integer pageIndex = 1;

    private Integer pageSize = 20;
}
