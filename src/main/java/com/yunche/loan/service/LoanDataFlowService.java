package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.RecombinationVO;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
public interface LoanDataFlowService {

    ResultBean<RecombinationVO> detail(Long orderId, String taskKey);

    ResultBean create(LoanDataFlowDO loanDataFlowDO);

    ResultBean update(LoanDataFlowDO loanDataFlowDO);

    ResultBean contract_c2b_detail(Long orderId);

    ResultBean contract_c2b_create(LoanDataFlowDO loanDataFlowDO);

    ResultBean contract_c2b_update(LoanDataFlowDO loanDataFlowDO);

}
