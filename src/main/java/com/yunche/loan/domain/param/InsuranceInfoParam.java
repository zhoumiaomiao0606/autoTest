package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class InsuranceInfoParam
{
    @NotBlank
    private Byte year;//年次

    private List<InsuranceRelevanceUpdateParam> insuranceRelevanceList; //保险列表
}
