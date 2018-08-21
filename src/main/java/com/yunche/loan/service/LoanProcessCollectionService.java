package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;

/**
 * @author liuzhe
 * @date 2018/8/20
 */
public interface LoanProcessCollectionService {

    /**
     * [催收工作台]流程 -审核
     *
     * @param approval
     * @return
     */
    ResultBean<Void> approval(ApprovalParam approval);

    /**
     * 开启 -[催收工作台]流程
     *
     * @param orderId           主订单ID
     * @param collectionOrderId 催收单ID
     * @return
     */
    Long startProcess(Long orderId, Long collectionOrderId);
}
