package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanProcessService {

    ResultBean<Void> approval(ApprovalParam approval);

    ResultBean<List<TaskStateVO>> currentTask(Long orderId);

    ResultBean<TaskStateVO> taskStatus(Long orderId, String taskDefinitionKey);

    ResultBean<List<String>> orderHistory(Long orderId, Integer limit);

    ResultBean<LoanProcessLogVO> log(Long orderId, String taskDefinitionKey);
}
