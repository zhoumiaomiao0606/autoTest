package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleOutboundDO;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-20 12:32
 * @description: 车辆出库视图
 **/
public class VehicleOutboundInfo extends VehicleOutboundDO
{
    //贷款金额
    private String loan_amount;

    //代偿总额
    private String compensation_amount_sum;

    //清收成本
    private String final_costs;

}
