package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.InsuranceListQuery;

import java.util.List;

public interface InsuranceUrgeService {

    List list(InsuranceListQuery insuranceListQuery);

    ResultBean detail(Long orderId);
}
