package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.TelephoneVerifyVO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanOrderService {

    ResultBean<List<LoanOrderVO>> query(LoanOrderQuery query);

    ResultBean<List<LoanOrderVO>> multipartQuery(LoanOrderQuery query);

    ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<CreditRecordVO> creditRecordDetail(Long orderId, Byte type);

    ResultBean<CustDetailVO> customerDetail(Long orderId);

    ResultBean<Void> updateCustomer(AllCustDetailParam allCustDetailParam);

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId);

    ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId);

    ResultBean<Long> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanVO);

    ResultBean<LoanHomeVisitVO> homeVisitDetail(Long orderId);

    ResultBean<Long> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam);

    ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam);

    ResultBean<Void> infoSupplementUpload(InfoSupplementParam infoSupplementParam);

    ResultBean<String> createCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Long> createLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<Long> addRelaCustomer(CustomerParam param);

    ResultBean<Long> delRelaCustomer(Long customerId);

    ResultBean<Long> createCreditRecord(CreditRecordParam creditRecordParam);

    ResultBean<Long> updateCreditRecord(CreditRecordParam creditRecordParam);

    ResultBean<LoanSimpleInfoVO> simpleInfo(Long orderId);

    ResultBean<List<LoanSimpleCustomerInfoVO>> simpleCustomerInfo(Long orderId);

    ResultBean<InfoSupplementVO> infoSupplementDetail(Long orderId);
    /**
     * 提车资料查询
     */
    ResultBean<VehicleInfoVO> vehicleInformationQuery(Long orderId);
}
