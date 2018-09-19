package com.yunche.loan.domain.param;


import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ExportMaterialReviewQueryVerifyParam
{

    //大区
    private Long biz_areaId;

    private List<Long> bizAreaList;
    private  String startDate;
    private  String endDate;

    private  String startDate1;
    private  String endDate1;

    private  String startDate2;
    private  String endDate2;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
