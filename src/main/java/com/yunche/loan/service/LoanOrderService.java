package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanOrderService {

    ResultBean<List<BaseLoanOrderVO>> query(LoanOrderQuery query);

    ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<CreditRecordVO> creditRecordDetail(Long orderId, Byte type);

    ResultBean<Long> creditRecord(CreditRecordParam customer);

    ResultBean<CustDetailVO> customerDetail(Long orderId);

    ResultBean<Void> updateCustomer(CustDetailVO custDetailVO);

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId);

    ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId);

    ResultBean<Void> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanVO);

    ResultBean<LoanHomeVisitVO> homeVisitDetail(Long orderId);

    ResultBean<Void> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam);

    ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<Void> infoSupplement(InfoSupplementParam infoSupplementParam);

    ResultBean<String> createCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Long> createLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam);
}
