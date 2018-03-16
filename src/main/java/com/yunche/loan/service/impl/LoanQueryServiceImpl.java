package com.yunche.loan.service.impl;

import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class LoanQueryServiceImpl implements LoanQueryService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public UniversalCustomerDetailVO universalCustomerDetail(Long customerId) {
        return loanQueryDOMapper.selectUniversalCustomerDetail(customerId);
    }
}
