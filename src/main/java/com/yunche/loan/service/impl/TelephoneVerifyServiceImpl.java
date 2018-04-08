package com.yunche.loan.service.impl;

import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.TelephoneVerifyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class TelephoneVerifyServiceImpl implements TelephoneVerifyService {
    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setCredits(loanQueryDOMapper.selectUniversalCreditInfo(orderId));
        recombinationVO.setHome(loanQueryDOMapper.selectUniversalHomeVisitInfo(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify",orderId));
        recombinationVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderId(orderId));
        recombinationVO.setSupplement(loanQueryDOMapper.selectUniversalSupplementInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecord(orderId));
        recombinationVO.setCustomers(customers);

        return recombinationVO;
    }
}
