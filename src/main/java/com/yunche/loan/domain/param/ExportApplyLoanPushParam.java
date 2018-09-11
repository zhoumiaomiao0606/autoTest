package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

@Data
public class ExportApplyLoanPushParam {

    private String  name;

    private Date startLendDate;//借款开始时间

    private Date endLendDate;//借款结束时间

    private  Date startRepayDate;//还款开始时间

    private  Date endRepayDate;//还款结束时间
}
