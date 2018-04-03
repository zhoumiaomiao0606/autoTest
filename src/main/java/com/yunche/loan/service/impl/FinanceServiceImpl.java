package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.FinanceService;
import com.yunche.loan.service.LoanProcessLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

@Service
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanProcessLogService loanProcessLogService;

    @Override
    public RecombinationVO detail(Long orderId) {
        FinanceVO financeVO = loanQueryDOMapper.selectFinance(orderId);

        if (financeVO != null) {
            LoanProcessLogDO loanProcessLog = loanProcessLogService.getLoanProcessLog(orderId, TELEPHONE_VERIFY.getCode());
            if (loanProcessLog != null) {
                financeVO.setVerify_status(loanProcessLog.getAction() == null ? "-1" : String.valueOf(loanProcessLog.getAction()));
                financeVO.setVerify_report(loanProcessLog.getInfo());
            }
        }
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO<FinanceVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(financeVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }
}
