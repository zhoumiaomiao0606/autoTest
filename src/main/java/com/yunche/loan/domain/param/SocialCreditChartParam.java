package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:08
 * @description:
 **/
@Data
public class SocialCreditChartParam extends BaseQuery
{
  /*  private  String startDate;
    private  String endDate;*/

    //征信申请时间
  /*  private String startCreditGmtCreate;
    private String endCreditGmtCreate;*/

    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;
    //征信申请时间-排序
    private byte orderBy;


    //权限过滤
    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
