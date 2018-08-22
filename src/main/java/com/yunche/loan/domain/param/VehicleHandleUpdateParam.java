package com.yunche.loan.domain.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:37
 * @description:
 **/
@Data
public class VehicleHandleUpdateParam
{
    private Long bankRepayImpRecordId;

    private Long orderid;

    private String vehicleInboundDate;

    private String vehicleInboundAddress;

    private String vehicleInboundAddressDetail;

    private String customerCondition;

    private String chassisNumber;

    private String driverKilometers;

    private String vehichleSurface;

    private String vehicleKey;

    private String vehicleOthermaterial;

    private String vehicleOtherthings;

    private BigDecimal purchaseTax;

    private BigDecimal roadMaintenanceCosts;

    private String roadMaintenanceCostsStatement;

    private BigDecimal breakRulesCosts;

    private String breakRulesCostsStatement;

    private String vehicleStatement;

    private BigDecimal trailVehicleCosts;

    private BigDecimal vehicleMaintenanceCosts;

    private BigDecimal otherCosts;

    private BigDecimal finalCosts;

    private String relationMaterialUrl1;

    private String relationMaterialUrl2;

    //附件路径
    private List<UniversalFileParam> files;
}
