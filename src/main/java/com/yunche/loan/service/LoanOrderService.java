package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanOrderService {

    ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId);

    ResultBean<CreditRecordVO> creditRecordDetail(Long orderId, Byte type);

    ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId);

    ResultBean<LoanHomeVisitVO> homeVisitDetail(Long orderId);

    ResultBean<Long> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam);

    ResultBean<String> createCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderParam param);

    ResultBean<Long> createLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam);

    ResultBean<Long> createCreditRecord(CreditRecordParam creditRecordParam);

    ResultBean<Long> updateCreditRecord(CreditRecordParam creditRecordParam);

    ResultBean<LoanSimpleInfoVO> simpleInfo(Long orderId);

    ResultBean<List<LoanSimpleCustomerInfoVO>> simpleCustomerInfo(Long orderId);


    ResultBean createCreditDownreport(LoanCreditExportQuery loanCreditExportQuery);

    ResultBean picCheck();


    RecombinationVO<UniversalInfoVO> newCreditRecordDetail(Long orderId);
}
