package com.yunche.loan.domain.param;


import com.google.common.collect.Sets;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
public class ExportSocialCreditQueryVerifyParam
{
    private  String startDate;
    private  String endDate;

    private Byte  state;

    //征信申请时间
    private String startCreditGmtCreate;
    private String endCreditGmtCreate;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    Long maxGroupLevel;

    Set<String> juniorIds = Sets.newHashSet();
}
