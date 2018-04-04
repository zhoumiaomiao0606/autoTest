package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.param.AppLoanFinancialPlanParam;
import com.yunche.loan.domain.param.LoanFinancialPlanParam;
import com.yunche.loan.domain.vo.AppLoanFinancialPlanVO;
import com.yunche.loan.domain.vo.LoanFinancialPlanVO;

/**
 * @author liuzhe
 * @date 2018/3/9
 */
public interface LoanFinancialPlanService {
    ResultBean<Long> create(LoanFinancialPlanDO loanFinancialPlanDO);

    ResultBean<Void> update(LoanFinancialPlanDO loanFinancialPlanDO);

    ResultBean<LoanFinancialPlanVO> calc(LoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId);

    ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<Long> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam);
}
