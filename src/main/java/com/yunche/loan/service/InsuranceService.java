package com.yunche.loan.service;

import com.yunche.loan.domain.param.InsuranceUpdateParam;

import java.util.Map;

public interface InsuranceService {

    public Map detail(String order_id);

    public void update(InsuranceUpdateParam param);
}
