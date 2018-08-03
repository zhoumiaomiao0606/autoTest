package com.yunche.loan.domain.param;


import lombok.Data;

import java.util.List;

@Data
public class ExportMaterialReviewQueryVerifyParam
{
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
}
