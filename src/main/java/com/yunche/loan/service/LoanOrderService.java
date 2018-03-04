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

    ResultBean<List<BaseInstProcessOrderVO>> query(LoanOrderQuery query);

    ResultBean<InstProcessOrderVO> creditApplyDetail(String id);

    ResultBean<CreditRecordVO> creditRecordDetail(String id, Byte type);

    ResultBean<Void> creditRecord(CreditRecordParam customer);

    ResultBean<CustDetailVO> customerDetail(String orderId);

    ResultBean<Void> updateCustomer(CustDetailVO custDetailVO);

    ResultBean<Void> faceOff(String orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<LoanCarInfoVO> loanCarInfoDetail(String orderId);

    ResultBean<Void> createOrUpdateLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(String orderId);

    ResultBean<Void> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanVO);

    ResultBean<LoanHomeVisitVO> homeVisitDetail(String orderId);

    ResultBean<Void> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam);

    ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<Void> infoSupplement(InfoSupplementParam infoSupplementParam);
}
