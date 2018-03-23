package com.yunche.loan.service.impl;

import com.yunche.loan.config.common.ApprovalInfoUtil;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.FinanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private ApprovalInfoUtil approvalInfoUtil;

    @Override
    public RecombinationVO detail(Long orderId) {
        FinanceVO financeVO = loanQueryDOMapper.selectFinance(orderId);

        if(financeVO!=null){
            ApprovalInfoVO V = approvalInfoUtil.getApprovalInfoVO(orderId,LoanProcessEnum.TELEPHONE_VERIFY.getCode());
            if(V!=null){
                financeVO.setVerify_status(V.getAction()==null?"-1":String.valueOf(V.getAction()));
                financeVO.setVerify_report(V.getInfo());
            }
        }
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO<FinanceVO> recombinationVO = new RecombinationVO<FinanceVO>();
        recombinationVO.setInfo(financeVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }
}
