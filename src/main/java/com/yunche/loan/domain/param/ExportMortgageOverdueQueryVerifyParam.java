package com.yunche.loan.domain.param;


import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ExportMortgageOverdueQueryVerifyParam
{
    private  String startDate;
    private  String endDate;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
