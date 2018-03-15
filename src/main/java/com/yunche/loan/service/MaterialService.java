package com.yunche.loan.service;

import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface MaterialService {

    public RecombinationVO detail(Long orderId);

    public void update(MaterialUpdateParam param);
}
