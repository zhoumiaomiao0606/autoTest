package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ManualInsuranceParam;

import java.util.List;

public interface InsuranceUrgeDistributeService {

    List list(Integer pageIndex, Integer pageSize,Byte status);

    ResultBean manualDistribution(ManualInsuranceParam param);

    List selectInsuranceDistributeEmployee();
}
