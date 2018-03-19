package com.yunche.loan.config.task;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component("financialSchemeServiceTask")
public class FinancialSchemeServiceTask implements JavaDelegate, Serializable {

    /**
     * 实现JavaDelegate接口，使用其中的execute方法 由于要放入流程定义中，所以要实现可序列话接口
     */
    private static final long serialVersionUID = 5593437463482732772L;


    @Autowired
    private TaskService taskService;


    public FinancialSchemeServiceTask() {
    }

    @Override
    public void execute(DelegateExecution execution) {
//        taskService.complete(FINANCIAL_SCHEME.getCode());
    }
}
