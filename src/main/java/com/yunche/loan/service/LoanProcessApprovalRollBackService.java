package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.param.ApprovalParam;

/**
 * @author liuzhe
 * @date 2018/8/30
 */
public interface LoanProcessApprovalRollBackService {

    /**
     * 执行-反审任务
     *
     * @param approval
     * @param loanOrderDO
     * @param loanProcessDO
     * @return
     */
    ResultBean<Void> execRollBackTask(ApprovalParam approval, LoanOrderDO loanOrderDO, LoanProcessDO loanProcessDO);
}
