package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.ApprovalInfoUtil;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.ApprovalInfoVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
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

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private ApprovalInfoUtil approvalInfoUtil;

    @Override
    public UniversalCustomerDetailVO universalCustomerDetail(Long customerId) {
        UniversalCustomerDetailVO VO = loanQueryDOMapper.selectUniversalCustomerDetail(customerId);
        if(VO!=null){
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByCustomerId(customerId);
            Long orderId = loanOrderDO == null?null:loanOrderDO.getId();
            if(orderId!=null){
                ApprovalInfoVO bank = approvalInfoUtil.getApprovalInfoVO(orderId,LoanProcessEnum.BANK_CREDIT_RECORD.getCode());
                ApprovalInfoVO scociety = approvalInfoUtil.getApprovalInfoVO(orderId,LoanProcessEnum.SOCIAL_CREDIT_RECORD.getCode());
                if(bank!=null){
                    VO.setBank_addition(bank.getInfo());
                }
                if(scociety!=null){
                    VO.setSociety_addition(scociety.getInfo());
                }
            }
        }
        return VO;
    }
}
