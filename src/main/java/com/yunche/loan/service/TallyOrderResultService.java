package com.yunche.loan.service;

import com.yunche.loan.domain.param.TallyOrderResultUpdateParam;
import com.yunche.loan.domain.vo.BaseCustomerInfoVO;
import com.yunche.loan.domain.vo.CustomerOrderVO;
import com.yunche.loan.domain.vo.TallyOrderResultVO;

import java.util.List;

public interface TallyOrderResultService {
    TallyOrderResultVO detail(Long orderId);

    void update(TallyOrderResultUpdateParam param);

    List<CustomerOrderVO> CustomerOrder(String name);
}
