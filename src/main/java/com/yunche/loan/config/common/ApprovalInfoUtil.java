package com.yunche.loan.config.common;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.ApprovalInfoVO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 审核信息
 *
 * @author liuzhe
 * @date 2018/3/23
 */
@Component
public class ApprovalInfoUtil {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;


    /**
     * 获取订单任务节点的最新审核信息
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    public ApprovalInfoVO getApprovalInfoVO(String orderId, String taskDefinitionKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "订单ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务节点不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(orderId), null);
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");


        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .taskDefinitionKey(taskDefinitionKey)
                .orderByTaskCreateTime()
                .desc()
                .listPage(0, 1);

        if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
            HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);
            String taskId = historicTaskInstance.getId();

            HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(loanOrderDO.getProcessInstId())
                    .variableName(taskId)
                    .singleResult();

            if (null != historicVariableInstance) {
                Object value = historicVariableInstance.getValue();
                if (null != value) {
                    ApprovalInfoVO approvalInfoVO = (ApprovalInfoVO) value;
                    return approvalInfoVO;
                }
            }
        }

        return null;
    }
}
