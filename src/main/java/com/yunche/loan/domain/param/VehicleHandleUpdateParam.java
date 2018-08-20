package com.yunche.loan.domain.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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

    private Long handdlePerson;

    private Date trailVehicleDate;

    private String trailVehicleAddress;

    private Date vehicleInboundDate;

    private String vehicleInboundAddress;

    private String vehicleInboundAddressDetail;

    private String customerCondition;

    private String chassisNumber;

    private String driverKilometers;

    private String vehichleSurface;

    private Byte vehicleKey;

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
}
