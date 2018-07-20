package com.yunche.loan.service;


import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;


public interface LoanQueryService {

    UniversalCustomerDetailVO universalCustomerDetail(Long customerId);

    String selectTelephoneVerifyLevel();

    Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId);

}
