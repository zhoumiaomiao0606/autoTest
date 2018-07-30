package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ManualInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;

import java.util.List;

public interface InsuranceUrgeDistributeService {

    List list(InsuranceListQuery insuranceListQuery);

    ResultBean manualDistribution(ManualInsuranceParam param);

    List selectInsuranceDistributeEmployee();

    ResultBean detail(Long orderId);
}
