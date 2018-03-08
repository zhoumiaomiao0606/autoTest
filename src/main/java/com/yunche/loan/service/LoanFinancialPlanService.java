package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.param.AppLoanFinancialPlanParam;
import com.yunche.loan.domain.vo.AppLoanFinancialPlanVO;

/**
 * @author liuzhe
 * @date 2018/3/9
 */
public interface LoanFinancialPlanService {
    ResultBean<Long> create(LoanFinancialPlanDO loanFinancialPlanDO);

    ResultBean<Void> update(LoanFinancialPlanDO loanFinancialPlanDO);

    ResultBean<AppLoanFinancialPlanVO> calc(LoanFinancialPlanDO loanFinancialPlanDO);
}
