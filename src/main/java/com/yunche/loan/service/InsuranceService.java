package com.yunche.loan.service;

import com.yunche.loan.domain.param.InsuranceUpdateParam;

import java.util.Map;

public interface InsuranceService {

    public Map detail(Long orderId);

    public void update(InsuranceUpdateParam param);
}
