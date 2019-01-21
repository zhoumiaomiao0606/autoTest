package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BankInterfaceSerialDOMapper {

    int deleteByPrimaryKey(String serialNo);

    int insertSelective(BankInterfaceSerialDO record);

    BankInterfaceSerialDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(BankInterfaceSerialDO record);

    boolean checkRequestBussIsSucessByTransCodeOrderId(@Param("customerId") Long customerId,
                                                       @Param("transCode") String transCode);

    BankInterfaceSerialDO selectByCustomerIdAndTransCode(@Param("customerId") Long customerId,
                                                         @Param("transCode") String transCode);

    /**
     * [银行征信] - 推送失败的  所有订单-推送失败详情
     *
     * @return
     */
    List<BankInterfaceSerialDO> listOfBankCreditRecordPushFailed();

    List<String> videoPush();
}