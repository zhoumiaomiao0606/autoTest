package com.yunche.loan.service.impl;

import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.RepaymentPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service
public class RepaymentPlanServiceImpl implements RepaymentPlanService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRepayments(loanQueryDOMapper.selectUniversalLoanRepaymentPlan(orderId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }
}
