package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class InsuranceDetailVO<T> {
    private T info;

    private List<UniversalCreditInfoVO> credits = Lists.newArrayList();

    private List<UniversalCustomerVO> customers = Lists.newArrayList();

    private T  risks;

    private List<newInsuranceVO> newInsurance = Lists.newArrayList();
}
