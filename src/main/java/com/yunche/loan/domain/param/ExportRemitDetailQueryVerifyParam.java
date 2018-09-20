package com.yunche.loan.domain.param;


import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ExportRemitDetailQueryVerifyParam
{
    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;

    private  String startDate;
    private  String endDate;

   /* private  String startRemitDate;
    private  String endRemitDate;*/


    private  BigDecimal startRemitAmount;
    private  BigDecimal endRemitAmount;



    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
