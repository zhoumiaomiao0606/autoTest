package com.yunche.loan.domain.param;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotEmpty;
import java.util.List;

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
}
