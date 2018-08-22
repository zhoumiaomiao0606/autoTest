package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class SubimitVisitDoorVO {
    private String  visit_date;
    private String id;
    private String orderId;
    private String customerName;
    private String customerMobile;
    private String customerIdCard;
    private String carType;
    private String vehicleApplyLicensePlateArea;
    private String vehicleApplyLicensePlateAreaName;
    private String financialBank;
    private String partnerName;
    private String salesmanName;
    private String carName;
}
