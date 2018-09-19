package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 09:19
 * @description:
 **/
@Data
public class BankCreditChartParam  extends BaseQuery
{
    //大区id
    private Long biz_areaId;

    private List<Long> bizAreaList;
    //银行审核时间
    private  String startDate;
    private  String endDate;

    //征信申请时间
/*    private String startCreditGmtCreate;
    private String endCreditGmtCreate;*/

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    //征信申请时间-排序
    private byte orderBy =2;

    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
