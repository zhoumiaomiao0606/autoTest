package com.yunche.loan.service;

import com.yunche.loan.domain.param.TallyOrderResultUpdateParam;
import com.yunche.loan.domain.vo.TallyOrderResultVO;

public interface TallyOrderResultService {
    TallyOrderResultVO detail(Long orderId);

    void update(TallyOrderResultUpdateParam param);
}
