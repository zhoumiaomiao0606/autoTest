package com.yunche.loan.service;

import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;

public interface InstalmentService {
    public ApplyDiviGeneralInfoVO detail(Long orderId);
}
