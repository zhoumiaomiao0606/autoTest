package com.yunche.loan.domain.query;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BankCreditPrincipalQuery extends BaseQuery{
    private String bank;

    private String partnerId;
    //1银行0社会
    private int selectType;

    private String gmtCreateStart1;

    private String gmtCreateEnd1;

    private String bizAreaId;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
