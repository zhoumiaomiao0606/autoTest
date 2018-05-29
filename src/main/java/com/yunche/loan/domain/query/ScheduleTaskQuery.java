package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleTaskQuery {
    String key;

    Long employeeId;

    Long telephoneVerifyLevel;

    Long collectionLevel;

    Long financeLevel;

    Long maxGroupLevel;

    List<Long> juniorIds = Lists.newArrayList();

    private List<Long> areaIdList = Lists.newArrayList();//区域ID列表
    private List<String> bankList = Lists.newArrayList();//银行ID列表
}
