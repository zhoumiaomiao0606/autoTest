package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-18 09:57
 * @description:
 **/
@Data
public class LoanApplyOrdersByMonthChartVO
{
    //贷款中
    private String uc_remitCount;
    //已垫款
    private String c_remitCount;
    //合同准备中
    private String uc_materialPrintCount;
    //已放款
    private String c_bankLendCount;
    //抵押中
    private String uc_depositCount;
    //抵押完成
    private String c_depositCount;

}
