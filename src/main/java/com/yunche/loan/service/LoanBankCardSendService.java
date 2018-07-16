package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
public interface LoanBankCardSendService {

    ResultBean<Void> save(LoanBankCardSendDO loanBankCardSendDO);

    ResultBean<UniversalBankCardSendVO> detail(Long orderId);
}
