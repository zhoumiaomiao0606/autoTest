package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AccommodationApplyParam {

    private List orderIds = Lists.newArrayList();

    private Date lendDate;

    private Long orderId;

    private Byte repayType;

    private Date repayDate;

    private String repayRemark;

    private BigDecimal interest;

    private BigDecimal poundage;

    private Date repayInterestDate;

    private String repayRegisterRemark;
}
