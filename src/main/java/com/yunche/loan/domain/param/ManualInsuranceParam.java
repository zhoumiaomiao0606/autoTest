package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class ManualInsuranceParam {

    List<AllocationRela> allotList = Lists.newArrayList();

    private Long sendeeId;

    @Data
    public static class AllocationRela{
        Long orderId;
        Integer insuranceYear;
    }
}
