package com.yunche.loan.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class newInsuranceVO {

    private String insurance_company_name;

    private String insurance_number;

    private BigDecimal insurance_amount;

    private Date start_date;

    private Date end_date;

    private Byte insurance_type;
}
