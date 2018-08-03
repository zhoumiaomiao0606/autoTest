package com.yunche.loan.domain.param;


import lombok.Data;

import java.util.List;

@Data
public class ExportSocialCreditQueryVerifyParam
{
    private  String startDate;
    private  String endDate;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;
}
