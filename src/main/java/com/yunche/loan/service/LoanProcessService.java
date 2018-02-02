package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
public interface LoanProcessService {

    ResultBean<String> startProcessInstance(Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> creditApply(InstLoanOrderVO instLoanOrderVO, String processId);

    ResultBean<Void> creditVerify(String processId, String action);

    ResultBean<List<Task>> getTasks(String userGroupName);

    ResultBean<Void> completeTasks(boolean isApproved, String taskId);

}
