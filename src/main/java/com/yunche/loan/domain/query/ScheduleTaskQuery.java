package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ScheduleTaskQuery {
    String key;

    Long employeeId;

    Long telephoneVerifyLevel;

    Long collectionLevel;

    Long financeLevel;

    Long maxGroupLevel;

    Long financeApplyLevel;

    Long refundApplyLevel;

    Long materialSupplementLevel;

    Set<String> juniorIds = Sets.newHashSet();

    private List<Long> bizAreaIdList = Lists.newArrayList();//区域合伙人ID列表
    private List<String> bankList = Lists.newArrayList();//银行name列表
}
