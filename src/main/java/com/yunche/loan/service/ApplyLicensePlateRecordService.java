package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;


public interface ApplyLicensePlateRecordService {
    public RecombinationVO detail(Long orderId);

    public void update(ApplyLicensePlateRecordUpdateParam param);
}
