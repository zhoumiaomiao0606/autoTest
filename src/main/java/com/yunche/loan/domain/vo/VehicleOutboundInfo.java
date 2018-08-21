package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleOutboundDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-20 12:32
 * @description: 车辆出库视图
 **/
@Data
public class VehicleOutboundInfo extends VehicleOutboundDO
{
    //贷款金额
    private BigDecimal loan_amount;

    //代偿总额
    private BigDecimal compensation_amount_sum;

    //清收成本
    private BigDecimal final_costs;

    //省市区
    private Long provenceId;

    private Long cityId;

    private Long countyId;



}
