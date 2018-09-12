package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class ExportApplyLoanPushParam {

    private String  name;

    private String startLendDate;//借款开始时间

    private String endLendDate;//借款结束时间

    private  String startRepayDate;//还款开始时间

    private  String endRepayDate;//还款结束时间
}
