package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanProcessService {

    ResultBean<String> startProcessInstance(Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> creditApply(InstLoanOrderVO instLoanOrderVO, String processId, Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> creditVerify(String processId, String action, Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> bankCreditRecord(CustBaseInfoVO custBaseInfoVO, String processId, String action, Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> socialCreditRecord(CustBaseInfoVO custBaseInfoVO, String processId, String action, Long operatorId, String operatorName, String operatorRole);

    ResultBean<Void> loanApprove(InstLoanOrderVO instLoanOrderVO, String processId, Long operatorId, String operatorName, String operatorRole);

}
