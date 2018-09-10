package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AccommodationApplyParam {

    private List orderIds = Lists.newArrayList();

    private Date lendDate;
}
