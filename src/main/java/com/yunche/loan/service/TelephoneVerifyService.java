package com.yunche.loan.service;

import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface TelephoneVerifyService {

    RecombinationVO detail(Long orderId);

    void update(TelephoneVerifyParam param);

    String export(TelephoneVerifyParam telephoneVerifyParam);

}
