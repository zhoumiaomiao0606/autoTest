package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.viewObj.*;
import org.activiti.engine.task.Task;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanProcessService {

    ResultBean<String> getOrderId();

    ResultBean<String> createCreditApply(CreditApplyVO creditApplyVO);

    ResultBean<Void> updateCreditApply(InstProcessOrderVO processInstOrder);

    ResultBean<Void> approval(ApprovalParam approval);

    ResultBean<TaskStateVO> currentTask(String orderId);

    ResultBean<Byte> taskStatus(String orderId, String taskDefinitionKey);
}
