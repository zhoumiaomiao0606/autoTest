package com.yunche.loan.config.task;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.yunche.loan.config.constant.LoanProcessEnum.FINANCIAL_SCHEME;


@Component("financialSchemeServiceTask")
public class FinancialSchemeServiceTask implements JavaDelegate, Serializable {

    /**
     * 实现JavaDelegate接口，使用其中的execute方法 由于要放入流程定义中，所以要实现可序列话接口
     */
    private static final long serialVersionUID = 5593437463482732772L;


    @Autowired
    private TaskService taskService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;


    public FinancialSchemeServiceTask() {
    }

    @Override
    public void execute(DelegateExecution execution) {

        // TODO  orderId
        Long orderId = null;
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");

        Task task = getTask(loanOrderDO.getProcessInstId(), FINANCIAL_SCHEME.getCode());

        taskService.complete(task.getId());
    }


    /**
     * 获取任务
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    private Task getTask(String processInstId, String taskDefinitionKey) {
        // 获取当前流程taskId
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "当前任务不存在");
        return task;
    }
}
