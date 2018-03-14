package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import java.util.Map;

public interface ApplyLicensePlateDepositInfoService {

    public Map detail(String order_id);

    public void update(ApplyLicensePlateDepositInfoUpdateParam param);

}
