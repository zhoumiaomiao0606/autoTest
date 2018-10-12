package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 10:04
 * @description:
 **/
@Data
public class MortgageOverdueParam  extends BaseQuery
{
    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;

    //申请征信时间
    private  String startDate;
    private  String endDate;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;
    //征信申请时间-排序
    private byte orderBy;

    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
