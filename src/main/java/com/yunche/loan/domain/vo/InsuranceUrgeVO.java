package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InsuranceUrgeVO {

    private Long orderId;
    private String partnerName;
    private String name;
    private String idCard;
    private String mobile;
    private String bank;
    private String employeeName;
    private String carPrice;
    private Integer insuranceYear;
    private Byte insuranceType;
    private Date startDate;
    private Date endDate;
    private Integer days;
    private Date businessStartDate;
    private Date businessEndDate;

}
