package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class LoanCreditExportQuery  {

    //征信提交时间
    String creditApplyStartTime;
    String creditApplyEndTime;

    //征信查询时间
    String creditQueryStartTime;
    String creditQueryEndTime;

    //征信查询状态
    String creditStatus;//1:未导出、2:已导出、3:已查询

    String employeeId;//征信提交人
    //导出标志
    String mergeFlag;//1:合成身份证图片 , 2:合成图片


}
