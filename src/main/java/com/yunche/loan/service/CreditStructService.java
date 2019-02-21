package com.yunche.loan.service;

import com.yunche.loan.domain.param.CreditStructParam;
import com.yunche.loan.domain.vo.RecombinationVO;

/**
 * @author liuzhe
 * @date 2019/2/19
 */
public interface CreditStructService {

    RecombinationVO detail(Long orderId);

    void save(CreditStructParam param);
}
