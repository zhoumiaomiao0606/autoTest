package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.AppBusinessInfoVO;
import com.yunche.loan.domain.vo.AppCustomerInfoVO;
import com.yunche.loan.domain.vo.AppInsuranceInfoVO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;


/**
 * @author liuzhe
 * @date 2018/3/5
 */
public interface AppLoanOrderService {

    ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<AppCreditApplyVO> createCreditApplyOrder(AppCustomerParam creditApplyOrderVO);

    ResultBean<AppCustDetailVO> customerDetail(Long orderId);

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<AppLoanHomeVisitVO> homeVisitDetail(Long orderId);

    ResultBean<Void> createOrUpdateLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam);

    ResultBean<Void> infoSupplementUpload(AppInfoSupplementParam infoSupplementParam);

    ResultBean<Long> createBaseInfo(AppLoanBaseInfoParam param);

    ResultBean<Void> updateBaseInfo(AppLoanBaseInfoDetailParam param);

    ResultBean<Long> addRelaCustomer(AppCustomerParam param);

    ResultBean<Void> updateCustomer(AppCustomerParam param);

    ResultBean<Long> delRelaCustomer(Long customerId);

    ResultBean<Long> createLoanCarInfo(AppLoanCarInfoParam appLoanCarInfoParam);

    ResultBean<Void> updateLoanCarInfo(AppLoanCarInfoParam appLoanCarInfoParam);

    ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(Long orderId);

    ResultBean<Long> createLoanFinancialPlan(AppLoanFinancialPlanParam param);

    ResultBean<Void> updateLoanFinancialPlan(AppLoanFinancialPlanParam param);

    ResultBean<AppLoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId);

    ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam);

    ResultBean<AppInfoSupplementVO> infoSupplementDetail(Long orderId);

    ResultBean<AppCustomerInfoVO> customerInfo(Long orderId);

    ResultBean<AppBusinessInfoVO> businessInfo(Long orderId);

    ResultBean<AppInsuranceInfoVO> insuranceInfo(Long orderId);
}
