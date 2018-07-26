package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.BankRecordQueryDOMapper;
import com.yunche.loan.service.InsuranceUrgeDistributeService;
import org.springframework.beans.factory.annotation.Autowired;

public class InsuranceUrgeDistributeServiceImpl implements InsuranceUrgeDistributeService {

    @Autowired
    private BankRecordQueryDOMapper bankRecordQueryDOMapper;
    @Override
    public ResultBean list(Integer pageIndex, Integer pageSize) {
//        bankRecordQueryDOMapper.
        return null;
    }
}
