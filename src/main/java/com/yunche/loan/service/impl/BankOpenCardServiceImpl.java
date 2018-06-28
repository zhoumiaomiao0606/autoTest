package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

public class BankOpenCardServiceImpl implements BankOpenCardService{

    @Autowired
    LoanQueryService loanQueryService;
    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;
    @Autowired
    BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;
    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        RecombinationVO recombinationVO = new RecombinationVO();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, VALID_STATUS);
        Long customerId = loanOrderDO.getLoanCustomerId();
        UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryService.universalCustomerDetail(customerId);

        BankInterfaceSerialDO serialDO = bankInterfaceSerialDOMapper.selectByCustomerIdAndTransCode(customerId, IDict.K_API.CREDITCARDAPPLY);

        recombinationVO.setInfo(universalCustomerDetailVO);
        recombinationVO.setBankSerial(serialDO);

        return null;
    }
}
