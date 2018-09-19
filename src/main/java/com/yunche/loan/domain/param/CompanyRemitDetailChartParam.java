package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 14:45
 * @description:
 **/
@Data
public class CompanyRemitDetailChartParam  extends BaseQuery
{
    //权限过滤
    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
