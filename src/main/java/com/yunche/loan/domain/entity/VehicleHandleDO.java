package com.yunche.loan.domain.entity;

import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class VehicleHandleDO extends VehicleHandleDOKey {
    private String handdlePerson;

    private String trailVehicleDate;

    private String trailVehicleAddress;

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
    private List<UniversalCustomerFileVO> files = new ArrayList<UniversalCustomerFileVO>();

    //省市区
    private Long provenceId;

    private String provenceName;

    private Long cityId;

    private String cityName;

    private Long countyId;

    private String countyName;

}