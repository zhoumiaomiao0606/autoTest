package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.LoanOrderParam;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanProcessOrderService {

    /**
     * 新建业务单
     *
     * @param loanOrderDO
     * @return
     */
    ResultBean<Long> create(LoanOrderDO loanOrderDO);

    /**
     * 新建业务单  并绑定 贷款基本信息ID & 主贷人ID
     *
     * @param baseInfoId 贷款基本信息ID
     * @param customerId 主贷人ID
     * @return
     */
    ResultBean<Long> createLoanOrder(Long baseInfoId, Long customerId);

    /**
     * 更新业务单
     *
     * @param loanOrderDO
     * @return
     */
    ResultBean<Void> update(LoanOrderDO loanOrderDO);
}
