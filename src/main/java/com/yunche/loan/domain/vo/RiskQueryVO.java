package com.yunche.loan.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RiskQueryVO {
    private String id;

    private String customerName;

    private String idCard;

    private String insuranceCompanyName;

    private Byte insuranceYear;

    private String carName;

    private BigDecimal carPrice;

    private String saleName;

    private String partnerName;

    private Date endDate;

    private String dateNum;

    private Date endDate1;

    private String dateNum1;

    private String insuranceType;

    private String endDateTotal;

}
