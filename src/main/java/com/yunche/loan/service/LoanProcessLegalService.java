package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;

/**
 * @author liuzhe
 * @date 2018/8/23
 */
public interface LoanProcessLegalService {

    /**
     * 法务处理-流程
     *
     * @param approval
     * @return
     */
    ResultBean<Void> approval(ApprovalParam approval);

    /**
     * 开启流程
     *
     * @param orderId           主订单ID
     * @param collectionOrderId 催收批次号
     * @return
     */
    Long startProcess(Long orderId, Long collectionOrderId);
}
