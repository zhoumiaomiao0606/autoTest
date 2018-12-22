package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-11 18:10
 * @description:
 **/
@Data
public class ExportCustomerInfoParam
{
    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    //主贷人姓名
    private String pname;

    //合同资料公司至银行-确认接收时间
    private String startTime;

    private String endTime;

    private String materialPrintStartTime;

    private String materialPrintEndTime;

    private String materialPrintSubmitUser;

    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();
}
