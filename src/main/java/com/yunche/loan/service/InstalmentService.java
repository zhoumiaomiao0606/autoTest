package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;

public interface InstalmentService {
    public ApplyDiviGeneralInfoVO detail(Long orderId);

    public void update(InstalmentUpdateParam param);
}
