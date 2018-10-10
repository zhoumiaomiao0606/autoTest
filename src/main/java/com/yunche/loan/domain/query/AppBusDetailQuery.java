package com.yunche.loan.domain.query;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AppBusDetailQuery extends BaseQuery{

    private String makeMoneyState;

    private String gmtCreateStart1;

    private String gmtCreateEnd1;

    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();

    //合伙人团队列表
    private List<Long> partnerList;

}
