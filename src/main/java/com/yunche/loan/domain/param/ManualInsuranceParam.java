package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class ManualInsuranceParam {

    List<Long> orderIdList = Lists.newArrayList();

    private String sendee;

    private Long sendeeId;

    private Integer  insuranceYear;
}
