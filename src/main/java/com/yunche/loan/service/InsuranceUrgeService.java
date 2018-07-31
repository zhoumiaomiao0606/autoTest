package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RenewInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;

import java.util.List;

public interface InsuranceUrgeService {

    List list(InsuranceListQuery insuranceListQuery);

    ResultBean detail(Long orderId);

    Void renew(RenewInsuranceParam renewInsuranceParam);

    ResultBean renewDetail(Long id,Long orderId);

    String generateSms(RenewInsuranceParam renewInsuranceParam);

    ResultBean sendSms(RenewInsuranceParam param);
}
