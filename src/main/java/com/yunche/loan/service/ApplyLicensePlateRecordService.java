package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;

import java.util.Map;

public interface ApplyLicensePlateRecordService {
    public Map detail(String order_id);

    public void update(ApplyLicensePlateRecordUpdateParam param);
}
