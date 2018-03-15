package com.yunche.loan.service;

import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import java.util.Map;

public interface InsuranceService {

    public RecombinationVO detail(Long orderId);

    public RecombinationVO query(Long orderId);

    public void update(InsuranceUpdateParam param);
}
