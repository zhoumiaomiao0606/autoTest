package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

@Data
public class QueryVINParam
{
    private String queryVIN;

    Set<String> juniorIds = Sets.newHashSet();

    Long maxGroupLevel;
}
