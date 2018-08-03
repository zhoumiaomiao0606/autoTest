package com.yunche.loan.service;


import com.yunche.loan.domain.vo.BankInterfaceSerialReturnVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import org.apache.ibatis.annotations.Param;


public interface LoanQueryService {

    boolean selectCheckOrderInBankInterfaceSerial(Long orderId,String transCode);

    UniversalCustomerDetailVO universalCustomerDetail(Long customerId);

    String selectTelephoneVerifyLevel();

    Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId,String transCode);

    Integer selectBankOpenCardStatusByOrderId(Long orderId);

    void checkBankInterFaceSerialStatus(Long customerId,String transCode);

    public String selectLastBankInterfaceSerialNoteByTransCode(Long customerId,String transCode);

    public BankInterfaceSerialReturnVO selectLastBankInterfaceSerialByTransCode(Long customerId, String transCode);

}
