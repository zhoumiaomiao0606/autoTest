package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import java.util.Map;

public interface ApplyLicensePlateDepositInfoService {

    public RecombinationVO detail(Long orderId);

    public void update(ApplyLicensePlateDepositInfoUpdateParam param);

}
