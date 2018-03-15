package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class InsuranceRelevanceDO {
    private Long id;

    private Long insurance_info_id;

    private String insurance_company_name;

    private String insurance_number;

    private String area;

    private BigDecimal insurance_amount;

    private Date start_date;

    private Date end_date;

    private Byte insurance_type;

    private Byte status;

    private String feature;
}