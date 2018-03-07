package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
public interface AppLoanOrderService {
    ResultBean<List<AppLoanProcessOrderVO>> query(AppLoanOrderQuery query);

    ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<String> createCreditApplyOrder(CreditApplyOrderVO creditApplyOrderVO);

    ResultBean<Void> updateCreditApplyOrder(AppCreditApplyOrderVO creditApplyOrderVO);

    ResultBean<AppCreditRecordVO> creditRecordDetail(Long orderId, Byte type);

    ResultBean<Void> creditRecord(CreditRecordParam creditRecordParam);

    ResultBean<CustDetailVO> customerDetail(Long orderId);

    ResultBean<Void> updateCustomer(CustDetailVO custDetailVO);

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(Long orderId);

    ResultBean<Void> createOrUpdateLoanCarInfo(AppLoanCarInfoParam appLoanCarInfoParam);

    ResultBean<AppLoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId);

    ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam);

    ResultBean<Void> createOrUpdateLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<AppLoanHomeVisitVO> homeVisitDetail(Long orderId);

    ResultBean<Void> createOrUpdateLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam);

    ResultBean<Void> infoSupplement(AppInfoSupplementParam infoSupplementParam);
}
