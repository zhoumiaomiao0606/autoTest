package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 10:44
 * @description:
 **/
@Data
public class MaterialReviewParam extends BaseQuery
{
    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;

    //申请征信时间
    private  String startDate;
    private  String endDate;

    //资料接收日期
   /* private  String startDate1;
    private  String endDate1;*/


    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    //资料审核状态  1.已通过   2.未提交
  /*  private Byte state;*/

    //权限过滤
    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
