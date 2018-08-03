package com.yunche.loan.service;

import com.yunche.loan.domain.vo.BankInterfaceSerialReturnVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.domain.vo.UniversalInfoSupplementVO;

import java.util.List;

public interface LoanQueryService {

    boolean selectCheckOrderInBankInterfaceSerial(Long orderId, String transCode);

    UniversalCustomerDetailVO universalCustomerDetail(Long customerId);

    String selectTelephoneVerifyLevel();

    Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId, String transCode);

    Integer selectBankOpenCardStatusByOrderId(Long orderId);

    void checkBankInterFaceSerialStatus(Long customerId, String transCode);

    String selectLastBankInterfaceSerialNoteByTransCode(Long customerId, String transCode);

    BankInterfaceSerialReturnVO selectLastBankInterfaceSerialByTransCode(Long customerId, String transCode);

    UniversalInfoSupplementVO detail(Long infoSupplementId);

    List<UniversalInfoSupplementVO> history(Long orderId);
}
