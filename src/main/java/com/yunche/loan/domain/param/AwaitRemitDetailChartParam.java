package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 11:12
 * @description:
 **/
@Data
public class AwaitRemitDetailChartParam extends BaseQuery
{
    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
