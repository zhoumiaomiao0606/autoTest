package com.yunche.loan.service.impl;


import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.FinancialSchemeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FinancialSchemeServiceImpl implements FinancialSchemeService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO<FinancialSchemeVO> recombinationVO = new RecombinationVO<FinancialSchemeVO>();
        recombinationVO.setInfo(financialSchemeVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }
}
