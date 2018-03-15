package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.AppBusinessInfo;
import com.yunche.loan.domain.entity.AppCustomerInfo;
import com.yunche.loan.domain.entity.AppInsuranceInfo;
import com.yunche.loan.domain.entity.AppLoanCustomerVO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppCustomerQuery;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
public interface AppLoanOrderService {
    ResultBean<List<AppLoanOrderVO>> query(AppLoanOrderQuery query);

    ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<AppCreditApplyVO> createCreditApplyOrder(AppCustomerParam creditApplyOrderVO);

//    ResultBean<AppCreditRecordVO> creditRecordDetail(Long orderId, Byte type);

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

    ResultBean<List<AppLoanOrderVO>> listCreditNotEnding(BaseQuery query);

    ResultBean<AppInfoSupplementVO> infoSupplementDetail(Long orderId);

    ResultBean<List<AppLoanCustomerVO>> customerQuery(AppCustomerQuery query);

    ResultBean<AppCustomerInfo> customerInfo(Long orderId);

    ResultBean<AppBusinessInfo> businessInfo(Long orderId);

    ResultBean<AppInsuranceInfo> insuranceInfo(Long orderId);
}