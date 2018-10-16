package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 17:42
 * @description:
 **/
@Data
public class LoanApplyOrdersByMonthChartParam {

    private String startDate;

    private String endDate;

    private String selectYear;

    private String selectMonth;
}
