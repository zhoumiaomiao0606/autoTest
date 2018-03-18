package com.yunche.loan.service;

import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;


public interface ApplyLicensePlateRecordService {
    RecombinationVO detail(Long orderId);

    void update(ApplyLicensePlateRecordUpdateParam param);


}
