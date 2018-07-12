package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.RecombinationVO;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
public interface LoanDataFlowService {

    ResultBean<RecombinationVO> detail(Long orderId, Byte type);

    ResultBean create(LoanDataFlowDO loanDataFlowDO);

    ResultBean update(LoanDataFlowDO loanDataFlowDO);

    ResultBean<Object> key();

    ResultBean<Object> key_get_type(String key);

    ResultBean<Object> type_get_key(String type);
}