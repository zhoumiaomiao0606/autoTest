package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.LoanFinancialPlanTempHisDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.entity.LoanRefundApplyDO;
import com.yunche.loan.domain.entity.LoanRejectLogDO;
import com.yunche.loan.mapper.LoanFinancialPlanTempHisDOMapper;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.mapper.LoanRefundApplyDOMapper;
import com.yunche.loan.mapper.LoanRejectLogDOMapper;
import com.yunche.loan.service.LoanRejectLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_DOING;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_REJECT;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * @author liuzhe
 * @date 2018/4/16
 */
@Service
public class LoanRejectLogServiceImpl implements LoanRejectLogService {

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanRejectLogDOMapper loanRejectLogDOMapper;

    @Autowired
    private LoanFinancialPlanTempHisDOMapper loanFinancialPlanTempHisDOMapper;

    @Autowired
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;


    @Override
    public LoanRejectLogDO rejectLog(Long orderId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskDefinitionKey), "任务节点不能为空");

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        // 进行中
        if (ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus())) {

            Byte taskStatus = getTaskStatus(loanProcessDO, taskDefinitionKey);

            if (null == taskStatus) {
                if (FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(taskDefinitionKey)) {
                    LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(orderId);
                    if (null != loanFinancialPlanTempHisDO) {
                        taskStatus = loanFinancialPlanTempHisDO.getStatus();
                    }
                } else if (REFUND_APPLY.getCode().equals(taskDefinitionKey)) {
                    LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(orderId);
                    if (null != loanRefundApplyDO) {
                        taskStatus = loanRefundApplyDO.getStatus();
                    }
                }
            }

            // 被打回
            if (TASK_PROCESS_REJECT.equals(taskStatus)) {

                LoanRejectLogDO loanRejectLogDO = loanRejectLogDOMapper.lastByOrderIdAndTaskDefinitionKey(orderId, taskDefinitionKey);
                return loanRejectLogDO;
            }
        }
        return null;
    }

    /**
     * 获取任务状态
     *
     * @param loanProcessDO
     * @param taskDefinitionKey
     * @return
     */
    public static Byte getTaskStatus(LoanProcessDO loanProcessDO, String taskDefinitionKey) {

        Class<? extends LoanProcessDO> clazz = loanProcessDO.getClass();

        String[] keyArr = null;

        if (taskDefinitionKey.startsWith("servicetask")) {
            keyArr = taskDefinitionKey.split("servicetask");
        } else if (taskDefinitionKey.startsWith("usertask")) {
            keyArr = taskDefinitionKey.split("usertask");
        }

        try {

            // 下划线转驼峰
            String methodBody = StringUtil.underline2Camel(keyArr[1]);

            String methodName = "get" + methodBody;

            Method method = clazz.getMethod(methodName);

            Byte result = (Byte) method.invoke(loanProcessDO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
