package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_CREDIT_RECORD;
import static com.yunche.loan.config.constant.LoanProcessEnum.SOCIAL_CREDIT_RECORD;

@Service
public class LoanQueryServiceImpl implements LoanQueryService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanProcessLogService loanProcessLogService;

    @Override
    public UniversalCustomerDetailVO universalCustomerDetail(Long customerId) {
        UniversalCustomerDetailVO VO = loanQueryDOMapper.selectUniversalCustomerDetail(customerId);
        if (VO != null) {
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByCustomerId(customerId);
            Long orderId = loanOrderDO == null ? null : loanOrderDO.getId();
            if (orderId != null) {
                LoanProcessLogDO bank = loanProcessLogService.getLoanProcessLog(orderId, BANK_CREDIT_RECORD.getCode());
                LoanProcessLogDO scociety = loanProcessLogService.getLoanProcessLog(orderId, SOCIAL_CREDIT_RECORD.getCode());
                if (bank != null) {
                    VO.setBank_addition(bank.getInfo());
                }
                if (scociety != null) {
                    VO.setSociety_addition(scociety.getInfo());
                }
            }
        }
        return VO;
    }
}
