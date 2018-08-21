package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;

import java.util.List;

/**
 * 代偿流程
 *
 * @author liuzhe
 * @date 2018/8/20
 */
public interface LoanProcessInsteadPayService {

    /**
     * 审核
     *
     * @param approval
     * @return
     */
    ResultBean<Void> approval(ApprovalParam approval);

    /**
     * 开启流程
     *
     * @param orderId           主订单ID
     * @param insteadPayOrderId 代偿单ID
     * @return
     */
    Long startProcess(Long orderId, Long insteadPayOrderId);
}
