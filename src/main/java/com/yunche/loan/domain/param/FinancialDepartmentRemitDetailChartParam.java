package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 09:48
 * @description:
 **/
@Data
public class FinancialDepartmentRemitDetailChartParam extends BaseQuery
{
    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;

    //征信创建时间
    private  String startDate;
    private  String endDate;

   /* private  String startRemitDate;
    private  String endRemitDate;*/


/*    private BigDecimal startRemitAmount;
    private  BigDecimal endRemitAmount;*/



    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    //征信申请时间-排序
    private byte orderBy;

    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();


}
