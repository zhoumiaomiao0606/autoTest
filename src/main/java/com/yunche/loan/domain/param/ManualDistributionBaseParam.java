package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class ManualDistributionBaseParam {
    List<ManualDistributionParam> manual_distribution_list;
}
