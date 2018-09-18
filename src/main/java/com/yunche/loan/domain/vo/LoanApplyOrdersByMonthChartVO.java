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
    private Long uc_remitCount;
    //已垫款
    private Long c_remitCount;
    //合同准备中
    private Long uc_materialPrintCount;
    //已放款
    private Long c_bankLendCount;
    //抵押中
    private Long uc_depositCount;
    //抵押完成
    private Long c_depositCount;
}
