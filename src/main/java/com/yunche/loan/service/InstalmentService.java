package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface InstalmentService {
    public RecombinationVO detail(Long orderId);

    public void update(InstalmentUpdateParam param);
}
