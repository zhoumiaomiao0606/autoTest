package com.yunche.loan.config.task;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yunche.loan.config.constant.LoanProcessEnum.FINANCIAL_SCHEME;

/**
 * @author liuzhe
 * @date 2018/3/17
 */
public class FinancialSchemeServiceTask implements JavaDelegate {

    @Autowired
    private TaskService taskService;


    @Override
    public void execute(DelegateExecution execution) {
        taskService.complete(FINANCIAL_SCHEME.getCode());
    }
}
