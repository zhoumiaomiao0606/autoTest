package com.yunche.loan.service;

import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface TelephoneVerifyService {
    public RecombinationVO detail(Long orderId);

    public void update(TelephoneVerifyParam param);

    public String export(String startDate,String endDate);

}
