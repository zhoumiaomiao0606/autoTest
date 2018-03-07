package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanProcessService {

    ResultBean<String> getOrderId();

    ResultBean<String> createCreditApply(CreditApplyVO creditApplyVO);

    ResultBean<Void> updateCreditApply(CreditApplyOrderVO processInstOrder);

    ResultBean<Void> approval(ApprovalParam approval);

    ResultBean<TaskStateVO> currentTask(Long orderId);

    ResultBean<Byte> taskStatus(Long orderId, String taskDefinitionKey);
}
